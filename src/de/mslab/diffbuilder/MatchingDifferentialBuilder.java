package de.mslab.diffbuilder;

import de.mslab.core.ByteArray;
import de.mslab.core.Differential;

public class MatchingDifferentialBuilder extends AbstractDifferentialBuilder {
	
	public MatchingDifferentialBuilder() {
		super();
	}
	
	public synchronized Differential computeBackwardDifferentialFromMiddle(int fromRound, int toRound,
		ByteArray firstStartingState, DifferenceIterator stateBitsIterator, ByteArray expandedKey) {
		
		return computeDifferentialFromMiddle(
			fromRound, toRound, firstStartingState, stateBitsIterator, expandedKey, 
			backwardDifferentialsHelper
		);
	}
	
	public synchronized Differential computeForwardDifferentialFromMiddle(int fromRound, int toRound, 
		ByteArray firstStartingState, DifferenceIterator stateBitsIterator, ByteArray expandedKey) {
		
		return computeDifferentialFromMiddle(
			fromRound, toRound, firstStartingState, stateBitsIterator, expandedKey, 
			forwardDifferentialsHelper
		);
	}
	
	protected Differential computeDifferentialFromMiddle(int fromRound, int toRound, 
		ByteArray firstStartingState, DifferenceIterator stateBitsIterator, ByteArray expandedKey, 
		DifferentialsHelper differentialsHelper) {
		
		ByteArray secondStartingState;
		Differential accumulated = new Differential(fromRound, toRound);
		Differential current = new Differential(fromRound, toRound);
		Differential first = new Differential(fromRound, toRound);
		
		fillDifferential(accumulated);
		cipher.setExpandedKey(expandedKey);
		differentialsHelper.computeDifferential(first, firstStartingState);
		
		stateBitsIterator.reset();
		
		while(stateBitsIterator.hasNext()) {
			secondStartingState = stateBitsIterator.next();
			differentialsHelper.computeDifferential(current, secondStartingState);
			current.xor(first);
			accumulated.or(current);
		}
		
		return accumulated;
	}
	
}
