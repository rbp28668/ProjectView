/*
 * ActiveTaskFilter.java
 * Project: ProjectView
 * Created on 22 Jan 2008
 *
 */
package uk.co.alvagem.projectview.core.filters;

import uk.co.alvagem.projectview.model.Task;

public class ActiveTaskFilter extends AbstractTaskFilter implements TaskFilter {

    public boolean accept(Task task) {
        return task.isActive();
    }

    public String toString(){
        return "Active";
    }
    
    public TaskFilter copy(){
        return new ActiveTaskFilter();
    }
}
