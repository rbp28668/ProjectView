/*
 * BooleanValidator.java
 * Created on 16-May-2005
 *
 */
package uk.co.alvagem.ui;

import java.math.BigDecimal;

/**
 * BooleanValidator
 * 
 * @author rbp28668
 * Created on 16-May-2005
 */
public class BigDecimalValidator extends Validator {

    /**
     * 
     */
    public BigDecimalValidator() {
        super("Invalid big decimal value");
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.servlet.Validator#isValid(java.lang.String)
     */
    public boolean isValid(String contents) {
        try {
          new BigDecimal(contents);
          return true;
        } catch (Exception e) {
          return false;
        }
    }

}
