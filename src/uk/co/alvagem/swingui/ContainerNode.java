/*
 * ContainerNode.java
 * Project: ProjectView
 * Created on 1 Jan 2008
 *
 */
package uk.co.alvagem.swingui;

import javax.swing.tree.DefaultMutableTreeNode;

public class ContainerNode extends DefaultMutableTreeNode {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ContainerNode(Object obj){
        super(obj);
    }
    public boolean getAllowsChildren() {
        return true;
    }
    
    public boolean isLeaf() {
        return false;
    }
 
}
