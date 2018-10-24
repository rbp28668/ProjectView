/*
 * DayOfWeekCalendarItem.java
 * Project: ProjectView
 * Created on 5 Jan 2008
 *
 */
package uk.co.alvagem.projectview.model;

import java.util.Calendar;
import java.util.Date;

public class DayOfWeekCalendarItem extends CalendarItem {

    private int whichDay = Calendar.SUNDAY;  // Use Calendar.MONDAY etc.
    
	private static final String[] labels = {
		"Saturday",
		"Sunday",
		"Monday",
		"Tuesday",
		"Wednesday",
		"Thursday",
		"Friday"
		};

	private static final int[] daysOfWeek = {
			java.util.Calendar.SATURDAY,
			java.util.Calendar.SUNDAY,
			java.util.Calendar.MONDAY,
			java.util.Calendar.TUESDAY,
			java.util.Calendar.WEDNESDAY,
			java.util.Calendar.THURSDAY,
			java.util.Calendar.FRIDAY
	};

	
    public DayOfWeekCalendarItem() {
        super();
    }

    public static String[] getDays(){
    	return labels;
    }
    
    @Override
    public boolean appliesThisDay(Date day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        return whichDay == calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Gets the day which will be equal to one of the java.util.Calendar day constants.
     * @return the whichDay
     */
    public int getWhichDay() {
        return whichDay;
    }

    /**
     * @param whichDay the whichDay to set
     */
    public void setWhichDay(int whichDay) {
        this.whichDay = whichDay;
    }

    
    public int getDayIndex() {
    	for(int i=0; i<daysOfWeek.length; ++i){
    		if(whichDay == daysOfWeek[i]){
    			return i;
    		}
    	}
    	return 0;
    }
    
    public void setDayIndex(int idx) {
    	if(idx < 0 || idx >= daysOfWeek.length){
    		throw new IllegalArgumentException("Day of week index out of range");
    	}
    	whichDay = daysOfWeek[idx];
    }

    
    public String toString(){
    	String text = "?";
    	for(int i=0; i<daysOfWeek.length; ++i){
    		if(whichDay == daysOfWeek[i]){
    			text = labels[i];
    			break;
    		}
    	}
    	return Float.toString(getDayFraction()) + " every " + text;
    }

    @Override
    public void copyTo(CalendarItem dest) {
        super.copyTo(dest);
        DayOfWeekCalendarItem item = (DayOfWeekCalendarItem)dest;
        item.setDayIndex(getDayIndex());
    }

    @Override
    public CalendarItem create() {
        return new DayOfWeekCalendarItem();
    }

}
