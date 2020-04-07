package it.toscana.regione.commons.exception;

import it.toscana.regione.commons.exception.base.InternalAbstractException;

public class NotFoundException extends InternalAbstractException {

	private static final long		serialVersionUID	= -4706429275101764060L;
	protected static final String	CODE				= "404";
	private static final String		DESC				= "NOT_FOUND";

	public NotFoundException() {
	}

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(String message, Object... params) {
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
