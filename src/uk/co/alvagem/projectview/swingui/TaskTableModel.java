/*
 * TaskTableModel.java
 * Project: ProjectView
 * Created on 13 Jan 2008
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import uk.co.alvagem.projectview.core.filters.TaskFilter;
import uk.co.alvagem.projectview.model.Allocation;
import uk.co.alvagem.projectview.model.Constraint;
import uk.co.alvagem.projectview.model.Dependency;
import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.ui.Field;
import uk.co.alvagem.ui.FieldTemplate;
import uk.co.alvagem.ui.FormTemplate;

/**
 * TaskTableModel is a table model that represents a flattened hierarchy of tasks as a 2D table.
 * 
 * @author rbp28668
 */
public class TaskTableModel extends AbstractTableModel {

    
    /** */
    private static final long serialVersionUID = 1L;

    /** The base list of tasks */
    private Vector<Task> tasks = new Vector<Task>();
    
    /** The filtered (by a TaskFilter) lists of tasks - this list drives the rows of the model */
    private Vector<Task> filteredTasks = new Vector<Task>();
    
    /** The list of fields that should be displayed in the table.  This 
     * collection drives the columns of the model.
     */
    private Vector<FieldTemplate> fields = new Vector<FieldTemplate>();
    
    /** The root task of the hierarchy */
    private Task rootTask = null;
    
    /** The current filter filtering the rows.  Used to create filteredTasks from tasks. */
    private TaskFilter filter =  null;
    
    /** FormTemplate tha contains all the fields that could be displayed */
    private static FormTemplate template = new FormTemplate();
    static {
        
        template.add(new FieldTemplate(Task.class, "Name","name"));
        template.add(new FieldTemplate(Task.class, "Description","description"));
        template.add(new FieldTemplate(Task.class, "Notes","notes"));
        template.add(new FieldTemplate(Task.class, "Parent","parent",FieldTemplate.OBJECT)
            .setWidgetType("parent"));
        template.add(new FieldTemplate(Task.class, "Work Package","workPackage"));
        template.add(new FieldTemplate(Task.class, "Actual Work", "actualWork", ElapsedTimeHandler.TYPE_KEY));
        template.add(new FieldTemplate(Task.class, "Estimated Effort", "estimatedEffort", ElapsedTimeHandler.TYPE_KEY));
        template.add(new FieldTemplate(Task.class, "Estimate Spread", "estimateSpread", ElapsedTimeHandler.TYPE_KEY));
        template.add(new FieldTemplate(Task.class, "Fraction Complete", "fractionComplete"));
        template.add(new FieldTemplate(Task.class, "Start Date", "startDate"));
        template.add(new FieldTemplate(Task.class, "Finish Date", "finishDate"));
        template.add(new FieldTemplate(Task.class, "Constraints", "constraints",FieldTemplate.OBJECT)
            .setWidgetType("constraints"));
        template.add(new FieldTemplate(Task.class, "Predecessors", "predecessors", FieldTemplate.OBJECT)
            .setWidgetType("predecessors"));
        template.add(new FieldTemplate(Task.class, "Successors", "successors", FieldTemplate.OBJECT)
            .setWidgetType("successors"));
        template.add(new FieldTemplate(Task.class, "Resources", "allocations", FieldTemplate.OBJECT)
            .setWidgetType("allocations"));
        
    }

    /** Specialist widgets keyed by widget name.  These are used to display the collections */
    private static Map<String,Widget> widgets = new HashMap<String,Widget>();
    
    static {
        widgets.put("parent", new ParentTaskWidget());
        widgets.put("constraints", new ConstraintsWidget());
        widgets.put("predecessors", new PredecessorWidget());
        widgets.put("successors", new SuccessorWidget());
        widgets.put("allocations", new AllocationSetWidget());
    }
    
    /**
     * Creates an empty model.
     */
    public TaskTableModel() {
        initFields();
    }

    /**
     * Creates a model initialised with the task hierarchy rooted at the given task.
     * @param task is the root task of the hierarchy to be displayed.
     */
    TaskTableModel(Task task){
        setTasks(task);
        initFields();
    }

    /**
     * Initialises the fields vector that controls the display of columns.
     */
    private void initFields(){
        for(FieldTemplate ft : template.getFields()){
            fields.add(ft);
        }
    }
    
    /**
     * Sets the model to contain the task hierarchy rooted at the given task.
     * @param task is the root task of the hierarchy to be displayed.
     */
    public void setTasks(Task task){
        rootTask = task;
        tasks.clear();
        addTask(task);
    }
    
    /**
     * Recursive implementation used to set the tasks.  Should be called in
     * the context of an active transaction as it will load associated data.
     * @param task is the task hierarchy to add.
     */
    private void addTask(Task task){
        tasks.add(task);
        filteredTasks.add(task);
        
        // Load up required children.
        task.getConstraints().isEmpty();
        task.getPredecessors().isEmpty();
        task.getSuccessors().isEmpty();
        for(Allocation a : task.getAllocations()){
            a.getResource().getName();
        }
        
        for(Task child : task.getSubTasks()){
            addTask(child);
        }
    }


