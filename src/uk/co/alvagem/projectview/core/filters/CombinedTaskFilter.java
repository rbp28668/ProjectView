/*
 * CombinedTaskFilter.java
 * Project: ProjectView
 * Created on 22 Jan 2008
 *
 */
package uk.co.alvagem.projectview.core.filters;

import java.util.HashSet;
import java.util.Set;

public abstract class CombinedTaskFilter extends AbstractTaskFilter implements TaskFilter {

    private Set<TaskFilter> filters = new HashSet<TaskFilter>();
    
    public void add(TaskFilter filter){
        filters.add(filter);
    }
    
    public void remove(TaskFilter filter){
        filters.remove(filter);
    }
    
    protected Set<TaskFilter> getFilters(){
        return filters;
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.projectview.core.TaskFilter#canAddChild()
     */
    public boolean canAddChild() {
        return true;
    }

    protected void copyChildrenTo(CombinedTaskFilter copy){
        for(TaskFilter filter : filters){
            copy.add(filter.copy());
        }
    }
    
}
