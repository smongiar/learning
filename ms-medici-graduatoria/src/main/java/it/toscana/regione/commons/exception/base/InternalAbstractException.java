package it.toscana.regione.commons.exception.base;

/**
 * abstract class used for managing internal exceptions. E.g. exceptions that do
 * not need to be propagated to the external world
 * 
 * @author fgiuntoli
 *
 */
public abstract class InternalAbstractException extends CustomAbstractException {

	private static final long serialVersionUID = -7057769638890948318L;

	public InternalAbstractException() {
		super();
	}

	public InternalAbstractException(String message) {
		super(message);
	}

	public InternalAbstractException(String message, Throwable ex) {
		super(message, ex);
	}

	public InternalAbstractException(Throwable ex) {
		super(ex);
	}

	@Override
	public boolean isServerException() {
		return true;
	}

}
