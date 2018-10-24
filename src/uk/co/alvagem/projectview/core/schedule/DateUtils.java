/**
 * 
 */
package uk.co.alvagem.projectview.core.schedule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Various utility methods for date/time manipulation.
 * @author bruce.porteous
 *
 */
public class DateUtils {

	public static final long TICKS_PER_DAY = 1000L * 60L * 60L * 24L;

	/** Debug helper as scheduling is all about date/time */
    public static  DateFormat DF = SimpleDateFormat.getDateTimeInstance();

    static {
    	// Set default time-zone for debugging.
    	DF.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    
	/**
	 * Takes an arbitrary date and zeros the time part so that it
	 * represents 00:00 hrs on the morning of that day.
	 * @param date is the original day.
	 * @return a new time-zeroed Date.
	 */
	public static Date zeroTime(Date date){
		long ms = date.getTime();
		long time = ms % TICKS_PER_DAY;
		ms -= time;
		Date day = new Date(ms);
		return day;
	}

}
