/*
 * FieldTemplate.java
 * Created on 13-May-2005
 *
 */
package uk.co.alvagem.ui;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * FieldTemplate describes a single field of the form.
 * 
 * @author rbp28668 Created on 13-May-2005
 */
public class FieldTemplate {

  /** The name of the field to be used as a label */
  private String name;

  /** Descriptive text that can be used for tool-tips etc. */
  private String description;

  /**
   * The name of the property of the object to be bound to. The property should be
   * accessible by the normal getters & setters.
   */
  private String property;

  /** The desired field length on the display */
  private int length = 0;

  /** The maximum length of the input (for strings at least */
  private int maxlen = 0;

  /** Whether a value is required or not */
  private boolean required = false;

  /** Class of the object the property belongs to */
  private Class<?> targetClass;

//    /** Class of the property value */
//    private Class<?> propertyClass;

  /** TypeHandler to be used for associated fields */
  private TypeHandler handler;

  /** Getter method to use for this property. */
  private Method getter;

  /** Setter method to use for this property. */
  private Method setter;

  /**
   * Name of widget type to be used as a hint on the type of component to edit
   * this field
   */
  private String widgetType;

  /** Validators to be used on this field */
  private List<Validator> validation = new LinkedList<Validator>();

  private static Map<String, Validator> defaultValidation = new HashMap<String, Validator>();
  static {
    defaultValidation.put(Boolean.TYPE.getName(), new BooleanValidator());
    defaultValidation.put(Byte.TYPE.getName(), new ByteValidator());
    defaultValidation.put(Character.TYPE.getName(), new CharValidator());
    defaultValidation.put(Double.TYPE.getName(), new DoubleValidator());
    defaultValidation.put(Float.TYPE.getName(), new FloatValidator());
    defaultValidation.put(Integer.TYPE.getName(), new IntegerValidator());
    defaultValidation.put(Long.TYPE.getName(), new LongValidator());
    defaultValidation.put(Short.TYPE.getName(), new ShortValidator());
    
    defaultValidation.put(Boolean.class.getName(), new BooleanValidator());
    defaultValidation.put(Byte.class.getName(), new ByteValidator());
    defaultValidation.put(Character.class.getName(), new CharValidator());
    defaultValidation.put(Double.class.getName(), new DoubleValidator());
    defaultValidation.put(Float.class.getName(), new FloatValidator());
    defaultValidation.put(Integer.class.getName(), new IntegerValidator());
    defaultValidation.put(Long.class.getName(), new LongValidator());
    defaultValidation.put(Short.class.getName(), new ShortValidator());
    
    defaultValidation.put(Date.class.getName(), new DateValidator());
    defaultValidation.put(String.class.getName(), new StringValidator());
    defaultValidation.put(BigDecimal.class.getName(), new BigDecimalValidator());
  }

  public static final String OBJECT = "ObjectHandler";

  /** Handlers for parsing updates */
  private static Map<String, TypeHandler> handlers = new HashMap<String, TypeHandler>();

  /** handlers to map primitive types to their boxed types */
  private static Map<Class<?>, Class<?>> boxTypes = new HashMap<>();

  // Maps primitive types to their boxed equivalents
  
  static {
    boxTypes.put(Boolean.TYPE, Boolean.class);
    boxTypes.put(Byte.TYPE, Byte.class);
    boxTypes.put(Short.TYPE, Short.class);
    boxTypes.put(Integer.TYPE, Integer.class);
    boxTypes.put(Long.TYPE, Long.class);
    boxTypes.put(Float.TYPE, Float.class);
    boxTypes.put(Double.TYPE, Double.class);
    boxTypes.put(Character.TYPE, Character.class);
  }


  static {
    register(new ByteHandler());
    register(new ShortHandler());
    register(new IntegerHandler());
    register(new LongHandler());
    register(new FloatHandler());
    register(new DoubleHandler());
    register(new CharHandler());
    register(new StringHandler());
    register(new BooleanHandler());
    register(new DateHandler());
    register(new BigDecimalHandler());
    register(new ObjectHandler(), OBJECT);
    registerBoxTypes();
  }

