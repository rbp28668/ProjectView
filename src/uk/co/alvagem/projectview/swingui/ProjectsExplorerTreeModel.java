/*
 * ProjectsExplorerTreeModel.java
 * Project: ProjectView
 * Created on 29 Dec 2007
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.awt.Cursor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import uk.co.alvagem.database.DAOFactory;
import uk.co.alvagem.projectview.dao.TaskDAO;
import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.swingui.ContainerNode;
import uk.co.alvagem.swingui.ExceptionDisplay;
import uk.co.alvagem.swingui.ExplorerTreeModel;

public class ProjectsExplorerTreeModel extends ExplorerTreeModel implements TreeWillExpandListener {

    //private Projects projects;
    
    /**  */
    private static final long serialVersionUID = 1L;

    public ProjectsExplorerTreeModel(Projects projects) {
        super(projects);
        //this.projects = projects;
    }

    public void addTopLevelProject(Task task) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)getRoot();
        DefaultMutableTreeNode node = new ContainerNode(new TaskProxy(task));
        registerNode(node,task);
        int idx = root.getChildCount();
        insertNodeInto(node, root, idx);
    }
    
    Set<Task> getTopLevelProjects(){
        Set<Task>top = new HashSet<Task>();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)getRoot();
        int childCount = root.getChildCount();
        for(int i=0; i<childCount; ++i){
            DefaultMutableTreeNode child = (DefaultMutableTreeNode)root.getChildAt(i);
            TaskProxy proxy = (TaskProxy)child.getUserObject();
            top.add(proxy.getTask());
        }
        return top;
    }
    
    /**
     * Removes the node corresponding to a task from the tree. 
     * @param task
     */
    public void removeTask(Task task) {
        DefaultMutableTreeNode node = lookupNodeOf(task);
        if(node != null){
            removeNodeFromParent(node);
            removeNodeOf(task);
        }
    }
    

    public void addChildTask(Task parent, Task task) {
        DefaultMutableTreeNode parentNode = lookupNodeOf(parent);
        DefaultMutableTreeNode node = new ContainerNode(new TaskProxy(task));
        registerNode(node,task);
        int idx = parentNode.getChildCount();
        insertNodeInto(node,parentNode,idx);
    }
    
    public void insertTaskAfter(Task predecessor, Task task){
        DefaultMutableTreeNode predecessorNode = lookupNodeOf(predecessor);
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)predecessorNode.getParent();
        int idx = 1 + parentNode.getIndex(predecessorNode);
        DefaultMutableTreeNode node = new ContainerNode(new TaskProxy(task));
        registerNode(node,task);
        insertNodeInto(node,parentNode,idx);
    }

    public void swapAdjacentTasks(Task first, Task second){
        DefaultMutableTreeNode firstNode = lookupNodeOf(first);
        DefaultMutableTreeNode secondNode = lookupNodeOf(second);
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)firstNode.getParent();
        DefaultMutableTreeNode parentNode2 = (DefaultMutableTreeNode)secondNode.getParent();
        if(parentNode != parentNode2){  // paranoid?  Moi?
            throw new IllegalArgumentException("Can't swap tasks that have different parents");
        }
        // Remove second and re-insert at position of first.
        int index = parentNode.getIndex(firstNode);
        int i2 = parentNode.getIndex(secondNode);
        if(index + 1 != i2){
            throw new IllegalArgumentException("Nodes are not adjacent or order is reversed");
        }

        removeNodeFromParent(secondNode);
        insertNodeInto(secondNode,parentNode,index);
    }
    
    /**
     * Clears the tree model.
     */
    public void clear(){
    	removeAll();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)getRoot();
        root.removeAllChildren();
        reload(root);
    }
    
    public void treeWillCollapse(TreeExpansionEvent event)
            throws ExpandVetoException {
    }

    public void treeWillExpand(TreeExpansionEvent event)
            throws ExpandVetoException {
        JTree source = (JTree)event.getSource();
        TreePath path = event.getPath();
        Object last = path.getLastPathComponent();
         
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)last;
        Object item = node.getUserObject();
        source.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            if(item instanceof TaskProxy) {
                if(node.getChildCount() == 0){
                    Task task = ((TaskProxy)item).getTask();
                    addTaskChildren(node,task);
                }
            }
        } catch (Throwable t) {
            SwingUtilities.invokeLater(new ExceptionHandler(source,t));
            throw new ExpandVetoException(event);
        } finally {
            source.setCursor(Cursor.getDefaultCursor());
        }
        //System.out.println("Expanded");
    }

    private void addTaskChildren(DefaultMutableTreeNode parentNode, Task task) throws Throwable{
        DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
        factory.beginTransaction();
        TaskDAO dao = factory.getTaskDAO();
        try {
            dao.makePersistent(task);   // attach to DB
            List<Task> children = task.getSubTasks();
            int idx = 0;
            for(Task child : children){
                DefaultMutableTreeNode node = new ContainerNode(new TaskProxy(child));
                registerNode(node,child);
                insertNodeInto(node, parentNode, idx);
                ++idx;
            }
            factory.commit();
        } catch (Throwable t) {
            factory.rollback();
            throw t;
        }
    }

    private static class ExceptionHandler implements Runnable{
        private JTree source;
        private Throwable t;
        
        ExceptionHandler(JTree source, Throwable t){
            this.source = source;
            this.t = t;
        }

        public void run() {
            new ExceptionDisplay(source,"Project View",t);
        }
    }

    
}
