/*
 * WrappedTextArea.java
 * Project: EATool
 * Created on 28-Mar-2006
 *
 */
package uk.co.alvagem.swingui;

import javax.swing.JTextArea;
import javax.swing.text.Document;

/**
 * WrappedTextArea is a helper class is a JTextArea but with line wrapping set 
 * on by default.
 * 
 * @author rbp28668
 */
public class WrappedTextArea extends JTextArea {

    /**
     * 
     */
    public WrappedTextArea() {
        super();
        setWrapping();
    }

    /**
     * @param arg0
     */
    public WrappedTextArea(String arg0) {
        super(arg0);
        setWrapping();
    }

    /**
     * @param arg0
     * @param arg1
     */
    public WrappedTextArea(int arg0, int arg1) {
        super(arg0, arg1);
        setWrapping();
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     */
    public WrappedTextArea(String arg0, int arg1, int arg2) {
        super(arg0, arg1, arg2);
        setWrapping();
    }

    /**
     * @param arg0
     */
    public WrappedTextArea(Document arg0) {
        super(arg0);
        setWrapping();
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     */
    public WrappedTextArea(Document arg0, String arg1, int arg2, int arg3) {
        super(arg0, arg1, arg2, arg3);
        setWrapping();
    }

    private final void setWrapping(){
        setLineWrap(true);
        setWrapStyleWord(true);
    }
}
