/*
 * Validator.java
 * Created on 15-May-2005
 *
 */
package uk.co.alvagem.ui;

/**
 * Validator is an abstract class for validating input fields.  Concrete sub-classes
 * will check for required contents, valid numbers, regexp matches etc.
 * 
 * @author rbp28668
 * Created on 15-May-2005
 */
public abstract class Validator {

    public static final Validator REQUIRED = new RequiredValidator();
    
    public static final Validator BOOLEAN = new BooleanValidator();
    public static final Validator BYTE = new ByteValidator();
    public static final Validator CHAR = new CharValidator();
    public static final Validator DOUBLE = new DoubleValidator();
    public static final Validator FLOAT = new FloatValidator();
    public static final Validator INTEGER = new IntegerValidator();
    public static final Validator SHORT = new ShortValidator();
    public static final Validator STRING = new StringValidator();
    public static final Validator DATE = new DateValidator();
    
    private String errorText;	// display if validation fails
    
    /**
     * 
     */
    public Validator(String errorText) {
        super();
        if(errorText == null) {
            throw new NullPointerException("Validator error text must not be null");
        }
        this.errorText = errorText;
    }

    /**
     * isValid determines if the given contents are valid (by some appropriate rule).
     * This should ignore empty contents.
     * @param contents
     * @return
     */
    public abstract boolean isValid(String contents);
    
    /**
     * @return Returns the errorText.
     */
    public String getErrorText() {
        return errorText;
    }
}
