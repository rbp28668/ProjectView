/**
 * 
 */
package uk.co.alvagem.projectview.swingui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import uk.co.alvagem.database.DAOFactory;
import uk.co.alvagem.projectview.core.filters.Editable;
import uk.co.alvagem.projectview.core.filters.FieldTaskFilter;
import uk.co.alvagem.projectview.core.filters.ResourceTaskFilter;
import uk.co.alvagem.projectview.model.Resource;
import uk.co.alvagem.projectview.swingui.widgets.ComparisonTypeEditor;
import uk.co.alvagem.projectview.swingui.widgets.ResourceSelectionEditor;
import uk.co.alvagem.swingui.BasicDialog;
import uk.co.alvagem.swingui.SwingForm;
import uk.co.alvagem.ui.Field;
import uk.co.alvagem.ui.FieldTemplate;
import uk.co.alvagem.ui.FormTemplate;

/**
 * @author bruce.porteous
 *
 */
public class TaskFilterItemEditor extends BasicDialog {

	/** */
	private static final long serialVersionUID = 1L;

    /** Panel for handling fields */
    private PropertiesPanel propertiesPanel;

    static {
    	SwingForm.addEditor(FieldTaskFilter.FIELD_WIDGET, new FieldTypeEditor());
    	SwingForm.addEditor(FieldTaskFilter.COMPARISON_WIDGET, new ComparisonTypeEditor());
    }
	/**
	 * @param parent
	 * @param title
	 */
	public TaskFilterItemEditor(JDialog parent, String title, Editable editable) {
		super(parent, title);
		init(editable);
	}

	/**
	 * @param parent
	 * @param title
	 */
	public TaskFilterItemEditor(Component parent, String title, Editable editable) {
		super(parent, title);
		init(editable);
	}

	/**
	 * @param editable
	 */
	private void init(Editable editable) {
        propertiesPanel = new PropertiesPanel(editable);
        getContentPane().add(propertiesPanel, BorderLayout.CENTER);
        getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);
        pack();
    }
    
    @Override
    protected void onOK() {
        propertiesPanel.onOK();
    }

    @Override
    protected boolean validateInput() {
        return propertiesPanel.validateInput();
    }
	
    private static class PropertiesPanel extends JPanel{
        
        private static final long serialVersionUID = 1L;
        private SwingForm form;
        
        /**
         * Creates a new Properties Panel.  Which properties are editable depend
         * on whether the constraint is a leaf constraint or not.
         * @param constraint
         */
        PropertiesPanel(Editable editable) {
            
            DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
            List<Resource> resources = factory.getResourceDAO().findAll();
            ResourceSelectionEditor editor = new ResourceSelectionEditor(resources);
            SwingForm.addEditor(ResourceTaskFilter.RESOURCE_WIDGET, editor);
            
        	FormTemplate template = editable.getEditTemplate();
            form = new SwingForm(template, editable);
            form.buildPanel(this);
        }
        
        /**
         * Method onOK - called when the OK button is clicked
         *  to tidy up any current edit.
         */
        void onOK() {
            form.updateObjects();
        }
        
        /**
         * Validates the properties in the model.  If invalid a message is displayed
         * and the offending row highlighted.
         * @return true if valid, false if not.
         */
        public boolean validateInput(){
            boolean valid = form.validateInput(this, "Task Filter Item Editor");
            return valid;
        }

    }

    /**
     * Editor widget to allow the user to select one of the FieldTaskFilter
     * template's fields.
     * @author bruce.porteous
     *
     */
    private static class FieldTypeEditor extends SwingForm.TypeEditor {

    	FieldTypeEditor(){
    	}
    	
        @Override
        public Component getEditor(Field field) {
            FieldTemplate template = (FieldTemplate)field.getValueAsObject();
            FormTemplate form = FieldTaskFilter.getTemplate();
            
            FieldTemplate fields[] = new FieldTemplate[form.getFields().size()];
            fields = form.getFields().toArray(fields);
            int index = -1;
            for(int i=0; i<fields.length; ++i){
                if(fields.equals(template)){
                    index = i;
                    break;
                }
            }

            JComboBox list = new JComboBox(fields);
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

}
