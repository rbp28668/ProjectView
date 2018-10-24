/*
 * OrTaskFilter.java
 * Project: ProjectView
 * Created on 22 Jan 2008
 *
 */
package uk.co.alvagem.projectview.core.filters;

import uk.co.alvagem.projectview.model.Task;

public class OrTaskFilter extends CombinedTaskFilter  implements TaskFilter{

    public boolean accept(Task task) {
        for(TaskFilter filter : getFilters()){
            if(filter.accept(task)){
                return true;
            }
        }
        return false;
    }

    public String toString(){
        return "OR";
    }

    public TaskFilter copy(){
        OrTaskFilter copy = new OrTaskFilter();
        return copy;
    }

    public String getErrorStatus() {
        if(getFilters().size() < 2){
            return "OR needs 2 or more filters to combine";
        }
        return null;
    }

}