  // Re-registers the handlers for primitive types against their boxed types.
  private static void registerBoxTypes() {
    for(Map.Entry<Class<?>, Class<?>> type : boxTypes.entrySet()) {
      TypeHandler handler = handlers.get(type.getKey().getName()); // get handler for primitive type.
      assert(handler != null);
      System.out.println("Adding handler for " + type.getValue().getName() + " -> " + handler.getTargetClass().getName());
      handlers.put(type.getValue().getName(), handler);
    }
    
  }

  /**
   * Register a handler for parsing a particular type of input.
   * 
   * @param handler is the handler to register.
   */
  public static void register(TypeHandler handler) {
    handlers.put(handler.getTargetClass().getName(), handler);
  }


  /**
   * For handlers for "funny" types, namely collections, it's likely that you will
   * need to be quite explicit about which handler is invoked from a template
   * hence allow specific naming.
   * 
   * @param handler is the handler to register.
   * @param key     is the key used to access it in the template.
   */
  public static void register(TypeHandler handler, String key) {
    handlers.put(key, handler);
  }

  
  /**
   * hasHandlerFor checks whether there is a registered handler for a given type.
   * This can be used to setup forms as there is no point displaying a form field
   * that can't be parsed when it is submitted.
   * 
   * @param targetClass the Class we wish to check for.
   * @return true if a handler for the given class exists, otherwise false.
   */
  public static boolean hasHandlerFor(Class<?> targetClass) {
    return handlers.containsKey(targetClass.getName());
  }

  /**
   * @param targetClass
   * @param name
   * @param property
   */
  public FieldTemplate(Class<?> targetClass, String name, String property) {
    this(targetClass, name, property, (String) null);
  }

  /**
   * 
   */
  @Deprecated
  public FieldTemplate(Class<?> targetClass, String name, String property, Class<?> propertyClass) {
    this(targetClass, name, property, (String) null);
  }

  /**
   * @param targetClass
   * @param name
   * @param property
   * @param handlerName is the name of the field handler to use. If null then the
   *                    class-name of the targetClass's property is used.
   */
  public FieldTemplate(Class<?> targetClass, String name, String property, String handlerName) {
    super();
    this.targetClass = targetClass;
    this.name = name;
    this.property = property;

    this.getter = getGetter(targetClass, property);
    Class<?> propType = this.getter.getReturnType();
    this.setter = getSetter(targetClass, property, propType);

    if (handlerName == null) {
      if (propType.isEnum()) { // treat enums as a special case and manage as an object.
        handlerName = OBJECT;
      } else {
        handlerName = propType.getName();
      }
    }

    this.handler = handlers.get(handlerName);

    if (handler == null) {
      throw new PresentationException("Can't get handler for " + property);
    }

    Validator defaultValidator = defaultValidation.get(handlerName);
    if (defaultValidator != null) {
      validation.add(defaultValidator);
    }

  }

  /**
   * Helper method to find the getter Method for a given class/property.
   * 
   * @param c
   * @param property
   * @return
   */
  private Method getGetter(Class<?> c, String property) {
    char start = property.charAt(0);
    start = Character.toUpperCase(start);
    String getter = "get" + start + property.substring(1);

    Method m;
    try {
      try {
        m = c.getMethod(getter, (Class<?>[]) null);
      } catch (NoSuchMethodException e) {
        getter = "is" + start + property.substring(1);
        m = c.getMethod(getter, (Class<?>[]) null);
      }
    } catch (Exception e) {
      throw new PresentationException("Unable to find getter method for " + property);
    }

    return m;

  }

  /**
   * Finds the setter for 
   * @param targetClass
   * @param property
   * @param paramType
   * @return
   * @throws PresentationException
   */
  private Method getSetter(Class<?> targetClass, String property, Class<?> paramType) throws PresentationException {
    Method setter = null;
    try {
      try {
        setter = getSetterAttempt(targetClass, property, paramType);
      } catch (Exception e) {
        // Possible for the setter to be using the boxed type e.g. Boolean rather than boolean
        if(paramType.isPrimitive()) {
          Class<?> boxedType = getBoxedTypeFor(paramType);
          setter = getSetterAttempt(targetClass, property, boxedType);
        } else {
          throw e; // nope, not primitive so can't be using the boxed type.
        }
      }
    } catch (Exception e) {
      throw new PresentationException("Unable to find setter method for " + property);
    }

    return setter;
  }

