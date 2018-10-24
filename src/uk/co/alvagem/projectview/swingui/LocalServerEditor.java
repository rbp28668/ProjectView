/*
 * LocalServerEditor.java
 * Project: ProjectView
 * Created on 29 Dec 2007
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

import uk.co.alvagem.swingui.BasicDialog;
import uk.co.alvagem.swingui.SwingForm;
import uk.co.alvagem.ui.FieldTemplate;
import uk.co.alvagem.ui.FormTemplate;

public class LocalServerEditor extends BasicDialog {

    /** */
    private static final long serialVersionUID = 1L;

    /** Panel for handling property edit - presented in the properties tab */
    private PropertiesPanel propertiesPanel;

    LocalServerEditor(Component parent, LocalServer localServer){
        super(parent,"Edit Local Server");
        propertiesPanel = new PropertiesPanel(localServer);
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
        PropertiesPanel(LocalServer localServer) {
            buildTemplates();
            form = new SwingForm(template, localServer);
            form.buildPanel(this);
        }
        
        private void buildTemplates(){
            if(template == null){
                template = new FormTemplate();
                
                template.add(new FieldTemplate(LocalServer.class,"Name","name"));
                template.add(new FieldTemplate(LocalServer.class,"Auto-Start","autostart"));
                template.add(new FieldTemplate(LocalServer.class,"Silent","silent"));
                template.add(new FieldTemplate(LocalServer.class,"Trace","trace"));
                
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
            boolean valid = form.validateInput(this, "Local Server Editor");
            return valid;
        }

    }

 }
