/*
 * ResourceTaskFilter.java
 * Project: ProjectView
 * Created on 1 Feb 2008
 *
 */
package uk.co.alvagem.projectview.core.filters;

import java.util.Set;

import uk.co.alvagem.projectview.model.Allocation;
import uk.co.alvagem.projectview.model.Resource;
import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.ui.FieldTemplate;
import uk.co.alvagem.ui.FormTemplate;

public class ResourceTaskFilter extends AbstractTaskFilter implements
        TaskFilter, Editable{

    private static FormTemplate editTemplate = new FormTemplate();
    public static final String RESOURCE_WIDGET = "ResourceTaskFilter.Resource";
    
    private Resource resource;
    
    static {
        
        editTemplate.add(new FieldTemplate(ResourceTaskFilter.class,"Resource","resource",FieldTemplate.OBJECT)
                .setWidgetType(RESOURCE_WIDGET));
    }
    
    public ResourceTaskFilter() {
        
    }
    
    /* (non-Javadoc)
     * @see uk.co.alvagem.projectview.core.filters.AbstractTaskFilter#accept(uk.co.alvagem.projectview.model.Task)
     */
    @Override
    public boolean accept(Task task) {
        Set<Allocation> allocations = task.getAllocations();
        for(Allocation allocation : allocations){
            if(allocation.getResource().equals(resource)){
                return true;
            }
        }
        return false;
    }

    public String toString(){
        return "Resource " + resource.getName();
    }
    
    public TaskFilter copy(){
        ResourceTaskFilter copy = new ResourceTaskFilter();
        copy.resource = resource;
        return copy;
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.projectview.core.filters.Editable#getEditTemplate()
     */
    public FormTemplate getEditTemplate() {
        return editTemplate;
    }

    /**
     * @return the resource
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * @param resource the resource to set
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }


}
