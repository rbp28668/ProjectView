/*
 * ConstraintType.java
 * Project: ProjectView
 * Created on 27 Dec 2007
 *
 */
package uk.co.alvagem.projectview.model;

import java.util.Date;

public abstract class ConstraintType {

    private String name;
    
    ConstraintType(String name){
        this.name = name;
    }
    
    public abstract void applyToTask(Constraint constraint, Task task);
    
    public abstract void check(Constraint constraint, Task task);

	/**
	 * @param task
	 * @param start
	 * @return
	 */
	public abstract boolean canStartOn(Task task, Date start, Date constraintDate);

    public String toString(){
        return name;
    }

}
