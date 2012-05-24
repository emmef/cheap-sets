package org.emmef.cheapsets;

public class ElementNotInSubsetException extends IllegalArgumentException {
	public ElementNotInSubsetException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