    /**
     * Gets the current filter. Note that if removeFilter has been called this may still
     * have a valid value.
     * @return the current filter or null if none defined.
     */
    public TaskFilter getFilter() {
        return filter;
    }

    /**
     * Sets the TaskFilter that will filter the rows in the table.  As well as filtering
     * the rows, it is stored for subsequent calls to getFilter().
     * @param filter the filter to set.
     */
    public void setFilter(TaskFilter filter){
        this.filter = filter;
        filteredTasks.clear();
        for(Task task : tasks){
            if(filter.accept(task)){
                filteredTasks.add(task);
            }
        }
        fireTableStructureChanged();
    }

    /**
     * Removes the current filter from the table - all the rows are then displayed.  Note
     * that this does not set the current filter to null so it can be re-instated by
     * calling getFilter() followed by setFilter().
     */
    public void removeFilter(){
        filteredTasks.clear();
        filteredTasks.addAll(tasks);
        fireTableStructureChanged();
    }
    
    
    /**
     * Gets the complete list of possible fields that can be used as columns in the table.
     * @return the List of FieldTemplate that describe all possible columns.
     */
    public List<FieldTemplate> getAllFields(){
        return template.getFields();
    }
    
    /**
     * Gets the list of fields that currently describe the columns in the model.
     * @return Collection of FieldTemplate that describe the current columns.
     */
    public Collection<FieldTemplate> getSelectedFields(){
        return fields;
    }
    
    /**
     * Sets the fields used to describe the columns.  Note that these fields must
     * be a subset of those returned by getAllFields().
     * @param fields is the collection of TemplateField to set the model's columns.
     */
    public void setSelectedFields(Collection<FieldTemplate> fields){
        this.fields.clear();
        for(FieldTemplate field :template.getFields()){
            if(fields.contains(field)){
                this.fields.add(field);
            }
        }
        fireTableStructureChanged();
    }
    

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return fields.size();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return filteredTasks.size();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Task task = filteredTasks.get(rowIndex);
        FieldTemplate ft = fields.get(columnIndex);
        Field field = new Field(task,ft);
        String value;
        if(field.getWidgetType() != null){
            Widget widget = widgets.get(field.getWidgetType());
            value = widget.translate(field.getValueAsObject());
        } else {
            //System.out.println(field.getName() + " -> " + field.getValue());
        	value = field.getValue();
        }
        return value;
    }

 
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int column) {
        FieldTemplate ft = fields.get(column);
        return ft.getName();
    }

    /**
     * Widget translates an object returned by a form field to a string suitable for display in the table.
     */
    private static abstract class Widget{
        public abstract String translate(Object value);
    }
    
    /**
     * ParentTaskWidget displays the parent task.
     */
    private static class ParentTaskWidget extends Widget {

        @Override
        public String translate(Object value) {
            Task task = (Task)value;
            if(task == null){
                return "";
            }
            return task.getName();
        }
    }

    /**
     * PredecessorWidget displays any predecessors as a comma separated string.
     */
    private static class PredecessorWidget extends Widget {

        @Override
        public String translate(Object value) {
            StringBuffer values = new StringBuffer();
            Set<Dependency> dependencies = (Set<Dependency>)value;
            for(Dependency dependency: dependencies){
                if(values.length() != 0){
                    values.append(", ");
                }
                values.append(dependency.getPredecessor().getName());
            }
            return values.toString();
        }
        
    }

    /**
     * SuccessorWidget displays any successors as a comma separated string.
     */
    private static class SuccessorWidget extends Widget {

        @Override
        public String translate(Object value) {
            StringBuffer values = new StringBuffer();
            Set<Dependency> dependencies = (Set<Dependency>)value;
            for(Dependency dependency: dependencies){
                if(values.length() != 0){
                    values.append(", ");
                }
                values.append(dependency.getSuccessor().getName());
            }
            return values.toString();
        }
        
    }

    /**
     * ConstraintsWidget displays any constraints as a comma separated string.
     */
    private static class ConstraintsWidget extends Widget {

        @Override
        public String translate(Object value) {
            StringBuffer values = new StringBuffer();
            Set<Constraint> constraints = (Set<Constraint>)value;
            for(Constraint constraint: constraints){
                if(values.length() != 0){
                    values.append(", ");
                }
                values.append(constraint.toString());
            }
            return values.toString();
        }
    }
        
    /**
     * AllocationSetWidget displays any resource allocations as a comma seperated string.
     */
    private static class AllocationSetWidget extends Widget {

        @Override
        public String translate(Object value) {
            StringBuffer values = new StringBuffer();
            Set<Allocation> allocations = (Set<Allocation>)value;
            for(Allocation allocation : allocations){
                if(values.length() != 0){
                    values.append(", ");
                }
                values.append(allocation.getResource().getName() + "(" + allocation.getUtilisation() + ")");
            }
            return values.toString();
        }
    }
    
}
