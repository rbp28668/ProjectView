/*
 * TaskFilter.java
 * Project: ProjectView
 * Created on 22 Jan 2008
 *
 */
package uk.co.alvagem.projectview.core.filters;

import java.util.List;

import uk.co.alvagem.projectview.model.Task;

public interface TaskFilter {

    public boolean accept(Task task) throws FilterException;
    
    public boolean canAddChild();
    
    public void add(TaskFilter child);
    
    public void remove(TaskFilter child);

    public List<TaskFilter> getChildren();
    
    public TaskFilter getParent();
    
    public void setParent(TaskFilter parent);
    
    public TaskFilter copy() throws FilterException;
    
    public String getErrorStatus();
}
