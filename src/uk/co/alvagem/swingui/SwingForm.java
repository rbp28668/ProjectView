/*
 * SwingForm.java
 * Project: ProjectView
 * Created on 30 Dec 2007
 *
 */
package uk.co.alvagem.swingui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import uk.co.alvagem.ui.DateHandler;
import uk.co.alvagem.ui.Field;
import uk.co.alvagem.ui.Form;
import uk.co.alvagem.ui.FormTemplate;

/**
 * SwingForm
 * @author rbp28668
 */
public class SwingForm extends Form {

    /** Fields in this form */
    private Map<Field,Component> components = new HashMap<Field,Component>();
    
    /** Editors keyed by class name or widget hint (both Strings). */ 
    private static Map<String,TypeEditor> editors = new HashMap<String,TypeEditor>();
    
    private static final String ENUM_EDITOR = "EnumEditor";
    
    // Set up the type of editor controls for each of the primitive types.
    static {
        StringTypeEditor ste = new StringTypeEditor();
        // Primitive types edited as string
        editors.put(Byte.TYPE.getName(),ste);
        editors.put(Short.TYPE.getName(),ste);
        editors.put(Integer.TYPE.getName(), ste);
        editors.put(Long.TYPE.getName(), ste);
        editors.put(Float.TYPE.getName(), ste);
        editors.put(Double.TYPE.getName(), ste);
        editors.put(Character.TYPE.getName(),ste);
        
        // Box types for primitives edited as string
        editors.put(Byte.class.getName(),ste);
        editors.put(Short.class.getName(),ste);
        editors.put(Integer.class.getName(), ste);
        editors.put(Long.class.getName(), ste);
        editors.put(Float.class.getName(), ste);
        editors.put(Double.class.getName(), ste);
        editors.put(Character.class.getName(),ste);

        // Boolean, both primitive and boxed
        BooleanTypeEditor bte = new BooleanTypeEditor();
        editors.put(Boolean.TYPE.getName(),bte);
        editors.put(Boolean.class.getName(),bte);

        // Other useful classes
        editors.put(BigDecimal.class.getName(), ste);
        editors.put(String.class.getName(), new TextTypeEditor());
        editors.put(Date.class.getName(), new DateEditor());
        editors.put(ENUM_EDITOR, new EnumEditor());
    }
    
    /**
     * Creates a form from a given template.  This form is not
     * bound to any objects.
     * @param template
     */
    public SwingForm(FormTemplate template) {
    	super(template);
    }

    /**
     * Creates a form from a given template that is bound to a 
     * given object.
     * @param template
     * @param o
     */
    public SwingForm(FormTemplate template, Object o){
    	super(template);
        template.updateForm(this,o);
    }
 
    public static void addEditor(Class<?> type, TypeEditor editor){
        editors.put(type.getName(),editor);
    }

    public static void addEditor(String type, TypeEditor editor){
        editors.put(type,editor);
    }

    public void buildPanel(JPanel panel){
        
        GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;
        // One of the following confuses the layout:
        //c.ipadx = 10;
        //c.insets = new Insets(5,2,5,2);
        
        panel.setBorder(new StandardBorder());
        
        Component firstEdit = null;
        
        for(Field field : getFields()){
            
            c.gridwidth = 1; // start of row.
            c.fill = GridBagConstraints.HORIZONTAL;
            
            
            JLabel name = new JLabel(field.getName());
            name.setToolTipText(field.getDescription());
            layout.setConstraints(name,c);
            panel.add(name);
            
            TypeEditor editor = getEditor(field);

            Component component = editor.getEditor(field);
            if(firstEdit == null){
                firstEdit = component;
            }

            c.fill = GridBagConstraints.NONE;
            c.gridwidth = GridBagConstraints.REMAINDER; //end row
            layout.setConstraints(component,c);
            panel.add(component);
            
            components.put(field,component);
        }
        c.gridwidth = 1;
        c.weighty = 1.0f;
        JLabel padding = new JLabel();
        layout.setConstraints(padding,c);
        panel.add(padding);
        
        if(firstEdit != null){
            firstEdit.requestFocus();
        }

    }

    /**
     * Gets the TypeEditor for a given field.  By default this is determined
     * by the property class but can be over-ridden by the widget-hint in 
     * the the template (accessed via the field).
     * @param field is the field to get the editor for.
     * @return a corresponding TypeEditor.
     * @throws IllegalArgumentException if a TypeEditor can't be found for the 
     * given field.
     */
    private TypeEditor getEditor(Field field) {
        String select = field.getWidgetType();
        if(select == null){
          if(field.getPropertyClass().isEnum()) {
            select = ENUM_EDITOR;
          } else {
            select = field.getPropertyClass().getName();
          }
        }
        
        TypeEditor editor = editors.get(select);
        if(editor == null){
            throw new IllegalArgumentException("Can't find SwingForm editor for " 
                    + select);
        }
        return editor;
    }
    


    /**
     * updateObject should be called once the input validates to update
     * the underlying object.
     */
    void updateObject() {
        for(Map.Entry<Field, Component> entry: components.entrySet()){
            Field f = entry.getKey();
            Component c = entry.getValue();
            TypeEditor editor = getEditor(f);
            editor.setFieldValue(c,f);
            f.updateObject();
        }
    }

