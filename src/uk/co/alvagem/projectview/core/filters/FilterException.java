/**
 * 
 */
package uk.co.alvagem.projectview.core.filters;

import uk.co.alvagem.util.ChainedException;

/**
 * Exception to show a problem with a filter.
 * @author bruce.porteous
 *
 */
public class FilterException extends ChainedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public FilterException() {
		super();
	}

	/**
	 * @param message
	 */
	public FilterException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FilterException(String message, Throwable cause) {
		super(message, cause);
	}

}
