/*
 * FloatValidator.java
 * Created on 16-May-2005
 *
 */
package uk.co.alvagem.ui;

/**
 * FloatRangeValidator
 * 
 * @author rbp28668
 * Created on 16-May-2005
 */
public class FloatRangeValidator extends Validator {

	private boolean minInclusive = true;
	private boolean maxInclusive = true;
	private float min = -Float.MAX_VALUE;
	private float max = Float.MAX_VALUE;
	
	
	public FloatRangeValidator(float max){
		this(0.0f, max, true, true);
	}
	
	public FloatRangeValidator(float min, float max){
		this(min, max, true, true);
	}
	
    /**
     * 
     */
    public FloatRangeValidator(float min, float max, boolean minInclusive, boolean maxInclusive) {
        super("Number must be in the range " + 
        		min + 
        		"(" + ((minInclusive) ? "inclusive" : "exclusive") + ")" +
        		" to " +
        		max +
        		"(" + ((maxInclusive) ? "inclusive" : "exclusive") + ")"
        		);
        this.min = min;
        this.max = max;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
   	}

    /* (non-Javadoc)
     * @see uk.co.alvagem.servlet.Validator#isValid(java.lang.String)
     */
    public boolean isValid(String contents) {
        if(contents != null && contents.length() > 0){
			try {
				float value = Float.valueOf(contents);
				if(minInclusive){
					if(value < min){
						return false;
					}
				} else {
					if(value <= min) {
						return false;
					}
				}
				
				if(maxInclusive){
					if(value > max) {
						return false;
					} 
				} else {
					if(value >= max) {
						return false;
					}
				}
				
				return true;
			}catch (Exception e) {
				return false;
			}
        }
        return true;
    }

}
