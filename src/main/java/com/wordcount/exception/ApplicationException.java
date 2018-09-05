package com.wordcount.exception;

/**
 * RuntimeException that represents the Application specific exception.
 * It is thrown explicitly by the application.
 *
 */
public class ApplicationException extends RuntimeException {
	private static final long serialVersionUID = 8010610033104092220L;

	public ApplicationException() {
	}

	public ApplicationException(String message, Throwable ex) {
		super(message, ex);
	}

	public ApplicationException(String message) {
		super(message);
	}

	public ApplicationException(Throwable cause) {
		super(cause);
	}

	@Override
	public String toString() {
		return new StringBuilder(
				this.getClass().getName()).append(" - ").
				append(this.getMessage()).toString();
	}

}
