/*
 * ShortValidator.java
 * Created on 16-May-2005
 *
 */
package uk.co.alvagem.ui;

/**
 * ShortValidator
 * 
 * @author rbp28668
 * Created on 16-May-2005
 */
public class ShortValidator extends Validator {

    /**
     * 
     */
    public ShortValidator() {
        super("Please enter a valid number");
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.servlet.Validator#isValid(java.lang.String)
     */
    public boolean isValid(String contents) {
        if(contents != null && contents.length() > 0){
			try {
				Short.valueOf(contents);
				return true;
			}catch (Exception e) {
				return false;
			}
        }
        return true;
    }

}
