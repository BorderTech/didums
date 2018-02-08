package com.github.bordertech.didums;

/**
 * Factory Exception.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class FactoryException extends RuntimeException {

	/**
	 * Creates a FactoryException with the specified message.
	 *
	 * @param msg the message.
	 */
	public FactoryException(final String msg) {
		super(msg);
	}

	/**
	 * Creates a FactoryException with the specified message and cause.
	 *
	 * @param msg the message.
	 * @param throwable the cause of the exception.
	 */
	public FactoryException(final String msg, final Throwable throwable) {
		super(msg, throwable);
	}

	/**
	 * Creates a FactoryException with the specified cause.
	 *
	 * @param throwable the cause of the exception.
	 */
	public FactoryException(final Throwable throwable) {
		super(throwable);
	}
}
