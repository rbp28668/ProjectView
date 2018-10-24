package uk.co.alvagem.swingui;

import java.util.Date;

import javax.swing.JPanel;

public class DateTimePicker extends JPanel {

   private static final long serialVersionUID = 1L;

   private DatePicker datePanel;
   private TimePicker timePanel;
   
   public DateTimePicker() {
     this(40,10);
   }
   public DateTimePicker(int yearCount, int yearsPrevious) {
     datePanel = new DatePicker(yearCount, yearsPrevious);
     timePanel = new TimePicker();
     add(datePanel);
     add(timePanel);
   }
   
   public void setDateTime(Date when){
     datePanel.setDate(when);
     timePanel.setTime(when);
   }

 public Date getDateTime(){
     return timePanel.addTime(datePanel.getDate());
 }

}
