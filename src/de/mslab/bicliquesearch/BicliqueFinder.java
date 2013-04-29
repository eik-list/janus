package de.mslab.bicliquesearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.mslab.bicliquesearch.helpers.DefaultBicliqueRater;
import de.mslab.core.Biclique;
import de.mslab.core.ByteArray;
import de.mslab.core.Differential;
import de.mslab.diffbuilder.BicliqueDifferentialBuilder;
import de.mslab.diffbuilder.DifferenceIterator;
import de.mslab.diffbuilder.DifferentialBuilder;
import de.mslab.utils.Logger;

/**
 * Searches for bicliques in a round interval of a given cipher.
 */
public class BicliqueFinder {
	
	private BicliqueFinderContext context;
	private volatile List<Biclique> bicliques;
	
	private DifferentialBuilder differentialBuilder = new BicliqueDifferentialBuilder();
	private volatile List<Differential> deltaDifferentials;
	private List<DeltaThread> deltaThreads;
	private List<NablaThread> nablaThreads;
	
	private volatile boolean hasFoundBiclique = false;
	private volatile int numDeltaThreadsRunning;
	private volatile int numNablaThreadsRunning;
	private volatile int numNablaDifferentialsMatched;
	private long startTime;
	
	private ByteArray initialKey;
	private Logger logger = Logger.getLogger();
	private volatile int maxBicliqueScore;
	private Object mutex = new Object();
	
	public BicliqueFinder() {
		
	}
	
	/**
	 * Returns a list of bicliques found in the last search.
	 */
	public List<Biclique> getBicliques() {
		return bicliques;
	}
	
	/**
	 * Returns the BicliqueFinderContext.
	 */
	public BicliqueFinderContext getContext() {
		return context;
	}
	
	public void setContext(BicliqueFinderContext context) {
		this.context = context;
	}
	
