package uk.co.alvagem.projectview.core.schedule;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import uk.co.alvagem.projectview.model.Allocation;
import uk.co.alvagem.projectview.model.Calendar;
import uk.co.alvagem.projectview.model.WorkingDay;

/**
 * Track scheduling for each resource. This tracks the amount of time available on each day
 * based on the resource allocation.  Each year of the schedule is managed by a ResourceYear
 * which is created as necessary.
 * @author bruce.porteous
 *
 */
public class ResourceSchedule {
	/** The last date/time the resource was scheduled on */
	private Date date;
	
	/** The allocation of resource */
	private Allocation allocation;
	
	/** Collection of ResourceYear keyed by 4 digit year number */
	private Map<Integer,ResourceYear> available = new HashMap<Integer,ResourceYear>();
		
	/** Earliest day the resource was scheduled on */
	private Date earliestDay;
	
	/** Latest day the resource was scheduled on */
	private Date latestDay;

	/** Creates a ResourceSchedule to start scheduling on a given date */
	public ResourceSchedule(Date start){
		date = DateUtils.zeroTime(start);
	}

	/**
	 * Gets the current scheduling date.  Note that this doesn't tell you anything about the
	 * time that the resource is available that day.
	 * @return the current scheduling date.  
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Sets the current scheduling date.
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date.setTime(date.getTime());
	}

	/**
	 * Gets the resource allocation for this resource schedule.
	 * @return the allocation
	 */
	public Allocation getAllocation() {
		return allocation;
	}

	/**
	 * Sets the resource allocation for this resourcing schedule.  Note - also
	 * clears existing scheduling information.
	 * @param allocation the allocation to set
	 */
	public void setAllocation(Allocation allocation) {
		this.allocation = allocation;
		available.clear(); // If we've just changed the resource then dump existing info.
	}
	
	/**
	 * Gets the number of hours available on a given date.
	 * @param date
	 * @return
	 */
	public float availableOn(Date date){
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTime(date);
		Integer yr = cal.get(java.util.Calendar.YEAR);

		ResourceYear year = available.get(yr);
		if(year == null){
			year = new ResourceYear();
			available.put(yr,year);
		}
		
		int dayOfYear = cal.get(java.util.Calendar.DAY_OF_YEAR);
		return year.getHoursOn(dayOfYear, allocation, date);
	}
	
	/**
	 * Burns a number of hours on a given date.  Note that availableOn() must be called
	 * to check available hours before hours can be allocated.
	 * @param date
	 * @param hours
	 */
	public void burnHoursOn(Date date, float hours){
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTime(date);
		Integer yr = cal.get(java.util.Calendar.YEAR);
		ResourceYear year = available.get(yr);
		if(year == null){
			throw new IllegalStateException("Can't burn hours on unscheduled day");
		}
		int dayOfYear = cal.get(java.util.Calendar.DAY_OF_YEAR);
		float left = year.burnHoursOn(dayOfYear, hours);
		
		// Cache start date - may be overridden by scheduler.
		long time = date.getTime();
		if(left == 0){
			time += (24L * 3600L * 1000L);
		}
		this.date.setTime(time);
		
		// Record earliest and latest dates when time allocated for later reporting 
		Date thisDay = new Date(date.getTime());
		DateUtils.zeroTime(thisDay);
		
		if(earliestDay == null){
			earliestDay = thisDay;
		} else if(thisDay.before(earliestDay)){
			earliestDay = thisDay;
		}
		
		if(latestDay == null){
			latestDay = thisDay;
		} else if (thisDay.after(latestDay)){
			latestDay = thisDay;
		}
	}
	
	/**
	 * Gets the number of hours used on a particular day.  Note that availableOn() must be called
	 * to check available hours before hours can be allocated.
	 * @param date
	 */
	public float getHoursUsedOn(Date date){
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTime(date);
		Integer yr = cal.get(java.util.Calendar.YEAR);
		ResourceYear year = available.get(yr);
		if(year == null){
			throw new IllegalStateException("Can't get hours on unscheduled day");
		}
		int dayOfYear = cal.get(java.util.Calendar.DAY_OF_YEAR);
		float used = year.getHoursUsedOn(dayOfYear);
		return used;
	}

	/**
	 * Gets the earliest day this resource is scheduled.
	 * @return the first or earliest day.
	 */
	public Date getEarliestDay(){
		return earliestDay;
	}
	
	/**
	 * Gets the latest day this resource is scheduled.
	 * @return the last or latest day.
	 */
	public Date gatLatestDay(){
		return latestDay;
	}
	
	/**
	 * Tracks the number of unscheduled hours per day for a resource in one
	 * year.
	 * @author bruce.porteous
	 *
	 */
	private static class ResourceYear {
		
		/** Number of unscheduled hours for each day during the year */
		private Float[] hoursPerDay = new Float[367]; // leap year + calendar.DAY_OF_YEAR starts at 1
		private float[] hoursUsed = new float[367];
		
		ResourceYear(){
			Arrays.fill(hoursUsed,0.0f);
		}
		
		/**
		 * Gets the number of hours available on a given day.
		 * @param dayOfYear is the day of the year from 1 to 366 (leap years!) inclusive.
		 * @param allocation is the resource allocation that provides the starting point for the
		 * number of unscheduled hours.
		 * @param day is the day we are trying to schedule on (note- must match dayOfYear).
		 * @return number of available hours.
		 */
		float getHoursOn(int dayOfYear, Allocation allocation, Date day){
			assert(dayOfYear > 0);
			assert(dayOfYear < hoursPerDay.length);
			
			if(hoursPerDay[dayOfYear] == null){
				float maxAllocation = allocation.getUtilisation();
				
				float hoursWorkThisDay = 0;
				Calendar calendar = allocation.getResource().getAvailability();
				float available = maxAllocation;
				if(calendar != null) {
					available = calendar.scheduleDay(day, maxAllocation);
					hoursWorkThisDay = available * calendar.getWorkingDayLength();
				} else {
					hoursWorkThisDay = available * WorkingDay.DEFAULT.getHoursPerDay();
				}
				
				hoursPerDay[dayOfYear] = new Float(hoursWorkThisDay);
			}
			
			return hoursPerDay[dayOfYear];
		}
		
		/**
		 * Burns a number of hours on a given day.
		 * @param dayOfYear is the day of the year from 1 to 366 (leap years!) inclusive.
		 * @param hours are the number of hours to burn.
		 * @return the number of hours left that day.
		 */
		float burnHoursOn(int dayOfYear, float hours){
			assert(dayOfYear > 0);
			assert(dayOfYear < hoursPerDay.length);
			assert(hoursPerDay[dayOfYear] != null);
			assert(hours <= hoursPerDay[dayOfYear]);
			
			hoursUsed[dayOfYear] += hours;
			hoursPerDay[dayOfYear] -= hours;
			return hoursPerDay[dayOfYear];
		}
		
		/**
		 * Get the actual number of hours allocated on a given day.
		 * @param dayOfYear is the day index in the year.
		 * @return the number of hours effort allocated for this day.
		 */
		float getHoursUsedOn(int dayOfYear){
			assert(dayOfYear > 0);
			assert(dayOfYear < hoursPerDay.length);
			return hoursUsed[dayOfYear];
		}
	}

}