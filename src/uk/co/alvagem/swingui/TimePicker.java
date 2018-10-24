/*
 * TimePicker.java
 * Project: ProjectView
 * Created on 12 Jan 2008
 *
 */
package uk.co.alvagem.swingui;

import java.util.Calendar;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public class TimePicker extends JPanel {

    private static final long serialVersionUID = 1L;
    private JComboBox<Integer> hour;
    private JComboBox<Integer> min;
    private JComboBox<Integer> sec;

    
    
    /**
     * 
     */
    public TimePicker() {
        
        Integer[] hours = buildList(24);
        Integer[] mins = buildList(60);
        Integer[] secs = buildList(60);
        
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        
        hour = new JComboBox<Integer>(hours);
        min = new JComboBox<Integer>(mins);
        sec = new JComboBox<Integer>(secs);
        
        add(hour);
        add(min);
        add(sec);
    }


    private Integer[] buildList(int size) {
      Integer[] vals = new Integer[size];
      for(int i=0; i<vals.length; ++i){
          vals[i] = new Integer(i);
      }
      return vals;
    }
    
    
    public void setTime(Date when){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(when);
        
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        int s = calendar.get(Calendar.SECOND);
        
        hour.setSelectedIndex(h);
        min.setSelectedIndex(m);
        sec.setSelectedIndex(s);
    }

    public Date getTime(){
      return addTime(new Date(0));
    }

    public Date addTime(Date date){
      Calendar calendar = Calendar.getInstance();
      calendar.clear();
      calendar.setTime(date);
      
      int h = hour.getSelectedIndex();
      int m = min.getSelectedIndex();
      int s = sec.getSelectedIndex();

      calendar.set(Calendar.HOUR_OF_DAY,h);
      calendar.set(Calendar.MINUTE,m);
      calendar.set(Calendar.SECOND,s);
      
      return calendar.getTime();
  }

}
