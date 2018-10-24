/*
 * Field.java
 * Created on 13-May-2005
 *
 */
package uk.co.alvagem.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;



public class Field {
    
		private static int fieldIndex = 0;
		private Object object;		// object this field belongs to.
		private String property;	// name of property field maps to.
		private Class<?> propClass;	// type of the property.
		private Object value;		// current value for this field, usually a string
		private String id;			// for web page.
		private String error;	    // non-null if this field in error.
		private FieldTemplate template; // Template for this field.	
	
		/**
		 * Creates a new field for a given object and property of that object as
		 * described by a FieldTemplate.
		 * @param o
		 * @param template
		 * @throws PresentationException
		 */
		public Field(Object o, FieldTemplate template) throws PresentationException{
		    init(o, template.getProperty(), template);
		}

		
		/**
		 * Creates a new Field for the given object and property of that object.  The field is 
		 * initialized with the value of the object and the property type is stored to allow
		 * the value to be written back.
		 * @param o
		 * @param property
		 * @throws PresentationException
		 */
		private void init(Object o, String property, FieldTemplate template) throws PresentationException  {
			this.object = o;
			this.property = property;
			this.template = template;
			this.id = createId();
			try {
				Method m = template.getGetter();
                this.propClass = m.getReturnType();
				
				Object retval = m.invoke(o,(Object[])null);
				//System.out.println(property + " --> " + (retval == null ? "NULL" : retval.toString()));
			    TypeHandler handler = template.getHandler();
			    if(handler instanceof ObjectHandler) {
			    	this.value = retval;
			    } else {
					if(retval != null){
					    this.value = handler.getValue(retval);
					} else {
					    this.value = "";
					}
			    }
			} catch (SecurityException e) {
				throw new PresentationException("Security prevents getting value of property " + property + " from " + o.getClass().getName(),e);
			} catch (IllegalArgumentException e) {
				throw new PresentationException("Cannot call zero argument form of property " + property + " from " + o.getClass().getName(),e);
			} catch (IllegalAccessException e) {
				throw new PresentationException("Illegal Access getting value of property " + property + " from " + o.getClass().getName(),e);
			} catch (InvocationTargetException e) {
				throw new PresentationException("Invalid invocation target prevents getting value of property " + property + " from " + o.getClass().getName(),e);
			}
		}

		
		/**
		 * Updates the underlying object from the saved value.  This should only be
		 * called if the fields have been validated.
		 * @throws PresentationException if the handlers are incorrect or a field value
		 * is invalid for the field's type.
		 */
		public void updateObject() throws PresentationException {
			
			TypeHandler handler = template.getHandler();
			Object param;
			// TODO handler needs to be updated so that it can just parse value and that will work in all cases. Remove this if(handler...)
			if(handler instanceof ObjectHandler){
				param = value;
			} else {
				param = handler.parse((String)value); 
			}
			
			// Can't have nulls for primitive types so...
			if(param == null && propClass.isPrimitive()) {
				throw new PresentationException("Invalid value for property " + property + ": " + value);
			}
			
			try {
				Object[] params = new Object[]{param};
				template.getSetter().invoke(object,params);
			} catch (IllegalArgumentException e) {
				throw new PresentationException(e.getMessage());
			} catch (IllegalAccessException e) {
				throw new PresentationException(e.getMessage());
			} catch (InvocationTargetException e) {
				throw new PresentationException(e.getMessage());
			}
			
		}

		/**
		 * Creates a unique Id for the field.
		 * @return String containing a unique value.
		 */
		private synchronized String createId(){
			++fieldIndex;
			
			// Just 'cos you're paranoid.....
			if(fieldIndex == Integer.MAX_VALUE) {
				fieldIndex = 0;
			}
			
			String result = "x" + Integer.toString(fieldIndex,16);
			return result;
		}
		
		/**
		 * Gets the index of the field.
		 * @return Returns the fieldIndex.
		 */
		public static int getFieldIndex() {
			return fieldIndex;
		}
		
		/**
		 * @param fieldIndex The fieldIndex to set.
		 */
		public static void setFieldIndex(int fieldIndex) {
			Field.fieldIndex = fieldIndex;
		}
		
		/**
		 * Gets the unique (String) Id of this field.  Used for web-forms to name controls.
		 * @return Returns the id.
		 */
		public String getId() {
			return id;
		}
		
		/**
		 * Gets the object this field is bound to.
		 * @return Returns the object.
		 */
		public Object getObject() {
			return object;
		}
		
		/**
		 * @return Returns the property.
		 */
		public String getProperty() {
			return property;
		}

		/**
		 * @return Returns the value.
		 */
		public String getValue() {
			return (String)value;
		}
		
		/**
		 * @param value The value to set.
		 */
		public boolean setValue(String value) {
			this.value = value;
			
			TypeHandler handler = template.getHandler();
			
			// No text is fine, unless field marked as required.
			if(value == null || value.length() == 0){
			    if(isRequired()){
			        setError(Validator.REQUIRED.getErrorText());
			        return false;
			    }
			} else { // some text.....
			    
			    // Use template to validate.
			    clearError();
		        String err = template.validate(value);
		        if(err != null) {
		            setError(err);
		            return false;
		        }

			    // Finally, ensure that the handler will parse correctly.
			    Object param = handler.parse(value);
				if(param == null){
				    setError("Please enter a valid value");
				    return false;
				}
			}
			return true;
		}
		
		/**
		 * @return
		 */
		public Object getValueAsObject(){
			return value;
		}
		
		/**
		 * @param value
		 * @return
		 */
		public boolean setValueObject(Object value){
			this.value = value;
			return true;
		}
		
		/**
		 * Get the class of the field's object & property.
		 * @return Returns the property's class
		 */
		public Class<?> getPropertyClass() {
			return propClass;
		}

	    /**
	     * Gets the length to be used for the input field.  The maximum number of
	     * characters may be greater than this.
	     * @return Returns the length.
	     */
	    public int getLength() {
	        return template.getLength();
	    }
	    
	    /**
	     * Get the maximum number of characters that may be stored in this field.
	     * @return Returns the maxlen.
	     */
	    public int getMaxlen() {
	        return template.getMaxlen();
	    }
	    
	    /**
	     * Gets a string that can be used for a field label on the UI.
	     * @return Returns the name.
	     */
	    public String getName() {
	        return template.getName();
	    }

	    /**
	     * Gets some descriptive text for this field for use in tool-tips etc.
	     * @return descriptive text or null if none available.
	     */
	    public String getDescription(){
	        return template.getDescription();
	    }
	    
        /**
         * @return Returns the error.
         */
        public String getError() {
            return error;
        }
        /**
         * @param error The error to set.
         */
        public void setError(String error) {
            this.error = error;
        }
        
        /**
         * clearError clears any error status for this field.
         * 
         */
        public void clearError(){
            this.error = null;
        }
        
        /**
         * hasError determines if the field is in error.
         * @return
         */
        public boolean hasError(){
            return this.error != null;
        }
        
        /**
         * isRequired determines if the field is required.
         * @return
         */
        public boolean isRequired() {
            return template.isRequired();
        }

        /**
         * Gets the name of the widget to use for displaying this field. This allows
         * a template to over-ride the default widget for the data type this field
         * accesses.
         * @return A widget type name or null if none specified.
         */
        public String getWidgetType() {
            return template.getWidgetType();
        }
	}