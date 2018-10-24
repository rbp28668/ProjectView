/*
 * AbstractTaskFilter.java
 * Project: ProjectView
 * Created on 22 Jan 2008
 *
 */
package uk.co.alvagem.projectview.core.filters;

import java.util.Collections;
import java.util.List;

import uk.co.alvagem.projectview.model.Task;

public abstract class AbstractTaskFilter implements TaskFilter {

    private TaskFilter parent;
    
    public boolean accept(Task task) {
        return false;
    }

    public void add(TaskFilter child) {
        throw new UnsupportedOperationException("Can't add child Task Filter");
    }

    public void remove(TaskFilter child) {
        throw new UnsupportedOperationException("Can't remove child Task Filter");
    }

    public boolean canAddChild() {
        return false;
    }

    public List<TaskFilter> getChildren() {
        return  Collections.EMPTY_LIST;
    }

    public TaskFilter getParent() {
        return parent;
    }

    public void setParent(TaskFilter parent) {
        this.parent = parent;
    }


    /* (non-Javadoc)
     * @see uk.co.alvagem.projectview.core.filters.TaskFilter#copy()
     */
    public TaskFilter copy() {
        throw new UnsupportedOperationException("Can't copy Abstract Task Filter");
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.projectview.core.filters.TaskFilter#getErrorStatus()
     */
    public String getErrorStatus() {
        return null;
    }


}