	/**
	 * Returns the logger, which is used to output debug information.
	 */
	public Logger getLogger() {
		return logger;
	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	/**
	 * Starts the process of finding bicliques for a given cipher in the round interval, which is defined
	 * by the values fromRound to toRound of the BicliqueFinderContext.
	 * Before calling this method, specify a BicliqueFinderContext, and pass using the method setLogger().
	 */
	public void findBicliques() {
		logStart();
		initializeDifferenceBuilder();
		initializeBicliqueRater();
		determineNumIterations();
		computeInitialKey();
		
		for (context.iterationIndex = 0; context.iterationIndex < context.numIterations; context.iterationIndex++) {
			reset();
			computeDeltaDifferentialsWithMultipleThreads();
			logDeltaProgress();
			
			initializeDifferenceBuilder();
			computeNablaDifferentialsWithMultipleThreads();
			
			if (hasFoundBiclique && context.stopAfterFoundFirstBiclique) {
				break;
			}
			
			logNablaProgress();
		}
	}
	
	/**
	 * Clears the internal lists of differentials, bicliques, and threads used for 
	 * biclique finding to clean up memory.
	 */
	public void tearDown() {
		if (deltaDifferentials == null) {
			deltaDifferentials = Collections.synchronizedList(new ArrayList<Differential>(context.numThreads));
		} else {
			deltaDifferentials.clear();
		}
		
		if (bicliques == null) {
			bicliques = Collections.synchronizedList(new ArrayList<Biclique>());
		} else {
			bicliques.clear();
		}
		
		if (deltaThreads == null) {
			deltaThreads = new ArrayList<DeltaThread>(context.numThreads);
		} else {
			deltaThreads.clear();
		}
		
		if (deltaThreads == null) {
			nablaThreads = new ArrayList<NablaThread>(context.numThreads);
		} else {
			deltaThreads.clear();
		}
		
		numDeltaThreadsRunning = 0;
		numNablaThreadsRunning = 0;
		hasFoundBiclique = false;
	}
	
	/**
	 * Computes the initial key K[0,0], which is used as the first secret key in all computations of delta and
	 * nabla differentials during the biclique search.
	 */
	private void computeInitialKey() {
		initialKey = new ByteArray(context.cipher.getKeySize());
		context.cipher.setKey(initialKey);
		initialKey = context.cipher.getExpandedKey();
	}
	
	private void computeDeltaDifferentialsWithMultipleThreads() {
		prepareDeltaThreads();
		startDeltaThreads();
		
		while(numDeltaThreadsRunning > 0) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		
		stopDeltaThreads();
	}
	
	private void computeNablaDifferentialsWithMultipleThreads() {
		prepareNablaThreads();
		startNablaThreads();
		
		while(numNablaThreadsRunning > 0) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		
		stopNablaThreads();
	}
	
	private void determineNumIterations() {
		long numDifferentials = context.differenceBuilder.getNumResults();
		long numRounds = context.toRound - context.fromRound + 1;
		
		long numBytesForKeys = numRounds * context.cipher.getKeySize();
		long numBytesForStates = (numRounds + 1) * context.cipher.getStateSize();
		long numBytesForIntermediateStates = numRounds * context.cipher.getStateSize();
		
		long numBytesRequiredPerDifferential = 16 * (numBytesForKeys + numBytesForStates + numBytesForIntermediateStates); 
		long numBytesRequired = numDifferentials * numBytesRequiredPerDifferential;
		
		Runtime runtime = Runtime.getRuntime();
		long maxNumBytesMemoryUsable = (runtime.maxMemory() - runtime.freeMemory()) * 7/8; // use 7/8 of free RAM
		logUsableMemory(maxNumBytesMemoryUsable);
		
		if (numBytesRequired < maxNumBytesMemoryUsable) {
			context.numDifferentialsPerIteration = numDifferentials;
			context.numIterations = 1;
		} else {
			context.numDifferentialsPerIteration = (int)((double)maxNumBytesMemoryUsable / (double)numBytesRequiredPerDifferential);
			context.numIterations = (int)(Math.ceil((double)numDifferentials / (double)context.numDifferentialsPerIteration));			
		}
	}
	
	private void initializeBicliqueRater() {
		if (context.bicliqueRater == null) {
			context.bicliqueRater = new DefaultBicliqueRater();
		}
		
		maxBicliqueScore = Integer.MIN_VALUE;
	}
	
	private void initializeDifferenceBuilder() {
		context.numDifferentialsToTest = context.differenceBuilder.initializeAndGetNumDifferences(
			context.dimension, 
			context.cipher.getKeySize()
		);
	}
	
	private void logDeltaProgress() {
		long numDifferentials = (context.iterationIndex + 1) * context.numDifferentialsPerIteration;
		long numTotalDifferentials = context.differenceBuilder.getNumResults();
		logger.info("{0}/{1} Delta differentials tested.", numDifferentials, numTotalDifferentials);
	}
	
	private void logNablaProgress() {
		long numDifferentials = (context.iterationIndex + 1) * context.numDifferentialsPerIteration;
		long numTotalDifferentials = context.differenceBuilder.getNumResults();
		logger.info("{0}/{1} Nabla differentials tested.", numDifferentials, numTotalDifferentials);
	}
	
	private void logNablaProgressInIteration() {
		double elapsedTime = (double)(System.nanoTime() - startTime) / 1000000000.0;
		double elapsedPart = (double)numNablaDifferentialsMatched / (double)context.numDifferentialsToTest;
		long expectedTime = (long)(elapsedTime / (double)elapsedPart);
		elapsedTime = (long)elapsedTime;
		
		logger.info("Computed and matched {0}/{1} nabla differences. Elapsed {2}/{3}s", 
			numNablaDifferentialsMatched, context.numDifferentialsToTest, elapsedTime, expectedTime
		);
	}
	
	private void logStart() {
		logger.info("Searching on {0} for round(s) [{1} - {2}]", context.cipher.getName(), context.fromRound, context.toRound);
	}
	
	private void logUsableMemory(long maxNumBytesMemoryUsable) {
		long numMegaBytesUsable = (long)((double)maxNumBytesMemoryUsable / (double)(1 << 20));
		logger.info("{0} MB memory available", numMegaBytesUsable);
	}
	
	private void prepareDeltaThreads() {
		DeltaThread thread;
		
		int offset = (int)(Math.floor(context.numDifferentialsPerIteration / context.numThreads));
		long startIndex;
		long endIndex;
		
		for (int i = 0; i < context.numThreads; i++) {
			startIndex = i * offset;
			endIndex = (i + 1) * offset;
			
			if (i + 1 == context.numThreads) {
				endIndex = context.numDifferentialsPerIteration;
			}
			
			if (deltaThreads.size() < context.numThreads) {
				thread = new DeltaThread(startIndex, endIndex);
				deltaThreads.add(thread);
			} else {
				thread = deltaThreads.get(i);
				thread.startIndex = startIndex;
				thread.endIndex = endIndex;
			}
		}
	}
	
	private void prepareNablaThreads() {
		NablaThread thread;
		int offset = (int)(Math.floor(context.numDifferentialsToTest / context.numThreads));
		long startIndex;
		long endIndex;
		
		for (int i = 0; i < context.numThreads; i++) {
			startIndex = i * offset;
			endIndex = (i + 1) * offset;

			if (i + 1 == context.numThreads) {
				endIndex = context.numDifferentialsPerIteration;
			}
			
			if (nablaThreads.size() < context.numThreads) {
				thread = new NablaThread(startIndex, endIndex);
				nablaThreads.add(thread);
			} else {
				thread = nablaThreads.get(i);
				thread.startIndex = startIndex;
				thread.endIndex = endIndex;
			}
		}
	}
	
	private void reset() {
		// clear lists and reset flags
		if (deltaDifferentials == null) {
			deltaDifferentials = Collections.synchronizedList(new ArrayList<Differential>(context.numThreads));
		} else {
			deltaDifferentials.clear();
		}
		
		if (bicliques == null) {
			bicliques = Collections.synchronizedList(new ArrayList<Biclique>());
		} else {
			bicliques.clear();
		}
		
		hasFoundBiclique = false;
		numNablaDifferentialsMatched = 0;
		startTime = System.nanoTime();
		
		// init dependencies
		differentialBuilder.setCipher(context.cipher);
		
		// create threads
		if (deltaThreads == null || deltaThreads.size() != context.numThreads) {
			stopDeltaThreads();
			deltaThreads = null;
			deltaThreads = new ArrayList<DeltaThread>(context.numThreads);
		}
		
		if (nablaThreads == null || nablaThreads.size() != context.numThreads) {
			stopNablaThreads();
			nablaThreads = null;
			nablaThreads = new ArrayList<NablaThread>(context.numThreads);
		}
	}
	
	private void startDeltaThreads() {
		numDeltaThreadsRunning = 0;
		
		if (deltaThreads != null) {
			for (int i = 0; i < deltaThreads.size(); i++) {
				DeltaThread thread = deltaThreads.get(i);
				thread.start();
			}
		}
	}
	
	private void startNablaThreads() {
		numNablaThreadsRunning = 0;
		
		if (nablaThreads != null) {
			for (int i = 0; i < nablaThreads.size(); i++) {
				NablaThread thread = nablaThreads.get(i);
				thread.start();
			}
		}
	}
	
	private void stopDeltaThreads() {
		if (deltaThreads != null) {
			for (int i = 0; i < deltaThreads.size(); i++) {
				DeltaThread thread = deltaThreads.get(i);
				thread.stop();
			}
		}
		
		numDeltaThreadsRunning = 0;
	}
	
	private void stopNablaThreads() {
		if (nablaThreads != null) {
			for (int i = 0; i < nablaThreads.size(); i++) {
				NablaThread thread = nablaThreads.get(i);
				thread.stop();
			}
		}
		
		numNablaThreadsRunning = 0;
	}
	
	/**
	 * Thread to compute delta (= forward) differentials. 
	 */
	private class DeltaThread implements Runnable {
		
		private long startIndex;
		private long endIndex;
		private Thread thread = null;
		private volatile boolean running;
		
		public DeltaThread(long startIndex, long endIndex) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}
		
		public void start() {
			if (!running) {
				thread = new Thread(this);
				thread.start();
				running = true;
				
				synchronized (mutex) {
					numDeltaThreadsRunning++;
				}
			}
		}
		
		public void run() {
			DifferenceIterator keyDifferencesIterator;
			Differential deltaDifferential;
			
			for (long i = startIndex; i < endIndex; i++) {
				keyDifferencesIterator = context.differenceBuilder.next();
				deltaDifferential = differentialBuilder.computeForwardDifferential(
					context.fromRound, context.toRound, keyDifferencesIterator, initialKey, context.fromRound
				);
				
				synchronized (mutex) {
					deltaDifferentials.add(deltaDifferential);
				}
				
				if (thread.isInterrupted()) {
					break;
				}
			}

			stop();
		}
		
		public void stop() {
			if (running) {
				thread.interrupt();
				thread = null;
				running = false;
				
				synchronized (mutex) {
					numDeltaThreadsRunning--;
				}
			}
		}
		
	}
	
