/*
 * Constraint.java
 * Created on 28-Apr-2005
 * By Bruce.Porteous
 *
 */
package uk.co.alvagem.projectview.model;

import java.text.DateFormat;
import java.util.Date;

/**
 * Constraint is a Constraint on scheduling a task.
 * @author Bruce.Porteous
 */
public class Constraint extends PersistentBase implements Persistent {

    private static final DateFormat FMT = DateFormat.getDateInstance(DateFormat.MEDIUM);
    
	/** Date that the constraint is referenced to */
	private Date when;
	
	/** Type of constraint */
	private ConstraintType type;
	
	/**
	 * 
	 */
	public Constraint() {
		super();
		when = new Date();
		type = ConstraintTypes.START_AT;
	}


    /**
     * @return the when
     */
    public Date getWhen() {
        return when;
    }


    /**
     * @param when the when to set
     */
    public void setWhen(Date when) {
        this.when = when;
    }

    /**
     * Type name can be persisted.
     * @return
     */
    public String getType(){
        return type.toString();
    }
    
    /**
     * Sets the type of this constraint.
     * @param name is the name of the constraint.  Note that the name
     * must correspond to one of the values (as returned by toString()) of
     * one of the members of ConstraintTypes.
     */
    public void setType(String name){
        type = ConstraintTypes.get(name);
    }
    
    /**
     * Apply this constraint to the task, moving start/end dates as necessary.
     * @param task
     */
    public void applyToTask( Task task){
        type.applyToTask(this, task);
    }
    
    /**
     * Check the start/finish dates of the task obey this constraint.  
     * @param task
     */
    public void check( Task task){
        type.check(this, task);
    }

	/**
	 * @param start
	 * @return
	 */
	public boolean canStartOn(Task task,Date start ) {
		return type.canStartOn(task,start,when);
	}

    public String toString(){
        return getType() + " " + FMT.format(when);
    }


    
}
