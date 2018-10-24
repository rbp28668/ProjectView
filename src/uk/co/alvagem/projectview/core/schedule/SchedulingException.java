/**
 * 
 */
package uk.co.alvagem.projectview.core.schedule;

/**
 * @author bruce.porteous
 *
 */
public class SchedulingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public SchedulingException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public SchedulingException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public SchedulingException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public SchedulingException(Throwable arg0) {
		super(arg0);
	}

}