	/**
	 * Computes nabla differentials.
	 */
	private class NablaThread implements Runnable {

		private long startIndex;
		private long endIndex;
		private Thread thread = null;
		private volatile boolean running;
		
		public NablaThread(long startIndex, long endIndex) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}
		
		public void start() {
			if (!running) {
				thread = new Thread(this);
				thread.start();
				running = true;
				
				synchronized (mutex) {
					numNablaThreadsRunning++;
				}
			}
		}
		
		public void run() {
			DifferenceIterator keyDifferencesIterator;
			Differential nablaDifferential;
			Differential deltaDifferential;
			Biclique biclique;
			
			int numDeltaDifferentials = deltaDifferentials.size();
			int score;
			
			nabla: for (long j = startIndex; j < endIndex; j++) {
				keyDifferencesIterator = context.differenceBuilder.next();
				nablaDifferential = differentialBuilder.computeBackwardDifferential(
					context.fromRound, context.toRound, keyDifferencesIterator, initialKey, context.toRound
				);
				
				for (int i = 0; i < numDeltaDifferentials; i++) {
					deltaDifferential = deltaDifferentials.get(i);
					
					if (!context.comparator.shareActiveNonLinearOperations(deltaDifferential, nablaDifferential)) {
						biclique = new Biclique(deltaDifferential, nablaDifferential);
						biclique.cipherName = context.cipher.getName();
						biclique.dimension = context.dimension;
						
						score = context.bicliqueRater.determineScoreForBiclique(biclique);
						
						synchronized (mutex) {
							if (score > maxBicliqueScore) {
								bicliques.clear();
							}
							
							if (score >= maxBicliqueScore) {
								maxBicliqueScore = score;
								bicliques.add(biclique);
								hasFoundBiclique = true;
							}
							
							if (context.stopAfterFoundFirstBiclique) {
								break nabla;
							}
						}
					} else if (hasFoundBiclique && context.stopAfterFoundFirstBiclique) {
						break nabla;
					}
				}
				
				if (thread.isInterrupted()) {
					break nabla;
				}
				
				numNablaDifferentialsMatched++;
				
				if (numNablaDifferentialsMatched % context.logInterval == 0) {
					logNablaProgressInIteration();
				}
			}
			
			stop();
		}
		
		public void stop() {
			if (running) {
				thread.interrupt();
				thread = null;
				running = false;
				
				synchronized (mutex) {
					numNablaThreadsRunning--;
				}
			}
		}
			
	}
	
}





















