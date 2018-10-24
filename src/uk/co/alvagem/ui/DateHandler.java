/*
 * DateHandler.java
 * Project: ProjectView
 * Created on 2 Jan 2008
 *
 */
package uk.co.alvagem.ui;

import java.text.DateFormat;
import java.util.Date;



public class DateHandler extends TypeHandler {

    public static final DateFormat FMT = DateFormat.getDateInstance(DateFormat.LONG);
    
    /* (non-Javadoc)
     * @see uk.co.alvagem.ui.TypeHandler#getValue(java.lang.Object)
     */
    @Override
    public String getValue(Object value) {
        if(! (value instanceof Date)){
            throw new IllegalArgumentException("DateHandler needs a Date!");
        }
        return FMT.format((Date)value);
    }

    @Override
    public int getDefaultLength() {
        return 16;
    }

    @Override
    public Class<?> getTargetClass() {
        return Date.class;
    }

    @Override
    public Object parse(String value) {
        try {
            return FMT.parse(value);
        }catch (Exception e) {
            return null;
        }
    }

}
