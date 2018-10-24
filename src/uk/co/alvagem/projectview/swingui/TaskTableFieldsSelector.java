/*
 * TaskTableFieldsSelector.java
 * Project: ProjectView
 * Created on 23 Jan 2008
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import uk.co.alvagem.swingui.BasicDialog;
import uk.co.alvagem.swingui.GridPanel;
import uk.co.alvagem.ui.FieldTemplate;

public class TaskTableFieldsSelector extends BasicDialog {

 
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private SelectionPanel panel;

    public TaskTableFieldsSelector(Component parent, TaskTableModel model) {
        super(parent, "Select Fields");
        
        panel = new SelectionPanel(model);
        add(panel,BorderLayout.CENTER);
        add(getOKCancelPanel(), BorderLayout.EAST);
        
        pack();
        
    }

    @Override
    protected void onOK() {
        panel.onOK();
    }

    @Override
    protected boolean validateInput() {
        return panel.validateInput();
    }
    
    private static class SelectionPanel extends GridPanel {

        private Map<FieldTemplate,JCheckBox> checks = new HashMap<FieldTemplate,JCheckBox>();
        private TaskTableModel model;
        
        /**  */
        private static final long serialVersionUID = 1L;
        
        SelectionPanel(TaskTableModel model){
            this.model = model;
            Collection<FieldTemplate> fields = model.getAllFields();
            Collection<FieldTemplate> selected = model.getSelectedFields();
            
            for(FieldTemplate field : fields){
                JCheckBox check = new JCheckBox();
                check.setSelected(selected.contains(field));
                checks.put(field,check);
                addItem(field.getName(), check, field.getDescription());
            }
        }

        protected void onOK() {
            Set<FieldTemplate> selected = new HashSet<FieldTemplate>();
            for(Map.Entry<FieldTemplate,JCheckBox> entry : checks.entrySet()){
                if(entry.getValue().isSelected()){
                    selected.add(entry.getKey());
                }
            }
            model.setSelectedFields(selected);
        }

        protected boolean validateInput() {
            int count = 0;
            for(Map.Entry<FieldTemplate,JCheckBox> entry : checks.entrySet()){
                if(entry.getValue().isSelected()){
                    ++count;
                }
            }
            if(count == 0){
                JOptionPane.showMessageDialog(this,"Please select one or more fields","Select Fields", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return true;
        }
        
    }

}
