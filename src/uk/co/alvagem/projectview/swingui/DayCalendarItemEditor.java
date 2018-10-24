/*
 * DayOfWeekCalendarItemEditor.java
 * Project: ProjectView
 * Created on 11 Jan 2008
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.awt.BorderLayout;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JPanel;

import uk.co.alvagem.projectview.model.DayCalendarItem;
import uk.co.alvagem.swingui.BasicDialog;
import uk.co.alvagem.swingui.SwingForm;
import uk.co.alvagem.ui.FieldTemplate;
import uk.co.alvagem.ui.FormTemplate;



public class DayCalendarItemEditor extends BasicDialog {

    /** */
    private static final long serialVersionUID = 1L;
    private ItemPanel panel;
    
    public DayCalendarItemEditor(JDialog parent, DayCalendarItem item) {
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
            template.add(new FieldTemplate(DayCalendarItem.class, "Availability", "dayFraction",Float.class)
                .setDescription("The availability fraction for this day")
                .setRequired(true));
            template.add(new FieldTemplate(DayCalendarItem.class, "Date", "day",Date.class)
                .setDescription("The date this availability corresponds to"));
        }
        
        ItemPanel(DayCalendarItem item){
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
    
    
    

}
