/*
 * DatePicker.java
 * Project: ProjectView
 * Created on 12 Jan 2008
 *
 */
package uk.co.alvagem.swingui;

import java.util.Calendar;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public class DatePicker extends JPanel {

    private static final long serialVersionUID = 1L;
    private JComboBox<Integer> day;
    private JComboBox<String> month;
    private JComboBox<Integer> year;
    private int firstYear;
    
    private static final String[] months = {
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    };
    
    public DatePicker(){
        this(40,10);
    }
    
    /**
     * 
     * @param yearCount is the total number of years to allow.
     * @param yearsPrevious is the number of previous years to allow.
     */
    public DatePicker(int yearCount, int yearsPrevious) {
        
        Integer[] days = new Integer[31];
        for(int i=0; i<days.length; ++i){
            days[i] = new Integer(i+1);
        }
        
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        
        int thisYear = calendar.get(Calendar.YEAR);
        
        Integer[] years = new Integer[yearCount];
        for(int i=0; i<years.length; ++i){
            years[i] = new Integer(thisYear + i - yearsPrevious);
        }
        firstYear = thisYear - yearsPrevious;
        
        day = new JComboBox<Integer>(days);
        month = new JComboBox<String>(months);
        year = new JComboBox<Integer>(years);
        
        add(day);
        add(month);
        add(year);
    }
    
    
    public void setDate(Date when){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(when);
        
        int d = calendar.get(Calendar.DAY_OF_MONTH) - calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        int m = calendar.get(Calendar.MONTH) - calendar.getActualMinimum(Calendar.MONTH);
        int y = calendar.get(Calendar.YEAR) - firstYear;
        
        day.setSelectedIndex(d);
        month.setSelectedIndex(m);
        year.setSelectedIndex(y);
    }

    public Date getDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        int d = day.getSelectedIndex() + calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        int m = month.getSelectedIndex() + calendar.getActualMinimum(Calendar.MONTH);
        int y = year.getSelectedIndex() + firstYear;

        calendar.set(y,m,d);
        return calendar.getTime();
    }

}
