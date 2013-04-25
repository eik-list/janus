package de.mslab.diffbuilder;

import de.mslab.core.ByteArray;

public interface DifferenceIterator {
	ByteArray next();
	boolean hasNext();
	void reset();
}
