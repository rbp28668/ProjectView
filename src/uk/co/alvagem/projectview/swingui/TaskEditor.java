/*
 * TaskEditor.java
 * Project: ProjectView
 * Created on 29 Dec 2007
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

import uk.co.alvagem.database.DAOFactory;
import uk.co.alvagem.projectview.dao.TaskDAO;
import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.projectview.swingui.widgets.UncertaintyTypeSelectionEditor;
import uk.co.alvagem.swingui.BasicDialog;
import uk.co.alvagem.swingui.SwingForm;
import uk.co.alvagem.ui.FieldTemplate;
import uk.co.alvagem.ui.FormTemplate;

public class TaskEditor extends BasicDialog {

    /** */
    private static final long serialVersionUID = 1L;

    /** Panel for handling property edit - presented in the properties tab */
    private PropertiesPanel propertiesPanel;

    public TaskEditor(Component parent, Task task){
        super(parent,"Edit Task");
        propertiesPanel = new PropertiesPanel(task);
        getContentPane().add(propertiesPanel, BorderLayout.CENTER);
        getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);
        pack();
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
 
        private static FormTemplate leafTemplate;
        private static FormTemplate template;
        
        /**
         * Creates a new Properties Panel.  Which properties are editable depend
         * on whether the task is a leaf task or not.
         * @param task
         */
        PropertiesPanel(Task task) {
            buildTemplates();
            FormTemplate toUse = null;
            if(task.getSubTasks().isEmpty()){
                toUse = leafTemplate;
            } else {
            	toUse = template;
            }
            form = new SwingForm(toUse, task);
            form.buildPanel(this);
        }
        
        private void buildTemplates(){
            if(leafTemplate == null){
                leafTemplate = new FormTemplate();
                template = new FormTemplate();

                final String TYPE_HINT = "TypeEditor.UncertaintyTypeSelection";
                SwingForm.addEditor(TYPE_HINT, new UncertaintyTypeSelectionEditor());

                FieldTemplate name = new FieldTemplate(Task.class,"Name","name");
                FieldTemplate desc = new FieldTemplate(Task.class,"Description","description");
                FieldTemplate notes = new FieldTemplate(Task.class,"Notes","notes");
                FieldTemplate wp = new FieldTemplate(Task.class,"Work Package","workPackage");
                FieldTemplate priority = new FieldTemplate(Task.class,"Priority","priority");
         
                leafTemplate.add(name);
                leafTemplate.add(desc);
                leafTemplate.add(notes);
                leafTemplate.add(wp);
                leafTemplate.add(priority);
                
                leafTemplate.add(new FieldTemplate(Task.class,"Estimated Effort","estimatedEffort",ElapsedTimeHandler.TYPE_KEY));
                leafTemplate.add(new FieldTemplate(Task.class,"Estimate Spread","estimateSpread",ElapsedTimeHandler.TYPE_KEY));
                leafTemplate.add(new FieldTemplate(Task.class,"Uncertainty Type","uncertaintyType",FieldTemplate.OBJECT)
                .setWidgetType(TYPE_HINT));
                leafTemplate.add(new FieldTemplate(Task.class,"Fraction Complete","fractionComplete"));
                leafTemplate.add(new FieldTemplate(Task.class,"Actual Work","actualWork",ElapsedTimeHandler.TYPE_KEY));
                leafTemplate.add(new FieldTemplate(Task.class,"Effort Driven", "effortDriven"));
                leafTemplate.add(new FieldTemplate(Task.class,"Active", "active"));
                
                template.add(name);
                template.add(desc);
                template.add(notes);
                template.add(wp);
                template.add(priority);
                
                // Use hibernate metadata to make mandatory fields and field lengths match
                // the database.
                HibernateMeta meta = new HibernateMeta();
                meta.update(name);
                meta.update(desc);
                meta.update(notes);
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
            boolean valid = form.validateInput(this, "Task Editor");
            return valid;
        }

    }

    
    /**
     * Edits a given task.  Here so that it can be triggered from a mouse-click, package
     * visibility so that it can be called from the explorer.
     * @param component is the parent UI component for the editor.
     * @param task is the task to be edited.
     * @throws Exception
     */
    public static void editTask(Component component, Task task) throws Exception{
        DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
        TaskEditor editor;
        
        // Make sure task is initialised enough to be editable.
        factory.beginTransaction();
        TaskDAO dao = factory.getTaskDAO();
        try {
            dao.makePersistent(task);
            task.getHistory().size();
            editor = new TaskEditor(component,task);
            factory.commit();
        } catch (Exception e) {
            factory.rollback();
            throw e;
        }

        editor.setVisible(true);

        if(editor.wasEdited()){
            factory.beginTransaction();
            dao = factory.getTaskDAO();
            try {
                dao.makePersistent(task);
                task.commitHistory();
                updateParents(task,dao);
                factory.commit();
            } catch (Exception e) {
                factory.rollback();
                throw e;
            }
            
        }

    }
    
    private static void updateParents(Task task, TaskDAO dao){
    	task = task.getParent();
    	while(task != null){
        	dao.makePersistent(task);
        	task.updateCompositeValues();
        	task.commitHistory();
        	task = task.getParent();
    	}
    }

 }
