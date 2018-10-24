/*
 * IsAbandonedTaskFilter.java
 * Project: ProjectView
 * Created on 22 Jan 2008
 *
 */
package uk.co.alvagem.projectview.core.filters;

import uk.co.alvagem.projectview.model.Task;

public class IsAbandonedTaskFilter  extends AbstractTaskFilter implements TaskFilter {

    public boolean accept(Task task) {
        return task.getFractionComplete() < 1.0f && !task.isActive();
    }
    
    public String toString(){
        return "Abandoned";
    }

    public TaskFilter copy(){
        return new IsAbandonedTaskFilter();
    }
    
}

