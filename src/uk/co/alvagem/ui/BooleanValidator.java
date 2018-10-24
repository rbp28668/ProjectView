/*
 * BooleanValidator.java
 * Created on 16-May-2005
 *
 */
package uk.co.alvagem.ui;

/**
 * BooleanValidator
 * 
 * @author rbp28668
 * Created on 16-May-2005
 */
public class BooleanValidator extends Validator {

    /**
     * 
     */
    public BooleanValidator() {
        super("Invalid boolean value");
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.servlet.Validator#isValid(java.lang.String)
     */
    public boolean isValid(String contents) {
        if(contents != null && contents.length() > 0){
            return contents.equals("on") 
            || contents.equals("true")
            || contents.equals("off")
            || contents.equals("false");
        }
        return true;
    }

}
