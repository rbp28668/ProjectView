/**
 * 
 */
package uk.co.alvagem.projectview.model;

/**
 * @author bruce.porteous
 *
 */
public class DefaultCalendar extends Calendar {

	private static Calendar instance = null;
	
	private DefaultCalendar(){
		setName("DEFAULT");
		DayOfWeekCalendarItem saturday = new DayOfWeekCalendarItem();
		saturday.setWhichDay(java.util.Calendar.SATURDAY);
		saturday.setDayFraction(0);

		DayOfWeekCalendarItem sunday = new DayOfWeekCalendarItem();
		sunday.setWhichDay(java.util.Calendar.SATURDAY);
		sunday.setDayFraction(0);

		
		getItems().add(saturday);
		getItems().add(sunday);
	}
	
	public static Calendar getInstance(){
		synchronized(DefaultCalendar.class){
			if(instance == null){
				instance = new DefaultCalendar();
			}
		}
		return instance;
	}
}
