/*
 * PresentationException.java
 * Project: ProjectView
 * Created on 29 Dec 2007
 *
 */
package uk.co.alvagem.ui;

import uk.co.alvagem.util.ChainedException;

public class PresentationException extends ChainedException {

    /** */
    private static final long serialVersionUID = 1L;

    public PresentationException() {
    }

    public PresentationException(String message) {
        super(message);
    }

    public PresentationException(String message, Throwable cause) {
        super(message, cause);
    }

}
