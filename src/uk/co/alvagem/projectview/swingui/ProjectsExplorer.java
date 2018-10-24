/*
 * ProjectsExplorer.java
 * Project: ProjectView
 * Created on 29 Dec 2007
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import uk.co.alvagem.database.DAOFactory;
import uk.co.alvagem.projectview.dao.TaskDAO;
import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.swingui.ActionSet;
import uk.co.alvagem.swingui.ExceptionDisplay;
import uk.co.alvagem.swingui.ExplorerTree;
import uk.co.alvagem.util.SettingsManager;

public class ProjectsExplorer extends ExplorerTree {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Main app;
    
    //private ExplorerTree tree;
    private ProjectsExplorerTreeModel treeModel;
    private final static String POPUPS_KEY = "/ProjectsExplorer/popups";

    /**
     */
    public ProjectsExplorer(Main app, ActionSet actions) {

        this.app = app;

        Projects projects = new Projects();
        treeModel = new ProjectsExplorerTreeModel(projects);
        setModel(treeModel);
        addTreeWillExpandListener(treeModel);

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
                                if(obj instanceof TaskProxy){
                            		Task task = ((TaskProxy)obj).getTask();
                            		editTask(ProjectsExplorer.this, task);
                                }
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

    /**
     * Edits a given task.  Here so that it can be triggered from a mouse-click, package
     * visibility so that it can be called from the explorer.
     * @param component is the parent UI component for the editor.
     * @param task is the task to be edited.
     * @throws Exception
     */
    void editTask(Component component, Task task) throws Exception{
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
    
    private void updateParents(Task task, TaskDAO dao){
    	task = task.getParent();
    	while(task != null){
        	dao.makePersistent(task);
        	task.updateCompositeValues();
        	task.commitHistory();
        	task = task.getParent();
    	}
    }
    
    /* (non-Javadoc)
	 * @see javax.swing.JComponent#getToolTipText(java.awt.event.MouseEvent)
	 */
	public String getToolTipText(MouseEvent event) {
    	String text = null;
    	int mx = event.getX();
    	int my = event.getY();
    	
        TreePath path = getPathForLocation(mx,my);
        if(path != null){
        	DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
        	Object obj = node.getUserObject();
        	if(obj instanceof Task){
        		Task task = (Task)obj;
        		text = task.getDescription();
        	}
        }
        return text;
	}

    /* (non-Javadoc)
     * @see javax.swing.JInternalFrame#dispose()
     */
    public void dispose() {
        treeModel.dispose();
    }

    Main getApp(){
        return app;
    }

    public ProjectsExplorerTreeModel getTreeModel(){
        return treeModel;
    }

	/**
	 *  Clears the explorer. 
	 */
	public void clearContents() {
		treeModel.clear();
	}


    /**
     * @return
     */
    public Task getSelectedTask() {
    	TaskProxy proxy = (TaskProxy)getSelectedNode().getUserObject();
        return proxy.getTask();
    }

}
