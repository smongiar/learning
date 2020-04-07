package it.toscana.regione.commons.exception;

import it.toscana.regione.commons.exception.base.ExternalAbstractException;

public class GenericException extends ExternalAbstractException {

	protected static final String	CODE				= "500";
	private static final String		DESC				= "INTERNAL_SERVER_ERROR";
	private static final long		serialVersionUID	= -4011620359874428488L;

	public GenericException() {
	}

	public GenericException(String message, Throwable ex) {
		super(message);
		if (ex != null)
			initCause(ex);
	}

	public GenericException(Throwable ex) {
		super(ex);
	}

	public GenericException(String message) {
		super(message);
	}

	public GenericException(String message, Object... params) {
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
