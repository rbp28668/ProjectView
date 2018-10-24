/**
 * 
 */
package uk.co.alvagem.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A validator that uses a regular expression for a validation pattern.
 * @author bruce.porteous
 *
 */
public class RegexpValidator extends Validator {

	private Pattern pattern;
	

	/**
	 * @param regexp
	 */
	public RegexpValidator(String regexp) {
		super("Value must match this pattern: " + regexp);
		pattern = Pattern.compile(regexp);
	}

	/**
	 * @param regexp
	 * @param errorText
	 */
	public RegexpValidator(String regexp, String errorText) {
		super(errorText);
		pattern = Pattern.compile(regexp);
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.ui.Validator#isValid(java.lang.String)
	 */
	@Override
	public boolean isValid(String contents) {
		Matcher matcher = pattern.matcher(contents);
		return matcher.matches();
	}

}
