/*
 * ResourceSelectionEditor.java
 * Project: ProjectView
 * Created on 1 Feb 2008
 *
 */
package uk.co.alvagem.projectview.swingui.widgets;

import java.awt.Component;
import java.util.Collection;

import javax.swing.JComboBox;

import uk.co.alvagem.projectview.model.Resource;
import uk.co.alvagem.swingui.SwingForm;
import uk.co.alvagem.ui.Field;

/**
 * ResourceSelectionEditor is an editing widget for selecting one of the available resources.
 * 
 * @author rbp28668
 */
public class ResourceSelectionEditor extends SwingForm.TypeEditor {

	private Collection<Resource> resources;
	
	public ResourceSelectionEditor(Collection<Resource> resources){
		this.resources = resources;
	}
	
    @Override
    public Component getEditor(Field field) {
        Object[] options = resources.toArray();
        Resource resource = (Resource)field.getValueAsObject();
        int index = -1;
        for(int i=0; i<options.length; ++i){
            if(options[i].equals(resource)){
                index = i;
                break;
            }
        }

        JComboBox list = new JComboBox(options);
        list.setEditable(false);
        list.setSelectedIndex(index);
        return list;
    }

    @Override
    public void setFieldValue(Component component, Field field) {
        if(!(component instanceof JComboBox)){
            throw new IllegalArgumentException("Invalid component type");
        }
        JComboBox list = (JComboBox)component;
        Object selected = list.getSelectedItem();
        field.setValueObject(selected);
    }
}