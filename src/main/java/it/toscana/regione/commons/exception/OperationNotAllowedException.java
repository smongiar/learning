package it.toscana.regione.commons.exception;

import it.toscana.regione.commons.exception.base.ExternalAbstractException;

public class OperationNotAllowedException extends ExternalAbstractException {

	private static final long		serialVersionUID	= 2298805640117774042L;
	protected static final String	CODE				= "412";
	private static final String		DESC				= "PRECONDITION_FAILED";

	public OperationNotAllowedException() {
	}

	public OperationNotAllowedException(String message) {
		super(message);
	}

	public OperationNotAllowedException(String message, Object... params) {
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
