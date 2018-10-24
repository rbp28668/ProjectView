/*
 * DayOfWeekCalendarItemEditor.java
 * Project: ProjectView
 * Created on 11 Jan 2008
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import uk.co.alvagem.projectview.model.DayOfWeekCalendarItem;
import uk.co.alvagem.swingui.BasicDialog;
import uk.co.alvagem.swingui.SwingForm;
import uk.co.alvagem.ui.Field;
import uk.co.alvagem.ui.FieldTemplate;
import uk.co.alvagem.ui.FormTemplate;



public class DayOfWeekCalendarItemEditor extends BasicDialog {

    /** */
    private static final long serialVersionUID = 1L;
    private ItemPanel panel;
    
    public DayOfWeekCalendarItemEditor(JDialog parent, DayOfWeekCalendarItem item) {
        super(parent,"Edit Calendar Entry");
    
        panel = new ItemPanel(item);
        add(panel,BorderLayout.CENTER);
        
        add(getOKCancelPanel(), BorderLayout.EAST);
        pack();
        setVisible(true);
    }

    @Override
    protected void onOK() {
        panel.onOK();
    }

    @Override
    protected boolean validateInput() {
        return panel.isValidInput();
    }

    private static class ItemPanel extends JPanel{
 
        /** */
        private static final long serialVersionUID = 1L;
        private SwingForm form;
        private static FormTemplate template = new FormTemplate();
        static {
            SwingForm.addEditor("DayOfWeek", new DayOfWeekEditor());
            template.add(new FieldTemplate(DayOfWeekCalendarItem.class, "Availability", "dayFraction")
                .setDescription("The availability fraction for this day")
                .setRequired(true));
            template.add(new FieldTemplate(DayOfWeekCalendarItem.class, "Day", "dayIndex")
                .setWidgetType("DayOfWeek")
                .setDescription("The day of the week this item corresponds to"));
        }
        
        ItemPanel(DayOfWeekCalendarItem item){
            form = new SwingForm(template, item);
            form.buildPanel(this);
        }
        
        boolean isValidInput(){
            return form.validateInput(this, "Calendar Item");
        }
        
        void onOK() {
            form.updateObjects();
        }
    }
    
    
    private static class DayOfWeekPanel extends JPanel{

        /**  */
        private static final long serialVersionUID = 1L;
        private JRadioButton[] buttons;
        private ButtonGroup group = new ButtonGroup();
        
        DayOfWeekPanel(){
            
            String[] labels = DayOfWeekCalendarItem.getDays();
            buttons = new JRadioButton[labels.length];

            
            setBorder(new TitledBorder("Select Day"));
            setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
            for(int i=0; i<labels.length; ++i){
                buttons[i] = new JRadioButton(labels[i]);
                add(buttons[i]);
                group.add(buttons[i]);
            }
        }

        void setDayIndex(int dayIndex){
            buttons[dayIndex].setSelected(true);
        }
        
        
        String getDayString(){
            String[] labels = DayOfWeekCalendarItem.getDays();
            return labels[getDayIndex()];
        }
        
        private int getDayIndex(){
            for(int i=0; i<buttons.length; ++i){
                if(buttons[i].isSelected()){
                    return i;
                }
            }
            return -1;
        }
    }
    
    private static class DayOfWeekEditor extends SwingForm.TypeEditor {

        @Override
        public Component getEditor(Field field) {
            DayOfWeekPanel panel = new DayOfWeekPanel();
            int dow = Integer.parseInt(field.getValue());
            panel.setDayIndex(dow);
            return panel;
        }

        @Override
        public void setFieldValue(Component component, Field field) {
            if(!(component instanceof DayOfWeekPanel )){
                throw new IllegalArgumentException("Invalid component type");
            }
            
            DayOfWeekPanel panel = (DayOfWeekPanel)component;
            int dow = panel.getDayIndex();
            field.setValue(Integer.toString(dow));
        }
        
    }

}
