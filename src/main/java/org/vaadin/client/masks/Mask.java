package org.vaadin.client.masks;

public interface Mask {
	boolean isValid(char character);
	char getChar(char character);
}
