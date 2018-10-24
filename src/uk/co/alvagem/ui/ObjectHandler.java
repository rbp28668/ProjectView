/*
 * ObjectHandler.java
 * Project: ProjectView
 * Created on 14 Jan 2008
 *
 */
package uk.co.alvagem.ui;


/**
 * ObjectHandler is a marker handler that suppresses the parsing and validation of field
 * values.  If this handler is specified then Field.getValueAsObject() and 
 * Field.setValueObject(Object) should be used to get/set the value of the field. 
 * 
 * @author rbp28668
 */
public final class ObjectHandler extends TypeHandler {

    
    /* (non-Javadoc)
     * @see uk.co.alvagem.ui.TypeHandler#getValue(java.lang.Object)
     */
    @Override
    public String getValue(Object value) {
        throw new UnsupportedOperationException("ObjectHandler.getValue");
    }

    @Override
    public int getDefaultLength() {
        return 0;
    }

    @Override
    public Class<?> getTargetClass() {
        return Object.class;
    }

    @Override
    public Object parse(String value) {
    throw new UnsupportedOperationException("ObjectHandler.parse");
    }


}
