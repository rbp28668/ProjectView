/*
 * DataException.java
 * Created on 20-May-2005
 *
 */
package uk.co.alvagem.projectview.dao;

/**
 * DataException
 * 
 * @author rbp28668
 * Created on 20-May-2005
 */
public class DataException extends Exception {

    /**
     * 
     */
    public DataException() {
        super();
    }

    /**
     * @param arg0
     */
    public DataException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public DataException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * @param arg0
     */
    public DataException(Throwable arg0) {
        super(arg0);
    }

}
