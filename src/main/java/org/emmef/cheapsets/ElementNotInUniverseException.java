package org.emmef.cheapsets;

/**
 * Exception that is thrown when a mapping or element is added in {@link UniverseBasedSet}
 */
public class ElementNotInUniverseException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;
	
	public ElementNotInUniverseException(String message) {
		super(message);
	}
}
