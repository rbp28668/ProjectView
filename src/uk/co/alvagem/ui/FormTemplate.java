/*
 * FormTemplate.java
 * Created on 13-May-2005
 *
 */
package uk.co.alvagem.ui;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * FormTemplate provides a template for a form.  FormTemplates are created once and
 * then hold information for the creation of Forms which are created, used and destroyed
 * as needed.
 * 
 * @author rbp28668
 * Created on 13-May-2005
 */
public class FormTemplate {

    /** Name of this template */
    private String name;
    
    /** List of FieldTemplates that make up this form template */
    private List<FieldTemplate> fields = new LinkedList<FieldTemplate>();
    
    /** Map of FieldTemplate keyed first by target class and then by property */
    private Map<Class<?>,Map<String,FieldTemplate>> fieldsByProperty = new HashMap<Class<?>,Map<String,FieldTemplate>>();
    
    /**
     * Creates an empty FormTemplate. This is the normal form where 
     * field templates are added using add(FieldTemplate).
     */
    public FormTemplate() {
        super();
    }

    /**
     * Provides a quick and dirty FormTemplate based on the properties of a given object.
     * @param targetClass is the Class to use as a template to create the FormTemplate.
     */
    public FormTemplate(Class<?> targetClass){
        buildFromObject(targetClass);
    }
    
    /**
     * Reflectively builds a template from a given class.
     * @param targetClass
     */
    private void buildFromObject(Class<?> targetClass){

        name = targetClass.getSimpleName();
        
        Method[] methods = targetClass.getMethods();
        for(int i=0; i<methods.length; ++i){
            Method method = methods[i];
            String methodName = method.getName();
            
            if(Modifier.isPublic(method.getModifiers()) 
                    && isGetter(methodName)
                    && method.getParameterTypes().length ==0){
                
                Class<?> propertyClass = method.getReturnType();
                if(hasSetter(targetClass, methodName, propertyClass)
                        && FieldTemplate.hasHandlerFor(propertyClass)){
                    FieldTemplate field = new FieldTemplate(targetClass, toBase(methodName), toProperty(methodName));
                    add(field);
                }
            }
        }
    }


    /**
     * Add a FieldTemplate to the FormTemplate.
     * @param field is the FieldTemplate to add.
     */
    public void add(FieldTemplate field){
        fields.add(field);
        Map<String,FieldTemplate> objMap = fieldsByProperty.get(field.getTargetClass());
        if(objMap == null){
        	objMap = new HashMap<String,FieldTemplate>();
        	fieldsByProperty.put(field.getTargetClass(),objMap);
        }
        objMap.put(field.getProperty(),field);
    }

    /**
     * Gets a FieldTemplate by its target class and property name.
     * @param target is the target class to find the field for.
     * @param property is the property name to look up.
     * @return the corresponding FieldTemplate or null if no match.
     */
    public FieldTemplate lookupField(Class<?>target, String property){
        Map<String,FieldTemplate> objMap = fieldsByProperty.get(target);
        if(objMap != null){
        	return objMap.get(property);
        }
        return null;
    }
    
    /**
     * Gets the name of this form template.
     * @return
     */
    public String getName(){
        return name;
    }
    
    /**
     * Sets the name of this form template.
     * @param name
     */
    public void setName(String name){
        this.name = name;
    }
    
    /**
     * hasSetter
     * @param targetClass
     * @param methodName
     * @param propertyClass
     * @return
     */
    private boolean hasSetter(Class<?> targetClass, String methodName, Class<?> propertyClass) {
        try {
            methodName = toSetter(methodName);
            targetClass.getMethod(methodName, new Class<?>[]{propertyClass});
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isGetter(String methodName){
        return methodName.startsWith("get") || methodName.startsWith("is");
    }

    private String toBase(String methodName){
        if(methodName.startsWith("get")){
            methodName = methodName.substring(3);
        } else if (methodName.startsWith("is")) {
            methodName = methodName.substring(2);
        } else if(methodName.startsWith("set")){
            methodName = methodName.substring(3);
        }
        return methodName;
    }
    
    
    private String toSetter(String methodName){
        return "set" + toBase(methodName);
    }
    
    private String toProperty(String methodName){
        methodName = toBase(methodName);
        StringBuffer sb = new StringBuffer(methodName.length());
        sb.append(Character.toLowerCase(methodName.charAt(0)));
        sb.append(methodName.substring(1));
        return sb.toString();
    }

    /**
     * Creates a Form from this template, bound to the given object.
     * @param o is the object to bind the form to.
     * @return a new Form.
     * @throws PresentationException
     */
    public Form createForm(Object o) throws PresentationException{
        return updateForm(new Form(this), o);
    }
    
    /**
     * Updates a form to bind it to a given object.
     * @param form is the form to bind.
     * @param o is the object to bind to.
     * @return the updated Form.
     * @throws PresentationException
     */
    public Form updateForm(Form form, Object o) throws PresentationException{
        
        for(FieldTemplate fieldTemplate : fields){
            Field field = new Field(o, fieldTemplate);
            form.add(field);
        }
        return form;
    }
    
    /**
     * getFields gets the list of fields in this template.
     * @return List of FieldTemplate.
     */
    public List<FieldTemplate> getFields() {
        return Collections.unmodifiableList(fields);
    }
}
