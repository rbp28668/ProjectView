/*
 * StartAt.java
 * Project: ProjectView
 * Created on 27 Dec 2007
 *
 */
package uk.co.alvagem.projectview.model;

import java.util.Date;

public class StartAt extends ConstraintType {

    StartAt(){
        super("Start At");
    }
    
    @Override
    public void applyToTask(Constraint constraint, Task task) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void check(Constraint constraint, Task task) {
        // TODO Auto-generated method stub
        
    }

	/* (non-Javadoc)
	 * @see uk.co.alvagem.projectview.model.ConstraintType#canStartOn(uk.co.alvagem.projectview.model.Task, java.util.Date)
	 */
	@Override
	public boolean canStartOn(Task task, Date start, Date constraintDate) {
		// TODO Auto-generated method stub
		return false;
	}

}
