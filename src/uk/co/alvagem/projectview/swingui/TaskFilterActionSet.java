/*
 * TaskFilterActionSet.java
 * Project: ProjectView
 * Created on 25 Jan 2008
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import uk.co.alvagem.database.DAOFactory;
import uk.co.alvagem.projectview.core.filters.Editable;
import uk.co.alvagem.projectview.core.filters.FilterFactory;
import uk.co.alvagem.projectview.core.filters.TaskFilter;
import uk.co.alvagem.swingui.ActionSet;
import uk.co.alvagem.swingui.ExceptionDisplay;

/**
 * Action set to allow editing of the task-filter tree.
 * @author bruce.porteous
 *
 */
public class TaskFilterActionSet extends ActionSet {

    private TaskFilterExplorer explorer;
    private Main app;
    
    /**
     * Creates the action set.
     * @param app is the application.
     * @param explorer is the explorer in which the task-filter tree is displayed.
     */
    TaskFilterActionSet(Main app, TaskFilterExplorer explorer){
        this.app = app;
        this.explorer = explorer;
        
        addAction("AddFilter", actionAddFilter);
        addAction("EditFilter", actionEditFilter);
        addAction("DeleteFilter", actionDeleteFilter);
    }
    
    /**
     * Helper method to show an exception to the user.
     * @param t is the Throwable to display.
     */
    private void showException(Throwable t){
        new ExceptionDisplay(explorer, app.getAppTitle(),t);
    }
    
    /**
     * Selects a filter type from amongst the available type.  Needed
     * for creating a new node in the filter tree.
     * @param types is the Collection of type names.
     * @return The selected type name or null if the user cancels.
     */
    private String selectFilter(Collection<String> types) {
        String[] options = types.toArray(new String[types.size()]);
        String selected = null;
        if(options.length == 1){
            selected = options[0];
        } else if(options.length > 1) {
            Arrays.sort(options);
            selected = (String)JOptionPane.showInputDialog(
                explorer,  "Select Filter", app.getAppTitle(),
                JOptionPane.QUESTION_MESSAGE, null,
                options, null
            );
        }
        return selected;
    }

    /** Adds a new filter to the selected node */
    private final Action actionAddFilter    = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                TaskFilter selected = explorer.getSelectedFilter();
                
                Set<String> types = FilterFactory.getFilterTypes();
                String type = selectFilter(types);
                if(type != null){
                	if(selected.canAddChild()){
	                    TaskFilter child = FilterFactory.newFilter(type);
	                    
	                    if(child instanceof Editable){
	                    	Editable editable = (Editable)child;
	                        DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
	                        factory.beginTransaction();
	                        TaskFilterItemEditor editor;
	                        try{
	                            editor = new TaskFilterItemEditor(explorer, "New filter item", editable);
	                            factory.commit();
	                        } catch (Exception x){
	                            factory.rollback();
	                            throw x;
	                        }
	                        editor.setVisible(true);
	                    	editor.setVisible(true);
	                    	if(editor.wasEdited()){
	    	                    selected.add(child);
	    	                    explorer.getModel().addChild(selected, child);
	                    	}
	                    } else { // not editable so just add it.
    	                    selected.add(child);
    	                    explorer.getModel().addChild(selected, child);
	                    }
                	}
                }
            } catch(Throwable t) {
                showException(t);
            }
        }
    };   

    /** Edits the filter at the selected node if it is editable */
    private final Action actionEditFilter    = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                TaskFilter selected = explorer.getSelectedFilter();
                
                if(selected instanceof Editable){
                	Editable editable = (Editable)selected;
                	DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                	factory.beginTransaction();
                	TaskFilterItemEditor editor;
                	try{
                	    editor = new TaskFilterItemEditor(explorer, "Edit filter item", editable);
                	    factory.commit();
                	} catch (Exception x){
                	    factory.rollback();
                	    throw x;
                	}
                	editor.setVisible(true);
                }
            } catch(Throwable t) {
                showException(t);
            }
        }
    };   

    /** Deletes the filter at the selected node */
    private final Action actionDeleteFilter    = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                TaskFilter selected = explorer.getSelectedFilter();
                if(selected != null){
                    TaskFilter parent = selected.getParent();
                    if(parent != null){
                        parent.remove(selected);
                    }
                    explorer.getModel().remove(selected);
                }
            } catch(Throwable t) {
                showException(t);
            }
        }
    };   

    
    
    
}
