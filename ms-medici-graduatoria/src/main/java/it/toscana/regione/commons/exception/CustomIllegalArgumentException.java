package it.toscana.regione.commons.exception;

import it.toscana.regione.commons.exception.base.ExternalAbstractException;

public class CustomIllegalArgumentException extends ExternalAbstractException {

	private static final long		serialVersionUID	= -5640656650824202417L;
	protected static final String	CODE				= "400";
	private static final String		DESC				= "ILLEGAL_ARGUMENT";

	public CustomIllegalArgumentException() {
	}

	public CustomIllegalArgumentException(String message) {
		super(message);
	}

	public CustomIllegalArgumentException(String message, Object... params) {
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