  /**
   * For a primitive type e.g. boolean returns its box type, e.g. Boolean.
   * @param primitiveType is the primitive type's class.
   * @return corresponding box type.
   */
  private Class<?> getBoxedTypeFor(Class<?> primitiveType) {
    assert(primitiveType.isPrimitive());
    return boxTypes.get(primitiveType);
  }

  private Method getSetterAttempt(Class<?> targetClass, String property, Class<?> paramType) throws Exception {
    Method setter;
    char start = property.charAt(0);
    start = Character.toUpperCase(start);
    String setterName = "set" + start + property.substring(1);
    Class<?>[] paramTypes = new Class[] { paramType };
    setter = targetClass.getMethod(setterName, paramTypes);
    return setter;
  }

  /**
   * @return Returns the display length for the field.
   */
  public int getLength() {
    if (length > 0) {
      return length;
    }

    int len = handler.getDefaultLength();
    if (len > 0) {
      return len;
    }

    return 40; // last resort!
  }

  /**
   * @return Returns the maxlen.
   */
  public int getMaxlen() {
    if (maxlen > 0) {
      return maxlen;
    }
    return 255;
  }

  /**
   * @return Returns the name.
   */
  public String getName() {
    return name;
  }

  /**
   * @return Returns the property.
   */
  public String getProperty() {
    return property;
  }

  /**
   * Gets descriptive text for this field. This text is intended to be used for
   * help, tool-tips etc.
   * 
   * @return descriptive text or null if none set.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets descriptive text for this field. This text is intended to be used for
   * help, tool-tips etc.
   * 
   * @param description is the descriptive text to set.
   */
  public FieldTemplate setDescription(String description) {
    this.description = description;
    return this;
  }

//    /**
//     * @return Returns the propertyClass.
//     */
//    public Class<?> getPropertyClass() {
//        return propertyClass;
//    }

  /**
   * @return the handler
   */
  public TypeHandler getHandler() {
    return handler;
  }

  /**
   * Determines if the field is mandatory.
   * 
   * @return true if input is required.
   */
  public boolean isRequired() {
    return required;
  }

  /**
   * @param required The required to set.
   * @return this template for method chaining.
   */
  public FieldTemplate setRequired(boolean required) {
    this.required = required;
    return this;
  }

  /**
   * @return Returns the targetClass.
   */
  public Class<?> getTargetClass() {
    return targetClass;
  }

  /**
   * @param length The length to set.
   * @return this template for method chaining.
   */
  public FieldTemplate setLength(int length) {
    this.length = length;
    return this;
  }

  /**
   * @param maxlen The maxlen to set.
   * @return this template for method chaining.
   */
  public FieldTemplate setMaxlen(int maxlen) {
    this.maxlen = maxlen;
    return this;
  }

  /**
   * @return the widgetType
   */
  public String getWidgetType() {
    return widgetType;
  }

  /**
   * @param widgetType the widgetType to set
   * @return this for method chaining.
   */
  public FieldTemplate setWidgetType(String widgetType) {
    this.widgetType = widgetType;
    return this;
  }

  /**
   * addValidation adds a Validator to the template.
   * 
   * @param validator is the Validator to add.
   * @return this template for method chaining.
   */
  public FieldTemplate addValidation(Validator validator) {
    if (validator == null) {
      throw new NullPointerException("Cannot add null validator to FieldTemplate");
    }
    validation.add(validator);
    return this;
  }

  /**
   * validate runs through the list of Validators checking the input. If any of
   * these fail, then the corresponding error text is returned.
   * 
   * @param value is the value to check.
   * @return error text if validation problem or null if all OK.
   */
  public String validate(String value) {
    for (Validator validator : validation) {
      if (!validator.isValid(value)) {
        return validator.getErrorText();
      }
    }
    return null;
  }

  /**
   * @return Returns the getter.
   */
  public Method getGetter() {
    return getter;
  }

  /**
   * @return Returns the setter.
   */
  public Method getSetter() {
    return setter;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return getName();
  }

}
