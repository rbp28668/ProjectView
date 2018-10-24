/**
 * 
 */
package uk.co.alvagem.swingui;

import uk.co.alvagem.util.ChainedException;

/**
 * Generic exception to throw for an initialisation failure.
 * @author bruce.porteous
 *
 */
public class InitialisationException extends ChainedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public InitialisationException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InitialisationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public InitialisationException(String message) {
		super(message);
	}

}
