/*
 * ConstraintEditor.java
 * Project: ProjectView
 * Created on 29 Dec 2007
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Collection;

import javax.swing.JPanel;

import uk.co.alvagem.projectview.model.Allocation;
import uk.co.alvagem.projectview.model.Resource;
import uk.co.alvagem.projectview.swingui.widgets.ResourceSelectionEditor;
import uk.co.alvagem.swingui.BasicDialog;
import uk.co.alvagem.swingui.SwingForm;
import uk.co.alvagem.ui.FieldTemplate;
import uk.co.alvagem.ui.FloatRangeValidator;
import uk.co.alvagem.ui.FormTemplate;

public class AllocationEditor extends BasicDialog {

    /** */
    private static final long serialVersionUID = 1L;

    /** Panel for handling property edit - presented in the properties tab */
    private PropertiesPanel propertiesPanel;

    AllocationEditor(Component parent, Allocation allocation, Collection<Resource> resources){
        super(parent,"Allocate Resource");
        propertiesPanel = new PropertiesPanel(allocation, resources);
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
         * @param allocation
         * @param resources
         */
        PropertiesPanel(Allocation allocation, Collection<Resource> resources) {
            buildTemplates(resources);
            form = new SwingForm(template, allocation);
            form.buildPanel(this);
        }
        
        private void buildTemplates(Collection<Resource> resources){
            if(template == null){
                template = new FormTemplate();
                
                final String RESOURCE_HINT = "AllocationEditor.ResourceSelection";
                SwingForm.addEditor(RESOURCE_HINT, new ResourceSelectionEditor(resources));
                 
                template.add(new FieldTemplate(Allocation.class,"Resource","resource",FieldTemplate.OBJECT)
                    .setWidgetType(RESOURCE_HINT));
                template.add(new FieldTemplate(Allocation.class,"Utilisation","utilisation")
                    .addValidation(new FloatRangeValidator(0.0f,1.0f)));
                
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
            boolean valid = form.validateInput(this, "Allocation Editor");
            return valid;
        }

    }
 }
