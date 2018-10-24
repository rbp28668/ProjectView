/*
 * ResourceSelectionEditor.java
 * Project: ProjectView
 * Created on 1 Feb 2008
 *
 */
package uk.co.alvagem.projectview.swingui.widgets;

import java.awt.Component;

import javax.swing.JComboBox;

import uk.co.alvagem.projectview.model.UncertaintyType;
import uk.co.alvagem.swingui.SwingForm;
import uk.co.alvagem.ui.Field;

/**
 * ResourceSelectionEditor is an editing widget for selecting one of uncertainty types.
 * @author rbp28668
 */
public class UncertaintyTypeSelectionEditor extends SwingForm.TypeEditor {

	
	public UncertaintyTypeSelectionEditor(){
	}
	
    @Override
    public Component getEditor(Field field) {
        Object[] options = UncertaintyType.getTypes().toArray();
        UncertaintyType type = (UncertaintyType)field.getValueAsObject();
        int index = -1;
        for(int i=0; i<options.length; ++i){
            if(options[i].equals(type)){
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