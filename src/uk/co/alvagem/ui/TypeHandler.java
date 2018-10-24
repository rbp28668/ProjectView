/*
 * TypeHandler.java
 * Created on 13-May-2005
 *
 */
package uk.co.alvagem.ui;


public abstract class TypeHandler {
    public String getValue(Object value) {return value.toString();}
	public abstract Object parse(String value);
	public abstract Class<?> getTargetClass();
	public abstract int getDefaultLength();
}