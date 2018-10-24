/*
 * StringValidator.java
 * Created on 16-May-2005
 *
 */
package uk.co.alvagem.ui;

/**
 * StringValidator
 * 
 * @author rbp28668
 * Created on 16-May-2005
 */
public class StringValidator extends Validator {

    /**
     * 
     */
    public StringValidator() {
        super("Invalid String");
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.servlet.Validator#isValid(java.lang.String)
     */
    public boolean isValid(String contents) {
        return true;
    }

}
