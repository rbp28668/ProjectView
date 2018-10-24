/*
 * CalendarItem.java
 * Project: ProjectView
 * Created on 5 Jan 2008
 *
 */
package uk.co.alvagem.projectview.model;

import java.util.Date;

public abstract class CalendarItem extends PersistentBase implements Persistent {

	/** Fraction of the working day available on the day(s) this Item applies to */
	private float dayFraction;  // 1 is available, 0 unavailable
    
    public CalendarItem() {
        super();
        dayFraction = 0;
    }

    /**
     * @return the dayFraction
     */
    public float getDayFraction() {
        return dayFraction;
    }

    /**
     * @param dayFraction the dayFraction to set
     */
    public void setDayFraction(float dayFraction) {
        this.dayFraction = dayFraction;
    }

    /**
     * Sees if this calendar item applies on the given date.
     * @param day is the date to check.
     * @return true iff the resource is available (even if only partially)
     * on the given day.
     */
    public abstract boolean appliesThisDay(Date day);
    
    /**
     * Gets the fraction of utilisation available on the given day. It it the
     * minimum of the maxAllocation and this entry's day-fraction if this 
     * item is relevent for the given day.
     * @param day is the day to schedule for.
     * @param maxAllocation is the maximum fraction of a day to use (so can limit
     * the allocation to 1/2 time or similar).
     * @return the available fraction of the resource on the given day.
     */
    public float getTimeOn(Date day, float maxAllocation) {
        if(appliesThisDay(day)){
            return Math.min(maxAllocation, getDayFraction());
        }
        return 0;
    }
    
    public void copyTo(CalendarItem dest){
        super.copyTo(dest);
        dest.setDayFraction(getDayFraction());
    }
    
    public abstract CalendarItem create();
    
    public CalendarItem clone(){
        CalendarItem copy = create();
        copyTo(copy);
        return copy;
    }
}
