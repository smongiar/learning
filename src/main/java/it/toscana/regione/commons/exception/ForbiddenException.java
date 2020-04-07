package it.toscana.regione.commons.exception;

import it.toscana.regione.commons.exception.base.ExternalAbstractException;

public class ForbiddenException extends ExternalAbstractException {

	private static final long	serialVersionUID	= -8935532684211342569L;

	private static final String	CODE				= "403";
	private static final String	DESC				= "FORBIDDEN";

	public ForbiddenException() {}

	public ForbiddenException(String message) {
		super(message);
	}

	public ForbiddenException(String message, Object... params) {
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
