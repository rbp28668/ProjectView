/*
 * TaskFilterExplorer.java
 * Project: ProjectView
 * Created on 25 Jan 2008
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import uk.co.alvagem.projectview.core.filters.TaskFilter;
import uk.co.alvagem.swingui.ActionSet;
import uk.co.alvagem.swingui.ExceptionDisplay;
import uk.co.alvagem.swingui.ExplorerTree;
import uk.co.alvagem.util.SettingsManager;

public class TaskFilterExplorer extends ExplorerTree {

    /**   */
    private static final long serialVersionUID = 1L;

    private Main app;
    private TaskFilterTreeModel treeModel;
    private final static String POPUPS_KEY = "/TaskFilterExplorer/popups";

    public TaskFilterExplorer(Main app, TaskFilter root) {

        this.app = app;
        
        treeModel = new TaskFilterTreeModel(root);
        setModel(treeModel);
 
        ActionSet actions = new TaskFilterActionSet(app, this);
        SettingsManager.Element cfg = app.getConfig().getElement(POPUPS_KEY);
        setPopups(cfg,actions);

        setToolTipText("*");
        
        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow = getRowForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    if (e.getClickCount() == 2) {

                        DefaultMutableTreeNode node = getSelectedNode();
                        if(node != null){
                            try{
                                Object obj = node.getUserObject();
                                // Do something with obj
                            } catch (Exception ex){
                                new ExceptionDisplay(getApp().getCommandFrame(),getApp().getAppTitle(),ex); 
                            }
                        }
                    }
                }
            }
        };
        addMouseListener(ml);

    }

    private Main getApp(){
        return app;
    }

    public TaskFilter getSelectedFilter() {
        return (TaskFilter)getSelectedItem();
    }
    
    public TaskFilterTreeModel getModel(){
        return treeModel;
    }

	/**
	 * Selects a given filter in the tree.
	 * @param filter
	 */
	public void select(TaskFilter filter) {
		TreePath path = treeModel.getPathToItem(filter);
		makeVisible(path);
		setLeadSelectionPath(path);
	}


}
