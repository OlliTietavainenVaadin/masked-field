package org.vaadin.client.masks;

public class NumericMask extends AbstractMask {
	
	public boolean isValid(char character) {
		return Character.isDigit(character);
	}

}
