/*
 * ElapsedTimeHandler.java
 * Created on 13-May-2005
 *
 */
package uk.co.alvagem.projectview.swingui;


import uk.co.alvagem.projectview.model.WorkingDay;
import uk.co.alvagem.ui.TypeHandler;


/**
 * ElapsedTimeHandler is a type handler that converts to and from working days and
 * can express elapsed time as working days, hours or mins.  Note that ProjectView stores
 * effort and work in man-hours.  
 * @author bruce.porteous
 *
 */
class ElapsedTimeHandler extends TypeHandler {

	/** Convert hours to working days */
	private final static float DAYS = 1.0f / WorkingDay.DEFAULT.getHoursPerDay();
	/** Convert hours to hours! */
	private final static float HOURS = 1.0f;
	/** Convert hours to mins */
	private final static float MINS = 60;
	
	public static final String TYPE_KEY = "ElapsedTimeHandler.Key";
	
	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#parse(java.lang.String)
	 */
	public Object parse(String value) {
		Float f = null;
		float multiplier = 1.0f / DAYS;
		try{
			value = value.toLowerCase().trim();
			StringBuffer buff = new StringBuffer(value.length());
			for(int i=0; i<value.length(); ++i){
				int ch = value.charAt(i);
				if(ch == 'h'){
					multiplier = 1.0f / HOURS;  // Convert to hours - 
				} else if(ch == 'm'){
					multiplier = 1.0f / MINS; // min to day.
				} else {
					buff.append((char)ch);
				}
			}
			float fv = Float.parseFloat(buff.toString());
			fv *= multiplier;
			f = new Float(fv);
		} catch (Exception e){
			// NOP
		}
		return f;
	}
	
	

	/* (non-Javadoc)
	 * @see uk.co.alvagem.ui.TypeHandler#getValue(java.lang.Object)
	 */
	@Override
	public String getValue(Object value) {
		String result = null;
		if(value instanceof Float){
			Float f = (Float)value;
			float fv = f.floatValue();
			
			float workingDay = WorkingDay.DEFAULT.getHoursPerDay();
			
			if(fv == 0.0f){
				result = "0";
			} else {
				float multiplier = DAYS; // default - convert working days to hours
				String suffix = "";
				if(fv < 1.0){ // < 1 hr?  display in mins
					multiplier = MINS;
					suffix = "m";
				} else if (fv < workingDay) { // < 1 working day? display in hours
					multiplier = HOURS;
					suffix = "h";
				}
				
				fv *= multiplier;
				result = Float.toString(fv) + suffix;
			}
		} else {
			throw new IllegalArgumentException("Elapsed time must be a Float");
		}

		return result;
	}



	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#getTargetClass()
	 */
	public Class<?> getTargetClass() {
		return Float.TYPE;
	}

    @Override
    public int getDefaultLength() {
        return 15; // -3.40282347e+38
    }
	
}