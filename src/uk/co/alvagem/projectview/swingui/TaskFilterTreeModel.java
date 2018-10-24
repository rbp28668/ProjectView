/*
 * TaskFilterTreeModel.java
 * Project: ProjectView
 * Created on 25 Jan 2008
 *
 */
package uk.co.alvagem.projectview.swingui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import uk.co.alvagem.projectview.core.filters.AbstractTaskFilter;
import uk.co.alvagem.projectview.core.filters.NotTaskFilter;
import uk.co.alvagem.projectview.core.filters.TaskFilter;
import uk.co.alvagem.swingui.ExplorerTreeModel;

public class TaskFilterTreeModel extends ExplorerTreeModel {

    /** */
    private static final long serialVersionUID = 1L;


    public TaskFilterTreeModel(TaskFilter rootItem) {
        super(new TaskFilterRoot(rootItem));
        init(rootItem);
    }
    
    private void init(TaskFilter rootItem){
        if(rootItem != null){
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)getRoot();
            addFilter(rootItem, root, 0);
        }
    }

    private int addFilter(TaskFilter item, DefaultMutableTreeNode parent, int idx) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
        registerNode(node, item);
        insertNodeInto(node,parent, idx++);
        addFilterChildren(item, node);
        return idx;
    }

    private void addFilterChildren(TaskFilter item, DefaultMutableTreeNode node) {
        int idx = 0;
        for(TaskFilter child : item.getChildren()){
            idx = addFilter(child, node, idx);
        }
    }
    
    public void addChild(TaskFilter selected, TaskFilter child) {
        DefaultMutableTreeNode parent = lookupNodeOf(selected);
        addFilter(child, parent, parent.getChildCount());
    }

    public void remove(TaskFilter selected) {
        DefaultMutableTreeNode node = lookupNodeOf(selected);
        removeNodeFromParent(node);
        removeNodeOf(selected);
        
    }

    public TaskFilter getFilter() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)getRoot();
        TaskFilterRoot filterRoot = (TaskFilterRoot)root.getUserObject();
        return filterRoot.getFilter();
    }

    public TreePath getPathToItem(TaskFilter filter){
    	TreeNode node = lookupNodeOf(filter);
		if(node == null){
			throw new IllegalArgumentException("Filter is not a node in the tree");
		}
		TreeNode[] path = getPathToRoot(node);
		return new TreePath(path);
    }

    private static class TaskFilterRoot extends AbstractTaskFilter implements TaskFilter{
        
        private TaskFilter child = null;

        TaskFilterRoot(TaskFilter root){
            child = root;
        }
        
        public void add(TaskFilter child) {
            this.child = child;
        }

        public boolean canAddChild() {
            return child == null;
        }

        public String toString(){
            return "Filter Tree";
        }

        public TaskFilter copy(){
            return null;
        }

        private TaskFilter getFilter(){
            return child;
        }
    }


}
