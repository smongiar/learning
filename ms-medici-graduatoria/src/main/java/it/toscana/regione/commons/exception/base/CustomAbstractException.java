package it.toscana.regione.commons.exception.base;

public abstract class CustomAbstractException extends RuntimeException {

	private static final long serialVersionUID = -5231114237726136230L;

	public CustomAbstractException() {
		super();
	}

	public CustomAbstractException(String message) {
		super(message);
	}

	public CustomAbstractException(String message, Throwable ex) {
		super(message, ex);
	}

	public CustomAbstractException(Throwable ex) {
		super(ex);
	}

	public abstract String getCode();

	public abstract String getDescription();

	public abstract boolean isServerException();

}
