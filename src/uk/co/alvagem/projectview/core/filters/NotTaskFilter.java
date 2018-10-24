/*
 * NotTaskFilter.java
 * Project: ProjectView
 * Created on 22 Jan 2008
 *
 */
package uk.co.alvagem.projectview.core.filters;

import java.util.Iterator;

import uk.co.alvagem.projectview.model.Task;

public class NotTaskFilter extends CombinedTaskFilter  implements TaskFilter {

    public boolean accept(Task task) {
        Iterator<TaskFilter> iter = getFilters().iterator();
        if(iter.hasNext()) {
            TaskFilter child = iter.next();
            return !child.accept(task);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.projectview.core.CombinedTaskFilter#canAddChild()
     */
    @Override
    public boolean canAddChild() {
        return getFilters().isEmpty();
    }

    public String toString(){
        return "NOT";
    }

    public TaskFilter copy(){
        NotTaskFilter copy = new NotTaskFilter();
        copyChildrenTo(copy);
        return copy;
    }
    
    public String getErrorStatus() {
        if(getFilters().size() != 1){
            return "NOT must have a child filter";
        }
        return null;
    }
    

    
}
