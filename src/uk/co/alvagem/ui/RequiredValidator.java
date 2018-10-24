/*
 * RequiredValidator.java
 * Created on 16-May-2005
 *
 */
package uk.co.alvagem.ui;

/**
 * RequiredValidator
 * 
 * @author rbp28668
 * Created on 16-May-2005
 */
public class RequiredValidator extends Validator {

    /**
     * @param errorText
     */
    public RequiredValidator() {
        super("Please enter a value");
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.servlet.Validator#isValid(java.lang.String)
     */
    public boolean isValid(String contents) {
        return contents != null && contents.length() > 0;
    }

}
