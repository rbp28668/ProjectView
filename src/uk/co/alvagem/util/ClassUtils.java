/*
 * ClassUtils.java
 * Project: EATool
 * Created on 17-Jul-2006
 *
 */
package uk.co.alvagem.util;

/**
 * ClassUtils provides utilities for manipulating class information.
 * 
 * @author rbp28668
 */
public class ClassUtils {

    /**
     * 
     */
    public ClassUtils() {
        super();
    }

    public static String baseClassNameOf(Object o){
        return baseClassNameOf(o.getClass());
    }

    public static String baseClassNameOf(Class theClass){
        String strClass = theClass.getName();
        int idx = strClass.lastIndexOf('.');    // full class name?
        if(idx != -1) {
            strClass = strClass.substring(idx+1);
        }
        idx = strClass.lastIndexOf('$');    // inner class?
        if(idx != -1) {
            strClass = strClass.substring(idx+1);
        }
        return strClass;
    }
}
