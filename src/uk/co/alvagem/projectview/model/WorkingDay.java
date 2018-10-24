/**
 * 
 */
package uk.co.alvagem.projectview.model;

import java.util.Date;

import uk.co.alvagem.projectview.core.schedule.DateUtils;

/**
 * @author bruce.porteous
 *
 */
public class WorkingDay {

	public static final WorkingDay DEFAULT = new WorkingDay();
	
	private float hoursPerDay = 7.5f;

	private float startTime = 9;
	
	private static final long ONE_HOUR = 60L * 60L * 1000L;

	/**
	 * 
	 */
	public WorkingDay() {
		super();
	}

	/**
	 * @return Returns the hoursPerDay.
	 */
	public float getHoursPerDay() {
		return hoursPerDay;
	}

	/**
	 * @param hoursPerDay The hoursPerDay to set.
	 */
	public void setHoursPerDay(float hoursPerDay) {
		this.hoursPerDay = hoursPerDay;
	}

	public Date getStartTimeOn(Date date){
		Date t = new Date(date.getTime());
		DateUtils.zeroTime(t);
		t.setTime(t.getTime() + (long)(startTime * ONE_HOUR));
		return t;
	}
}
