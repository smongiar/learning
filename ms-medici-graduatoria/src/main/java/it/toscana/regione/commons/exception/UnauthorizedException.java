package it.toscana.regione.commons.exception;

import it.toscana.regione.commons.exception.base.ExternalAbstractException;

public class UnauthorizedException extends ExternalAbstractException {

	private static final long	serialVersionUID	= -260808353465150803L;

	private static final String	CODE				= "401";
	private static final String	DESC				= "UNAUTHORIZED";

	public UnauthorizedException() {}

	public UnauthorizedException(String message) {
		super(message);
	}

	public UnauthorizedException(String message, Object... params) {
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
