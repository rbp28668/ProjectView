/*
 * AndTaskFilter.java
 * Project: ProjectView
 * Created on 22 Jan 2008
 *
 */
package uk.co.alvagem.projectview.core.filters;

import uk.co.alvagem.projectview.model.Task;

public class AndTaskFilter extends CombinedTaskFilter implements TaskFilter{

    public boolean accept(Task task) {
        for(TaskFilter filter : getFilters()){
            if(!filter.accept(task)){
                return false;
            }
        }
        return true;
    }
    
    public String toString(){
        return "AND";
    }

    public TaskFilter copy(){
        AndTaskFilter copy = new AndTaskFilter();
        copyChildrenTo(copy);
        return copy;
    }

    public String getErrorStatus() {
        if(getFilters().size() < 2){
            return "AND needs 2 or more filters to combine";
        }
        return null;
    }



}
