package de.mslab.rendering;

import java.io.IOException;

import com.itextpdf.text.DocumentException;

import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.core.Biclique;

public interface IBicliqueRenderer {
	
	public abstract DifferentialRenderer getDifferentialRenderer();
	
	/**
	 * Returns the padding in pixels from top and left to the leftmost differential. 
	 */
	public abstract int getPadding();
	
	/**
	 * Sets the padding in pixels.
	 */
	public abstract void setPadding(int padding);
	
	public abstract void setDifferentialRenderer(DifferentialRenderer differentialRenderer);
	
	public abstract void renderBiclique(String pathname, Biclique biclique, RoundBasedBlockCipher cipher)
		throws IOException, DocumentException;
	
}