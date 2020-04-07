package it.toscana.regione.commons.exception;

import it.toscana.regione.commons.exception.base.CustomAbstractException;

public class ClientExceptionFactory {

	public static CustomAbstractException getException(int httpErrorCode, String errorMessage, Throwable previous) {
		switch (httpErrorCode) {
		case 400:
			return new CustomIllegalArgumentException(errorMessage);
		case 401:
			return new UnauthorizedException(errorMessage);
		case 403:
			return new ForbiddenException(errorMessage);
		case 404:
			return new NotFoundException(errorMessage);
		case 412:
			return new OperationNotAllowedException(errorMessage);
		case 500:
			return new GenericException(errorMessage, previous);
		default:
			return new GenericException(previous);
		}
	}
}
