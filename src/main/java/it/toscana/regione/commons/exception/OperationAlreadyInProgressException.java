package it.toscana.regione.commons.exception;

import it.toscana.regione.commons.exception.base.ExternalAbstractException;

public class OperationAlreadyInProgressException extends ExternalAbstractException {

	private static final long		serialVersionUID	= -6846648318981093332L;
	protected static final String	CODE				= "409.1";
	private static final String		DESC				= "OPERATION_ALREADY_IN_PROGRESS";

	public OperationAlreadyInProgressException() {
	}

	public OperationAlreadyInProgressException(String message) {
		super(message);
	}

	public OperationAlreadyInProgressException(String message, Object... params) {
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
