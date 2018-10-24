/*
 * ComparisonTypeEditor.java
 * Project: ProjectView
 * Created on 1 Feb 2008
 *
 */
package uk.co.alvagem.projectview.swingui.widgets;

import java.awt.Component;

import javax.swing.JComboBox;

import uk.co.alvagem.projectview.core.filters.Comparison;
import uk.co.alvagem.swingui.SwingForm;
import uk.co.alvagem.ui.Field;

public class ComparisonTypeEditor extends SwingForm.TypeEditor {

	public ComparisonTypeEditor(){
	}
	
    @Override
    public Component getEditor(Field field) {
        Comparison comparison = (Comparison)field.getValueAsObject();
        int index = -1;
        for(int i=0; i<Comparison.COMPARISONS.length; ++i){
            if(Comparison.COMPARISONS[i].equals(comparison)){
                index = i;
                break;
            }
        }

        JComboBox list = new JComboBox(Comparison.COMPARISONS);
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