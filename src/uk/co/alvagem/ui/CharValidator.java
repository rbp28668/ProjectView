/*
 * CharValidator.java
 * Created on 16-May-2005
 *
 */
package uk.co.alvagem.ui;

/**
 * CharValidator
 * 
 * @author rbp28668
 * Created on 16-May-2005
 */
public class CharValidator extends Validator {

    /**
     * 
     */
    public CharValidator() {
        super("Invalid Character");
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.servlet.Validator#isValid(java.lang.String)
     */
    public boolean isValid(String contents) {
        return true;
    }

}
