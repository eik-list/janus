package de.mslab.diffbuilder;

import de.mslab.core.ByteArray;
import de.mslab.core.Differential;

public class MatchingDifferentialBuilder extends AbstractDifferentialBuilder {
	
	public MatchingDifferentialBuilder() {
		super();
	}
	
	public synchronized Differential computeBackwardDifferentialFromMiddle(int fromRound, int toRound,
		ByteArray firstStartingState, DifferenceIterator stateBitsIterator, ByteArray expandedKey) {

		ByteArray secondStartingState;
		
		Differential accumulated = new Differential(fromRound, toRound);
		Differential current = new Differential(fromRound, toRound);
		Differential first = new Differential(fromRound, toRound);
		
		fillDifferential(accumulated);
		cipher.setExpandedKey(expandedKey);
		computeBackwardDifferential(first, firstStartingState);
		
		stateBitsIterator.reset();
		
		while(stateBitsIterator.hasNext()) {
			secondStartingState = stateBitsIterator.next();
			computeBackwardDifferential(current, secondStartingState);
			current.xor(first);
			accumulated.or(current);
		}
		
		return accumulated;
	}
	
	public synchronized Differential computeForwardDifferentialFromMiddle(int fromRound, int toRound, 
		ByteArray firstStartingState, DifferenceIterator stateBitsIterator, ByteArray expandedKey) {
		
		ByteArray secondStartingState;
		
		Differential accumulated = new Differential(fromRound, toRound);
		Differential current = new Differential(fromRound, toRound);
		Differential first = new Differential(fromRound, toRound);
		
		fillDifferential(accumulated);
		cipher.setExpandedKey(expandedKey);
		computeForwardDifferential(first, firstStartingState);

		stateBitsIterator.reset();
		
		while(stateBitsIterator.hasNext()) {
			secondStartingState = stateBitsIterator.next();
			computeForwardDifferential(current, secondStartingState);
			current.xor(first);
			accumulated.or(current);
		}
		
		return accumulated;
	}
	
}
