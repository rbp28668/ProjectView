/*
 * FloatValidator.java
 * Created on 16-May-2005
 *
 */
package uk.co.alvagem.ui;

/**
 * FloatValidator
 * 
 * @author rbp28668
 * Created on 16-May-2005
 */
public class FloatValidator extends Validator {

    /**
     * 
     */
    public FloatValidator() {
        super("Please enter a valid number");
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.servlet.Validator#isValid(java.lang.String)
     */
    public boolean isValid(String contents) {
        if(contents != null && contents.length() > 0){
			try {
				Float.valueOf(contents);
				return true;
			}catch (Exception e) {
				return false;
			}
        }
        return true;
    }

}
