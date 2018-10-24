/*
 * Calendar.java
 * Created on 28-Apr-2005
 * By Bruce.Porteous
 *
 */
package uk.co.alvagem.projectview.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Calendar describes the availability of a resource.  Calendars may have a parent - 
 * where there is not an entry in the calendar for a specific day then any scheduling
 * request is delegated to the parent.  Hence child calendars over-ride their parent.
 * By using a set of CalendarItem, availability is set as a decimal fraction for each
 * day that the CalendarItem corresponds to. This can be 0 to denote a holiday (no work), or
 * can be 1 to identify working time during a holiday defined in a parent calendar.
 * A half-day would be 0.5 etc.  
 * @author Bruce.Porteous
  */
public class Calendar extends PersistentBase implements Persistent{

    private static final float NOT_FOUND = Float.MAX_VALUE;
    
    /** User friendly name of the calendar */
    private String name;

    /** The items that make up the calendar */
    private Set<CalendarItem> items = new HashSet<CalendarItem>();
    
    private Calendar parent;
    
    private float workingDayLength;
    
    
 	/**
	 * Creates a new, empty calendar.
	 */
	public Calendar() {
		super();
		name = "";
		workingDayLength = WorkingDay.DEFAULT.getHoursPerDay();
	}
	
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    
    /**
	 * @return Returns the parent.
	 */
	public Calendar getParent() {
		return parent;
	}
	/**
	 * @param parent The parent to set.
	 */
	public void setParent(Calendar parent) {
		this.parent = parent;
	}
	/**
     * @return the items
     */
    public Set<CalendarItem> getItems() {
        return items;
    }
    /**
     * @param items the items to set
     */
    public void setItems(Set<CalendarItem> items) {
        this.items = items;
    }
    
//    /**
//     * Gets the number of elapsed days to schedule a given amount of work.  At most
//     * maxAllocation days of work can be scheduled per day.
//     * @param startDate is the first day for allocating work.
//     * @param daysNeeded is the number of days of work needed for the allocation. 
//     * Note that this is the number of 24 hour days, not working days.
//     * @param maxAllocation is the maximum allocation of work per day in fractions of the working day.  
//     * Note that, for scheduling, the calendar will be tied to a resource so the allocation for that resource 
//     * will be relative to that resource's working day.
//     * 0 < maxAllocation <= 1.
//     * @return the number of elapsed days.
//     */
//    public float schedule(Date startDate, float daysNeeded, float maxAllocation){
//        
//        if(maxAllocation <= 0.0f || maxAllocation > 1.0f){
//            throw new IllegalArgumentException("Max allocation must lie between 0 (exclusive) and 1 (inclusive)");
//        }
//        
//        if(daysNeeded < 0){
//            throw new IllegalArgumentException("Can't have -ve days needed");
//        }
//        
//        float days = 0.0f;
//        Date day = startDate;
//
//        final long ONE_DAY = 24L * 60L * 60L * 1000L;
//        final float workFraction = workingDayLength / 24;
//        
//        float workThisDay = 0.0f;
//        
//        while (daysNeeded > 0){
//            workThisDay = workFraction * scheduleDay(day,maxAllocation);
//            daysNeeded -= workThisDay;
//            days += 1.0f;
//            startDate.setTime(startDate.getTime() + ONE_DAY);
//        }
//        return days;
//    }
    
    /**
     * Schedules an individual day.
     * @param day is the day to schedule on.
     * @param maxAllocation is the maximum work allocation for that day.
     * @return the available work allocation for this day.
     */
    public float scheduleDay(Date day, float maxAllocation){
        float workThisDay = NOT_FOUND;
        for(CalendarItem item : items){
            if(item.appliesThisDay(day)){
                workThisDay = item.getTimeOn(day, maxAllocation);
                break;
            }
        }
        if(workThisDay == NOT_FOUND && parent != null){
            workThisDay = parent.scheduleDay(day, maxAllocation);
        }
        if(workThisDay == NOT_FOUND){
            workThisDay = maxAllocation;
        }
        return workThisDay;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        return getName();
    }

	/**
	 * @return the workingDayLength
	 */
	public float getWorkingDayLength() {
		return workingDayLength;
	}

	/**
	 * @param workingDayLength the workingDayLength to set
	 */
	public void setWorkingDayLength(float workingDayLength) {
		this.workingDayLength = workingDayLength;
	}
	
	/**
	 * Estimates the fraction of availability based on the days in the week
	 * this calendar has availability for.  This is in ignorance of the 
	 * dates that a resource will be scheduled for, so specific days (e.g. bank
	 * holidays, leave etc) are ignored.  Note that if the calendar has 100% 
	 * resource for 7 days the fractional result will be 1.0.  More likely 
	 * the answer is going to be 5/7 for a standard 5 day working week.
	 * @return a fractional availability.
	 */
	public float estimateAvailability(){

		final int[] daysOfWeek = {
			java.util.Calendar.SATURDAY,
			java.util.Calendar.SUNDAY,
			java.util.Calendar.MONDAY,
			java.util.Calendar.TUESDAY,
			java.util.Calendar.WEDNESDAY,
			java.util.Calendar.THURSDAY,
			java.util.Calendar.FRIDAY
		};

		float total = 0;
		for(int day = 0; day<daysOfWeek.length; ++day){
			float est = getEstimateFor(daysOfWeek[day]);
			if(est == NOT_FOUND){
				est = workingDayLength / WorkingDay.DEFAULT.getHoursPerDay();
			}
			total += est;
		}
		
		return total / daysOfWeek.length;
	}
	
	/**
	 * Used by estimateAvailability, this tries to find a utilisation fraction 
	 * for a given day of the week.
	 * @param day is the day to find.
	 * @return a utilisation fraction for that day or NOT_FOUND if none of the
	 * calendars in the hierarchy explicitly reference the given day.
	 */
	private float getEstimateFor(int day){
		
		float est = NOT_FOUND;
        
		for(CalendarItem item : items){
        	if(item instanceof DayOfWeekCalendarItem){
        		DayOfWeekCalendarItem dow = (DayOfWeekCalendarItem)item;
        		if(day == dow.getWhichDay()) {
        			est = dow.getDayFraction() * workingDayLength / WorkingDay.DEFAULT.getHoursPerDay();
        			break;
        		}
        	}
        }
		
		if(est == NOT_FOUND && parent != null){
			est = parent.getEstimateFor(day);
		}
		
		return est;
	}
	
}
