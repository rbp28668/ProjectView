/*
 * ResourceEditor.java
 * Project: ProjectView
 * Created on 29 Dec 2007
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Collection;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import uk.co.alvagem.projectview.model.Calendar;
import uk.co.alvagem.projectview.model.Resource;
import uk.co.alvagem.swingui.BasicDialog;
import uk.co.alvagem.swingui.SwingForm;
import uk.co.alvagem.ui.Field;
import uk.co.alvagem.ui.FieldTemplate;
import uk.co.alvagem.ui.FormTemplate;

public class ResourceEditor extends BasicDialog {

    /** */
    private static final long serialVersionUID = 1L;

    /** Panel for handling property edit - presented in the properties tab */
    private PropertiesPanel propertiesPanel;

    ResourceEditor(Component parent, Resource resource, Collection<Calendar> calendars){
        super(parent,"Edit Resource");
        propertiesPanel = new PropertiesPanel(resource, calendars);
        getContentPane().add(propertiesPanel, BorderLayout.CENTER);
        getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);
        pack();
        setVisible(true);

    }
    
    @Override
    protected void onOK() {
        propertiesPanel.onOK();
    }

    @Override
    protected boolean validateInput() {
        return propertiesPanel.validateInput();
    }

    private static class PropertiesPanel extends JPanel{
        
        private static final long serialVersionUID = 1L;
        private SwingForm form;
        private static FormTemplate template = null;
        
        /**
         * Creates a new Properties Panel.  Which properties are editable depend
         * on whether the resource is a leaf resource or not.
         * @param resource
         */
        PropertiesPanel(Resource resource, Collection<Calendar> calendars) {
            buildTemplates(calendars);
            form = new SwingForm(template, resource);
            form.buildPanel(this);
        }
        
        private void buildTemplates(Collection<Calendar> calendars){
            final String CALENDAR_HINT = "ResourceEditor.CalendarSelection";
            SwingForm.addEditor(CALENDAR_HINT, new CalendarSelectionEditor(calendars));

            if(template == null){
                template = new FormTemplate();
                
                
                template.add(new FieldTemplate(Resource.class,"Name","name"));
                template.add(new FieldTemplate(Resource.class,"Availability","availability",FieldTemplate.OBJECT)
                .setWidgetType(CALENDAR_HINT));
                template.add(new FieldTemplate(Resource.class,"Cost","cost"));
                
                // Use hibernate metadata to make mandatory fields and field lengths match
                // the database.
                HibernateMeta meta = new HibernateMeta();
                meta.update(template);
            }
        }
        /**
         * Method onOK - called when the OK button is clicked
         *  to tidy up any current edit.
         */
        void onOK() {
            form.updateObjects();
        }
        
        /**
         * Validates the properties in the model.  If invalid a message is displayed
         * and the offending row highlighted.
         * @return true if valid, false if not.
         */
        public boolean validateInput(){
            boolean valid = form.validateInput(this, "Resource Editor");
            return valid;
        }

    }
    
    private static class CalendarSelectionEditor extends SwingForm.TypeEditor {

        private Collection<Calendar> calendars;
        
        CalendarSelectionEditor(Collection<Calendar> calendars){
            this.calendars = calendars;
        }
        
        @Override
        public Component getEditor(Field field) {
            Object[] options = calendars.toArray();
            Calendar calendar = (Calendar)field.getValueAsObject();
            int index = -1;
            for(int i=0; i<options.length; ++i){
                if(options[i].equals(calendar)){
                    index = i;
                    break;
                }
            }

            JComboBox list = new JComboBox(options);
            list.setEditable(false);
            list.setSelectedIndex(index);
            return list;
        }

        @Override
        public void setFieldValue(Component component, Field field) {
            if(!(component instanceof JComboBox)){
                throw new IllegalArgumentException("Invalid component type");
            }
            JComboBox list = (JComboBox)component;
            Object selected = list.getSelectedItem();
            field.setValueObject(selected);
        }
    }


 }
