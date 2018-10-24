/**
 * 
 */
package uk.co.alvagem.ui;

/**
 * Validates the text entered into a Long field.
 * @author bruce.porteous
 *
 */
public class LongValidator extends Validator {

	/**
	 * @param errorText
	 */
	public LongValidator() {
		super("Please enter a valid (long-integer) number");
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.ui.Validator#isValid(java.lang.String)
	 */
	@Override
	public boolean isValid(String contents) {
       if(contents != null && contents.length() > 0){
			try {
				Long.valueOf(contents);
				return true;
			}catch (Exception e) {
				return false;
			}
        }
        return true;
	}

}
