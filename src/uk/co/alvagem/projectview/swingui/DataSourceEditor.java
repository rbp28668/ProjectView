/*
 * LocalServerEditor.java
 * Project: ProjectView
 * Created on 29 Dec 2007
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import uk.co.alvagem.database.HibernateConfigurator;
import uk.co.alvagem.swingui.BasicDialog;
import uk.co.alvagem.swingui.SwingForm;
import uk.co.alvagem.ui.Field;
import uk.co.alvagem.ui.FieldTemplate;
import uk.co.alvagem.ui.FormTemplate;

public class DataSourceEditor extends BasicDialog {

    /** */
    private static final long serialVersionUID = 1L;

    /** Panel for handling property edit - presented in the properties tab */
    private PropertiesPanel propertiesPanel;

    DataSourceEditor(Component parent, HibernateConfigurator.DataSource dataSource){
        super(parent,"Edit Data Source");
        propertiesPanel = new PropertiesPanel(dataSource);
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
         * Creates a new Properties Panel.  
         * @param localServer
         */
        PropertiesPanel(HibernateConfigurator.DataSource dataSource) {
            buildTemplates();
            form = new SwingForm(template, dataSource);
            form.buildPanel(this);
        }
        
        private void buildTemplates(){
            if(template == null){
                template = new FormTemplate();
 
                final String TYPE_HINT = "DialectType";
                SwingForm.addEditor(TYPE_HINT, new DialectTypeEditor());

                template.add(new FieldTemplate(HibernateConfigurator.DataSource.class, "Name", "name"));
                template.add(new FieldTemplate(HibernateConfigurator.DataSource.class, "Driver Class", "driverClass"));
                template.add(new FieldTemplate(HibernateConfigurator.DataSource.class, "Connection URL", "connectionURL"));
                template.add(new FieldTemplate(HibernateConfigurator.DataSource.class, "Username", "username"));
                template.add(new FieldTemplate(HibernateConfigurator.DataSource.class, "Password", "password"));
                template.add(new FieldTemplate(HibernateConfigurator.DataSource.class, "Hibernate Dialect","dialect")
                .setWidgetType(TYPE_HINT));
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
            boolean valid = form.validateInput(this, "Data Source Editor");
            return valid;
        }

    }

    private static class DialectTypeEditor extends SwingForm.TypeEditor {

        @Override
        public Component getEditor(Field field) {
            
            String value = field.getValue();
            int index = -1;
            for(int i=0; i<HibernateConfigurator.DIALECTS.length; ++i){
                if(HibernateConfigurator.DIALECTS[i].equals(value)){
                    index = i;
                    break;
                }
            }

            JComboBox list = new JComboBox(HibernateConfigurator.DIALECTS);
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
