/*
 * ConstraintEditor.java
 * Project: ProjectView
 * Created on 29 Dec 2007
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import uk.co.alvagem.projectview.model.Constraint;
import uk.co.alvagem.projectview.model.ConstraintTypes;
import uk.co.alvagem.swingui.BasicDialog;
import uk.co.alvagem.swingui.SwingForm;
import uk.co.alvagem.ui.Field;
import uk.co.alvagem.ui.FieldTemplate;
import uk.co.alvagem.ui.FormTemplate;
import uk.co.alvagem.ui.Validator;

public class ConstraintEditor extends BasicDialog {

    /** */
    private static final long serialVersionUID = 1L;

    /** Panel for handling property edit - presented in the properties tab */
    private PropertiesPanel propertiesPanel;

    ConstraintEditor(Component parent, Constraint constraint){
        super(parent,"Edit Constraint");
        propertiesPanel = new PropertiesPanel(constraint);
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
         * on whether the constraint is a leaf constraint or not.
         * @param constraint
         */
        PropertiesPanel(Constraint constraint) {
            buildTemplates();
            form = new SwingForm(template, constraint);
            form.buildPanel(this);
        }
        
        private void buildTemplates(){
            if(template == null){
                template = new FormTemplate();
                
                final String TYPE_HINT = "ConstraintType";
                SwingForm.addEditor(TYPE_HINT, new ConstraintTypeEditor());
                 
                template.add(new FieldTemplate(Constraint.class,"Type","type")
                    .setWidgetType(TYPE_HINT));
                template.add(new FieldTemplate(Constraint.class,"Date","when")
                    .addValidation(Validator.DATE));
                
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
            boolean valid = form.validateInput(this, "Constraint Editor");
            return valid;
        }

    }

    private static class ConstraintTypeEditor extends SwingForm.TypeEditor {

        @Override
        public Component getEditor(Field field) {
            Object[] options = ConstraintTypes.getTypes().toArray();
            String value = field.getValue();
            int index = -1;
            for(int i=0; i<options.length; ++i){
                if(options[i].toString().equals(value)){
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
            field.setValue(list.getSelectedItem().toString());
        }
        
    }
 }
