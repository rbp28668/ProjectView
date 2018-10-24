/*
 * DateValidator.java
 * Project: ProjectView
 * Created on 2 Jan 2008
 *
 */
package uk.co.alvagem.ui;

import java.text.ParseException;


public class DateValidator extends Validator {

    public DateValidator() {
        super("Please enter a valid date of the form DD MMM YYYY");
    }

    @Override
    public boolean isValid(String contents) {
        try {
            DateHandler.FMT.parse(contents);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

}
