package it.toscana.regione.commons.exception.base;

/**
 * abstract exception class used to identify exception that must be propagated
 * to external context
 * 
 * @author fgiuntoli
 *
 */
public abstract class ExternalAbstractException extends CustomAbstractException {

	private static final long serialVersionUID = -7057769638890948318L;

	public ExternalAbstractException() {
		super();
	}

	public ExternalAbstractException(String message) {
		super(message);
	}

	public ExternalAbstractException(String message, Throwable ex) {
		super(message, ex);
	}

	public ExternalAbstractException(Throwable ex) {
		super(ex);
	}

	@Override
	public boolean isServerException() {
		return false;
	}

}
