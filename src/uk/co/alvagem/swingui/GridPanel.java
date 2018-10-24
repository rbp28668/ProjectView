/*
 * GridPanel.java
 * Project: ProjectView
 * Created on 11 Jan 2008
 *
 */
package uk.co.alvagem.swingui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * GridPanel is a JPanel with built-in support for laying out a 2 column
 * label - component grid using a GridBagLayout.
 * 
 * @author rbp28668
 */
public class GridPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private GridBagLayout layout;
    private GridBagConstraints c;
    private Component firstEdit = null;
    
    public GridPanel() {
        layout = new GridBagLayout();
        setLayout(layout);
        c = new GridBagConstraints();
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;
        c.ipadx = 10;
        c.insets = new Insets(5,2,5,2);
        
        setBorder(new StandardBorder());
    }

    public void addItem(String label, Component component){
        addItem(label, component, null);
    }

    public void addItem(String label, Component component, String tooltip){
        
        c.gridwidth = 1; // start of row.
        c.fill = GridBagConstraints.HORIZONTAL;
        
        
        JLabel name = new JLabel(label);
        if(tooltip != null){
            name.setToolTipText(tooltip);
        }
        layout.setConstraints(name,c);
        add(name);

        if(firstEdit == null && component.isFocusable()){
            firstEdit = component;
        }

        c.fill = GridBagConstraints.NONE;
        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(component,c);
        add(component);
    }
    
    public void setIntialFocus(){
        if(firstEdit != null){
            firstEdit.requestFocus();
        }
    }
}
