package it.toscana.regione.commons.exception;

import it.toscana.regione.commons.exception.base.ExternalAbstractException;

public class ConflictException extends ExternalAbstractException {

	private static final long		serialVersionUID	= -6846648318981093332L;
	protected static final String	CODE				= "409";
	private static final String		DESC				= "CONFLICT";

	public ConflictException() {
	}

	public ConflictException(String message) {
		super(message);
	}

	public ConflictException(String message, Object... params) {
		super(message != null ? String.format(message, params) : "");
	}

	@Override
	public String getCode() {
		return CODE;
	}

	@Override
	public String getDescription() {
		return DESC;
	}
}
