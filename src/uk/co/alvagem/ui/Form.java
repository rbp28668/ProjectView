/*
 * Form.java
 * Created on 06-May-2005
 * By Bruce.Porteous
 *
 */
package uk.co.alvagem.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Form binds a form template to a specific object (or objects).
 * @author rbp28668
 *
 */
public class Form {

	/** Map of Map of Fields.  The outer map is keyed by object, the inner by property */
	private Map<Object,Map<String,Field>> fields = new HashMap<Object,Map<String,Field>>(); // of map of Field.
	
	/** All the different Fields in this form*/
	private List<Field> fieldList = new LinkedList<Field>();
	
	private FormTemplate template;
	
	/**
	 * Creates an empty form. 
	 */
	public Form(FormTemplate template) {
		super();
		this.template = template;
	}

	/**
	 * Allows one form to be bound to multiple objects.
	 * @param o is the object to bind to.
	 */
	public void bindTo(Object o){
		template.updateForm(this,o);
	}
	
	/**
	 * Gets the field ID for a given object and property.
	 * @param o is the object to get an ID for.
	 * @param property is the required property of that object.
	 * @return a String ID for that field.
	 * @throws PresentationException
	 */
	public String getId(Object o, String property) throws PresentationException{
		Field field = getField(o,property);
		return field.getId();
	}
	
	/**
	 * Gets the value of a given property of an object.
	 * @param o is the object to get a property for.
	 * @param property is the required property of the object.
	 * @return the value of the object's property as a String.
	 * @throws PresentationException
	 */
	public String getValue(Object o, String property)  throws PresentationException {
		Field field = getField(o,property);
		return field.getValue();
	}
	
// TODO move to WebForm	
//	/**
//	 * @param request
//	 */
//	public boolean updateFrom(HttpServletRequest request) throws PresentationException{
//	    boolean isValid = true;
//		for(Iterator iter = fieldList.iterator(); iter.hasNext();){
//			Field field = (Field)iter.next();
//			String val = request.getParameter(field.getId());
//			isValid &= field.setValue(val);
//		}
//		return isValid;
//	}

	public void updateObjects() throws PresentationException {
		for(Field field : fieldList){
			field.updateObject();
		}
	}
	
	public List<Field> getFields(){
	    return Collections.unmodifiableList(fieldList);
	}
	
	public void add(Field field){
	    Object o = field.getObject();
	    String property = field.getProperty();
	    
		Map<String,Field> fieldsOfObject =  fields.get(o);
		if(fieldsOfObject == null) {
			fieldsOfObject = new HashMap<String,Field>();
			fields.put(o,fieldsOfObject);
		}
		
		fieldsOfObject.put(property,field);
		fieldList.add(field);
	}
	
	/**
	 * Gets a new field for the given object and property.  If a field for the
	 * given object and property exists then it is returned, otherwise a new
	 * field is created.
	 * @param o is the object this field is for.
	 * @param property is the property name for this field.
	 * @return a Field corresponding to the given object and property.
	 * @throws PresentationException
	 */
	private Field getField(Object o, String property) throws PresentationException {

		Map<String,Field> fieldsOfObject =  fields.get(o);
		if(fieldsOfObject == null) {
			fieldsOfObject = new HashMap<String,Field>();
			fields.put(o,fieldsOfObject);
		}
		
		Field field = fieldsOfObject.get(property);
		if(field == null){
			FieldTemplate ft = template.lookupField(o.getClass(), property);
			field = new Field(o,ft);
			fieldsOfObject.put(property,field);
			fieldList.add(field);
		}
		return field;
	}
}
