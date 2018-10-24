/*
 * TaskFilterEditor.java
 * Project: ProjectView
 * Created on 25 Jan 2008
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import uk.co.alvagem.projectview.core.filters.TaskFilter;
import uk.co.alvagem.swingui.BasicDialog;

public class TaskFilterEditor extends BasicDialog {

    /**  */
    private static final long serialVersionUID = 1L;
    private TaskFilter toEdit = null;
    private TaskFilterExplorer explorer;
    private Main app;
    
    public TaskFilterEditor(JDialog parent, String title,Main app, TaskFilter root) {
        super(parent, title);
        init(app, root);
    }

    public TaskFilterEditor(Component parent, String title,Main app, TaskFilter root) {
        super(parent, title);
        init(app, root);
    }
    
    private void init(Main app, TaskFilter root){
        this.app = app;
        
        if(root != null) {
            toEdit = root.copy();
        }
        
         explorer = new TaskFilterExplorer(app,  toEdit);
         JScrollPane scroll = new JScrollPane(explorer);
         add(scroll, BorderLayout.CENTER);
         add(getOKCancelPanel(), BorderLayout.EAST);
         pack();
    }

    @Override
    protected void onOK() {
        toEdit = explorer.getModel().getFilter();
    }

    @Override
    protected boolean validateInput() {
    	TaskFilter root = explorer.getModel().getFilter();
    	if(root == null){
            JOptionPane.showMessageDialog(this,"No task filter entered",app.getAppTitle(), JOptionPane.WARNING_MESSAGE);
            return false;
    	}
        return validate(root);
    }

    /**
     * Recursive validation of the filter tree.  If the tree is invalid then
     * a message is displayed to the user and the validation stops.
     * @param filter is the tree to validate.
     * @return true if valid, false if not.
     */
    private boolean validate(TaskFilter filter){
    	String err = filter.getErrorStatus();
    	if(err != null){
            explorer.select(filter);
            JOptionPane.showMessageDialog(this,err,app.getAppTitle(), JOptionPane.WARNING_MESSAGE);
    		return false;
    	}

    	boolean valid = true;
    	for(TaskFilter child : filter.getChildren()){
    		if(!validate(child)){
    			valid = false;
    			break;
    		}
    	}
    	return valid;
    }
    
    /**
     * Gets the edited task filter.  Helper where the filter tree is copied before
     * editing.
     * @return the edited filter tree.
     */
    public TaskFilter getEditedFilter(){
        return toEdit;
    }
}
