/*
 * DayCalendarItem.java
 * Project: ProjectView
 * Created on 5 Jan 2008
 *
 */
package uk.co.alvagem.projectview.model;

import java.text.DateFormat;
import java.util.Date;

public class DayCalendarItem extends CalendarItem {

    /** The day this item applies to */
    private Date day;
    private static final DateFormat FMT = DateFormat.getDateInstance(DateFormat.SHORT);
    
    public DayCalendarItem() {
        super();
        day = new Date();
    }

    @Override
    public boolean appliesThisDay(Date day) {
        return this.day.equals(day);
    }

    /**
     * @return the day
     */
    public Date getDay() {
        return day;
    }

    /**
     * @param day the day to set
     */
    public void setDay(Date day) {
        this.day = new Date(day.getTime());
    }

    public String toString(){
    	return Float.toString(getDayFraction()) + " on " + FMT.format(day);
    }

    @Override
    public void copyTo(CalendarItem dest) {
        super.copyTo(dest);
        DayCalendarItem item = (DayCalendarItem)dest;
        item.setDay(getDay());
    }

    @Override
    public CalendarItem create() {
        return new DayCalendarItem();
    }

}
