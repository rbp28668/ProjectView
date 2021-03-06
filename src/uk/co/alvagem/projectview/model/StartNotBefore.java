/*
 * StartNotBefore.java
 * Project: ProjectView
 * Created on 27 Dec 2007
 *
 */
package uk.co.alvagem.projectview.model;

import java.util.Date;

public class StartNotBefore extends ConstraintType {

    StartNotBefore(){
        super("Start Not Before");
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
		return !start.before(constraintDate);
	}

}