    /**
     * Validates the properties in the model.  If invalid a message is displayed
     * and the offending row highlighted.
     * @return true if valid, false if not.
     */
    public boolean validateInput(JPanel panel, String warningTitle){
        boolean valid = true;
        for(Map.Entry<Field, Component> entry: components.entrySet()){
            Field f = entry.getKey();
            Component c = entry.getValue();
            
            TypeEditor editor = getEditor(f);
            editor.setFieldValue(c,f);
            if(f.hasError()){
                JOptionPane.showMessageDialog(panel,f.getError(),warningTitle, JOptionPane.WARNING_MESSAGE);
                c.requestFocusInWindow();
                valid = false;
                break;

            }
        }

        return valid;
    }

    
    /**
     * TypeEditor is an abstract class for supplying and reading from, a component
     * on the form.  Sub-classes can provide the appropriate component for the type
     * of data to be edited.
     */
    public static abstract class TypeEditor{
        public abstract Component getEditor(Field field);
        public abstract void setFieldValue(Component component, Field field);
    }
 
    /**
     * StringTypeEditor is an editor for all those types which 
     * are edited as a single line of text.
     */
    public static class StringTypeEditor extends TypeEditor{

        @Override
        public Component getEditor(Field field) {
            JTextField text = new JTextField();
            text.setText(field.getValue());
            text.setColumns(field.getLength());
            return text;
        }

        @Override
        public void setFieldValue(Component component, Field field) {
            field.setValue(((JTextField)component).getText());
        }
        
    }
    
    /**
     * TextTypeEditor is an editor for strings - if the string is > 255 characters
     * then a TextArea is used.
     * 
     */
    public static class TextTypeEditor extends TypeEditor{

        @Override
        public Component getEditor(Field field) {
            Component component;
            if(field.getMaxlen() <= 255 ){ // arbitrary at the moment
                JTextField text = new JTextField();
                text.setText(field.getValue());
                text.setColumns(field.getLength());
                component = text;
            } else {
                JTextArea text = new JTextArea();
                text.setText(field.getValue());
                text.setLineWrap(true);
                text.setWrapStyleWord(true);
                text.setColumns(40);
                text.setRows(5);
                
                JScrollPane scroll = new JScrollPane();
                scroll.setViewportView(text);
                component = scroll;
            }
            return component;
        }

        @Override
        public void setFieldValue(Component component, Field field) {
            String value = null;
            if(component instanceof JTextField){
                value = ((JTextField)component).getText();
            } else if (component instanceof JTextArea){
                value = ((JTextArea)component).getText();
            } else if (component instanceof JScrollPane){
            	JScrollPane scroll = (JScrollPane)component;
            	JTextArea text = (JTextArea)scroll.getViewport().getView();
                value = text.getText();
            } else {
                throw new IllegalStateException("can't get value from " + component.getClass().getName());
            }
            field.setValue(value);
        }
        
    }
    
    /**
     * BooleanTypeEditor edits boolean variables using a checkbox.
     */
    public static class BooleanTypeEditor extends TypeEditor {

        @Override
        public Component getEditor(Field field) {
            JCheckBox check = new JCheckBox();
            String value = field.getValue();
            boolean set = value.equals("on") || value.equals("true") || value.equals('y');
            check.setSelected(set);
            return check;
        }

        @Override
        public void setFieldValue(Component component, Field field) {
            field.setValue(((JCheckBox)component).isSelected() ? "on" : "off");
        }
        
    }
    
    private static class DateEditor extends SwingForm.TypeEditor {

        @Override
        public Component getEditor(Field field) {
            DatePicker panel = new DatePicker();
            Date when = new Date();
            try {
                when = DateHandler.FMT.parse(field.getValue());
            } catch (ParseException e) {
                ;// NOP use todays date.
            }
            panel.setDate(when);
            return panel;
        }

        @Override
        public void setFieldValue(Component component, Field field) {
            if(!(component instanceof DatePicker )){
                throw new IllegalArgumentException("Invalid component type");
            }
            
            DatePicker panel = (DatePicker)component;
            Date when = panel.getDate();
            field.setValue(DateHandler.FMT.format(when));
        }
        
    }
    
    private static class EnumEditor extends SwingForm.TypeEditor {

      @Override
      public Component getEditor(Field field) {
          
        Class<Enum<?>> enumClass = (Class<Enum<?>>)field.getPropertyClass();
          Enum<?> value = (Enum<?>)field.getValueAsObject();
          int index = -1;
          if(value != null) {
            int i = 0;
            for(Enum<?> op : enumClass.getEnumConstants()) {
              if(op == value) {
                    index = i;
                    break;
                }
              ++i;
            }
          }

          JComboBox<Enum<?>> list = new JComboBox<>(enumClass.getEnumConstants());
          list.setEditable(false);
          list.setSelectedIndex(index);
          return list;
      }

      @Override
      public void setFieldValue(Component component, Field field) {
          if(!(component instanceof JComboBox)){
              throw new IllegalArgumentException("Invalid component type");
          }
          JComboBox<Enum<?>> list = (JComboBox<Enum<?>>)component;
          field.setValueObject(list.getSelectedItem());
      }
      
  }

    
}
