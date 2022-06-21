/*
 * Task.java
 * Created on 28-Apr-2005
 * By Bruce.Porteous
 *
 */
package uk.co.alvagem.projectview.model;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Task
 * @author Bruce.Porteous
 * 
 */
public class Task extends PersistentBase implements Persistent {
	
	public static final float UNDEFINED = Float.NEGATIVE_INFINITY;
	
	/** Short name for the task */
	private String name;
	
	/** Description of the task */
	private String description;
	
	/** Notes relating to the task */
	private String notes;
	
	/** Work package identifier */
	private long workPackage;
	
	/** Relative priority of this task **/
	private int priority;
	
	/** Sub-tasks of this task **/
	private List<Task> subTasks = new LinkedList<Task>();
		
	/** Parent task of this task **/
	private Task parent;
	
	/** Current state - not persisted until commitHistory() is called */
	private  TaskHistory current;
	
	/** History of this task */
	private List<TaskHistory> history = new LinkedList<TaskHistory>();
	
	/** Constraints on this task */
	private Set<Constraint> constraints = new HashSet<Constraint>();
	
	/** Predecessors of this task */
	private Set<Dependency> predecessors = new HashSet<Dependency>();
	
	/** Successors of this task */
    private Set<Dependency> successors = new HashSet<Dependency>();
    
    /** Allocations of resource(s) to this task */
    private Set<Allocation> allocations = new HashSet<Allocation>();
    
    /** Scheduling stuff. Note these have been made intrinsic to task for convenience.  They could
     * be moved external to task - especially if we end up with multiple scheduling algorithms. */
    private transient float scheduleEffort;
    private transient float scheduleElapsed;
    private transient float schedulePriority;
    private transient Set<Dependency> criticalPredecessors;
    private transient Set<Dependency> criticalSuccessors;
    private transient float criticalTime;
    private transient boolean isCriticalPath;
    private transient int index;
    private transient boolean isScheduled;
    private transient Set<Allocation> inheritedAllocations;
    private transient Set<Constraint> inheritedConstraints;

    
	/**
	 * 
	 */
	public Task() {
		super();
		name = "";
		description = "";
		notes = "";
		workPackage = 0;
		priority = 1000;
		current = new TaskHistory();
		//updateCurrent(); // Always want history loaded.
	}

	/**
	 * Updates current values from latest (last in list) in History.
	 */
	private void updateCurrent(){
		List<TaskHistory> hist = getHistory();
		if(!hist.isEmpty()){
			copyHistory(hist.get(hist.size()-1),current);
		}
	}
	
	/**
	 * Updates the current state of this task, and its children from the last entry
	 * in the history.
	 */
	public void rollbackHistory(){
		updateCurrent();
		for(Task task : subTasks){
			task.rollbackHistory();
		}
	}

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * @return Returns the notes.
     */
    public String getNotes() {
        return notes;
    }
    /**
     * @param notes The notes to set.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * @return Returns the workPackage.
     */
    public long getWorkPackage() {
        return workPackage;
    }
    /**
     * @param workPackage The workPackage to set.
     */
    public void setWorkPackage(long workPackage) {
        this.workPackage = workPackage;
    }
    
    /**
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * @return Returns the parent.
     */
    public Task getParent() {
        return parent;
    }
    /**
     * @param parent The parent to set.
     */
    public void setParent(Task parent) {
        this.parent = parent;
    }
    
    /**
     * @return Returns the subTasks.
     */
    public List<Task> getSubTasks() {
        return subTasks;
    }
    /**
     * @param subTasks The subTasks to set.
     */
    public void setSubTasks(List<Task> subTasks) {
        this.subTasks = subTasks;
    }
    
    
    /**
     * @return the history
     */
    public List<TaskHistory> getHistory() {
        return history;
    }


    /**
     * @param history the history to set
     */
    public void setHistory(List<TaskHistory> history) {
        this.history = history;
    }


    /**
     * @return the constraints
     */
    public Set<Constraint> getConstraints() {
        return constraints;
    }


    /**
     * @param constraints the constraints to set
     */
    public void setConstraints(Set<Constraint> constraints) {
        this.constraints = constraints;
    }


    
    /**
     * @return the predecessors
     */
    public Set<Dependency> getPredecessors() {
        return predecessors;
    }


    /**
     * @param predecessors the predecessors to set
     */
    public void setPredecessors(Set<Dependency> predecessors) {
        this.predecessors = predecessors;
    }


    /**
     * @return the successors
     */
    public Set<Dependency> getSuccessors() {
        return successors;
    }


    /**
     * @param successors the successors to set
     */
    public void setSuccessors(Set<Dependency> successors) {
        this.successors = successors;
    }

    /**
     * @return the allocations
     */
    public Set<Allocation> getAllocations() {
        return allocations;
    }

    /**
     * @param allocations the allocations to set
     */
    public void setAllocations(Set<Allocation> allocations) {
        this.allocations = allocations;
    }
    
    /**
     * Gets the current state of the task.
     * @return the current state.
     */
    public TaskHistory getCurrent(){
    	return current;
    }
    
    /* ===============================================================
     * Task History...
     * ===============================================================
     */

 
    /**
     * Puts the current values on the history list as a timestamped value.
     */
    public void commitHistory(){
    	Date now = new Date();
    	current.setTimePoint(now);
        current.setTimestamp(now);
        getHistory().add(current);
        TaskHistory previous = current;
        current = new TaskHistory();
        copyHistory(previous,current);
    }
    
    /**
     * Commits the changes to the task history down the
     * tree of sub-tasks.
     */
    public void commitHistoryTree(){
    	commitHistory();
    	for(Task sub: getSubTasks()){
    		sub.commitHistoryTree();
    	}
    }

    
	/**
	 * Convenience method to copy a task history.  Use when checkpointing a task.
	 * @param from is the history to copy from.
	 * @param to is the history to copy to.
	 */
	private void copyHistory(TaskHistory from, TaskHistory to) {
        to.setActualWork(from.getActualWork());
        to.setEstimatedEffort(from.getEstimatedEffort());
        to.setEstimateSpread(from.getEstimateSpread());
        to.setUncertaintyType(from.getUncertaintyType());
        to.setAlpha(from.getAlpha());
        to.setBeta(from.getBeta());
        to.setElapsedTime(from.getElapsedTime());
        to.setEffortDriven(from.isEffortDriven());
        to.setFinishDate(from.getFinishDate());
        to.setFractionComplete(from.getFractionComplete());
        to.setStartDate(from.getStartDate());
        to.setActive(from.isActive());
        to.setStatus(from.getStatus());
    }

	/** Used to signal a node has changed. If it has, then any parent tasks' composite
	 * values are likely to be updated, and so on until the root of the tree.
	 */
	public void fireChanged(){
		Task task = parent;
		while(task != null){
			task.updateCompositeValues();
			task.commitHistory();
			task = task.getParent();
		}
	}

	
	/**
	 * Used to refresh all the composite values from their children.  Refresh any children
	 * first, then update from those children.  NOP for leaf tasks.
	 */
	public void updateFromSubTree() {
		if(!subTasks.isEmpty()){
			for(Task child : subTasks){
				child.updateFromSubTree();
			}
			updateCompositeValues();
			commitHistory();
		}
	}

	/**
	 * Helper method that sets all the composite attributes from the immediate children.
	 * Note - does not recurse down or up the tree - updateFromChildren and fireChanged
	 * do this.
	 */
	public void updateCompositeValues(){
		setActualWorkFromChildren();
		setElapsedTimeFromChildren();
		setEstimatedEffortFromChildren();
		setEstimateSpreadFromChildren();
		setFinishDateFromChildren();
		setFractionCompleteFromChildren();
		setStartDateFromChildren();
	}

    /**
	 * @return Returns the actualWork.
	 */
	public float getActualWork() {
        return current.getActualWork();
	}
	
    /**
	 * Updates the actual work when a child changes.  This flows the 
	 * calculation up the tree - if one value is changed then the 
	 * parent totals must be changed.
	 */
//	private void updateActualWork() {
//    
//        setActualWorkFromChildren();
//        if(parent != null){
//        	parent.updateActualWork();
//        }
//	}

	/**
	 * Uses the child values for actual work to work out this task's value.
	 */
	private void setActualWorkFromChildren() {
		assert( !subTasks.isEmpty() );  // If called on leaf node will zero value.
		float work = 0.0f;
        for(Task child: subTasks){
            work += child.getActualWork();
        }
        current.setActualWork(work);
	}

	/**
	 * @param actualWork The actualWork to set.
	 */
	public void setActualWork(float actualWork) {
	    current.setActualWork(actualWork);
	}
	
	/**
	 * @return Returns the estimatedEffort.
	 */
	public float getEstimatedEffort() {
        return current.getEstimatedEffort();
	}
	
//	/**
//	 * Updates the estimated effort when a child changes
//	 */
//	private void updateEstimatedEffort() {
//	    setEstimatedEffortFromChildren();
//        if(parent != null){
//        	parent.updateEstimatedEffort();
//        }
//	}

	/**
	 * 
	 */
	private void setEstimatedEffortFromChildren() {
		assert( !subTasks.isEmpty());
		float effort = 0.0f;
	    for(Task child: subTasks){
	        effort += child.getEstimatedEffort();
	    }
	    current.setEstimatedEffort(effort);
	}

	/**
	 * @param estimatedEffort The estimatedEffort to set.
	 */
	public void setEstimatedEffort(float estimatedEffort) {
		current.setEstimatedEffort(estimatedEffort);
	}
	
	/**
	 * Gets the total estimate spread for this task.  
	 * @return Returns the estimateSpread.
	 */
	public float getEstimateSpread() {
        return current.getEstimateSpread();
	}

	/**
	 * Updates estimate spread when a child is changed.
	 */
//	private void updateEstimateSpread() {
//	    setEstimateSpreadFromChildren();
//        if(parent != null){
//        	parent.updateEstimateSpread();
//        }
//	}

	/**
	 * 
	 */
	private void setEstimateSpreadFromChildren() {
		assert(!subTasks.isEmpty());
		float effort = 0.0f;
	    for(Task child: subTasks){
	        effort += child.getEstimateSpread();
	    }
	    current.setEstimateSpread(effort);
	}

	/**
	 * @param estimateSpread The estimateSpread to set.
	 */
	public void setEstimateSpread(float estimateSpread) {
		current.setEstimateSpread(estimateSpread);
	}
	
    /**
     * Gets the name of the uncertainty type.  Provided primarily for 
     * persistance as it allows it to be set by string.
     * @return Returns the uncertaintyType.
     */
    public String getUncertaintyTypeName() {
        return current.getUncertaintyTypeName();
    }

    /**
     * Sets the uncertainty type by specifying the name of the type.
     * @param name is the name of the uncertainty type to set.
     */
    public void setUncertaintyTypeName(String name) {
    	current.setUncertaintyTypeName(name);
    }
    
    /**
     * Get the first shape parameter for the uncertainty curve.  This allows the
     * curve shape to be parameterised.  Meaning is dependent on the uncertainty
     * type.
	 * @return the alpha
	 */
	public float getAlpha() {
		return current.getAlpha();
	}

	/**
	 * @param alpha the alpha to set
	 */
	public void setAlpha(float alpha) {
		current.setAlpha(alpha);
	}

    /**
     * Get the second shape parameter for the uncertainty curve.  This allows the
     * curve shape to be parameterised.  Meaning is dependent on the uncertainty
     * type.
	 * @return the beta
	 */
	public float getBeta() {
		return current.getBeta();
	}

	/**
	 * @param beta the beta to set
	 */
	public void setBeta(float beta) {
		current.setBeta(beta);
	}
    
    /**
     * @return the elapsedTime (in days).
     */
    public float getElapsedTime() {
       return current.getElapsedTime();
     }

    /**
     * @return the elapsedTime (in days).
     */
//    private void updateElapsedTime() {
//        setElapsedTimeFromChildren();
//        if(parent != null){
//        	parent.updateElapsedTime();
//        }
//    }

	/**
	 * 
	 */
	private void setElapsedTimeFromChildren() {
		assert(!subTasks.isEmpty());
		long diff =  getFinishDate().getTime() - getStartDate().getTime();
        current.setElapsedTime(diff / (1000.0f * 60 * 60 * 24));
	}

    /**
     * @param elapsedTime the elapsedTime to set
     */
    public void setElapsedTime(float elapsedTime) {
        current.setElapsedTime(elapsedTime);
    }


    /**
     * @return the effortDriven
     */
    public boolean isEffortDriven() {
        return current.isEffortDriven();
    }


    /**
     * @param effortDriven the effortDriven to set
     */
    public void setEffortDriven(boolean effortDriven) {
        current.setEffortDriven(effortDriven);
    }

	/**
	 * @return Returns the fractionComplete.
	 */
	public float getFractionComplete() {
        return current.getFractionComplete();
	}
	
	/**
	 * @return Returns the fractionComplete.
	 */
//	private void updateFractionComplete() {
//	    setFractionCompleteFromChildren();
//        if(parent != null){
//        	parent.updateFractionComplete();
//        }
//	}

	/**
	 * 
	 */
	private void setFractionCompleteFromChildren() {
		assert(!subTasks.isEmpty());

		float total = getEstimatedEffort();
	    float complete = estimateComplete();
	    float fraction = 1.0f;
	    if(total > 0){
	        fraction =  complete/total;
	    }
	    current.setFractionComplete(fraction);
	}

	/**
	 * Helper function that gets the estimated work that should be completed 
	 * given the estimated effort and the fraction complete figures.
	 * @return an estimate of the work complete.
	 */
	public float estimateComplete(){
        return current.getEstimatedEffort() * current.getFractionComplete();
	}


	/**
	 * @param fractionComplete The fractionComplete to set.
	 */
	public void setFractionComplete(float fractionComplete) {
		current.setFractionComplete(fractionComplete);
	}
	
    /**
     * Gets the current start date, or, if there are sub-tasks, gets the
     * earliest date of the sub-tasks.
     * @return Returns the startDate.
     */
	public Date getStartDate() {
        return current.getStartDate();
	}
    
	/**
    * Updates the current value of start date from the children. Call when
     * a child is updated.
     */
//	private void updateStartDate() {
//	    setStartDateFromChildren();
//	    
//        if(parent != null){
//        	parent.updateStartDate();
//        }
//	}

	/**
	 * 
	 */
	private void setStartDateFromChildren() {
		assert(!subTasks.isEmpty());

		Date start = subTasks.get(0).getStartDate();
        for(Task child: subTasks){
            Date cs = child.getStartDate();
            if(cs.before(start)){
                start = cs;
            }
        }

	    current.setStartDate(start);
	}

	/**
	 * @param startDate The startDate to set.
	 */
	public void setStartDate(Date startDate) {
		current.setStartDate(startDate);
	}

    /**
     * @return Returns the finish Date.
     */
    public Date getFinishDate() {
        return current.getFinishDate();
    }

    
    /** 
     * Updates the current value of finish date from the children. Call when
     * a child is updated.
     */
//    private void updateFinishDate() {
//    	if(subTasks.isEmpty()){
//    		return;
//    	}
//    	
//        setFinishDateFromChildren();
//        if(parent != null){
//        	parent.updateFinishDate();
//        }
//    }

	/**
	 * 
	 */
	private void setFinishDateFromChildren() {
		assert(!subTasks.isEmpty());

		Date finish = subTasks.get(0).getFinishDate();
        for(Task child: subTasks){
            Date cf = child.getFinishDate();
            if(cf.after(finish)){
                finish = cf;
            }
        }

        current.setFinishDate(finish);
	}

    /**
     * @param finishDate The  finish Date to set.
     */
    public void setFinishDate(Date finishDate) {
        current.setFinishDate(finishDate);
    }
	
	/**
	 * @return Returns the uncertaintyType.
	 */
	public UncertaintyType getUncertaintyType() {
		return current.getUncertaintyType();
	}
	/**
	 * @param uncertaintyType The uncertaintyType to set.
	 */
	public void setUncertaintyType(UncertaintyType uncertaintyType) {
		current.setUncertaintyType(uncertaintyType);
	}

	public boolean isActive(){
	    return current.isActive();
	}
	
    public void setActive(boolean b) {
        current.setActive(b);
    }

    public TaskStatus getStatus() {
    	return current.getStatus();
    }
    
    public void setStatus(TaskStatus status) {
    	current.setStatus(status);
    }

	/**
	 * Used to map the enum into a standard int field in the db.
	 * @return the status
	 */
	public int getIntStatus() {
		return current.getIntStatus();
	}

	/**
	 * @param status the status to set
	 */
	public void setIntStatus(Integer status) {
		current.setIntStatus(status);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return getName();
	}

	
	
	
	
	/*----------------------------------------------------------------------------------
	 * Scheduling
	 -----------------------------------------------------------------------------------*/
	
	
	/**
	 * Effort to be used for a single pass of the scheduler.  Takes into account the 
	 * uncertainty values.
	 * @return the scheduleEffort
	 */
	public float getScheduleEffort() {
		return scheduleEffort;
	}

	/**
	 * @param scheduleEffort the scheduleEffort to set
	 */
	public void setScheduleEffort(float scheduleEffort) {
		this.scheduleEffort = scheduleEffort;
	}

	/**
	 * Sets the elapsed time to be used for scheduling.  Derived from the other task values
	 * should also take into account the uncertainty and resource allocation calendar.
	 * @return the scheduleElapsed
	 */
	public float getScheduleElapsed() {
		return scheduleElapsed;
	}

	/**
	 * @param scheduleElapsed the scheduleElapsed to set
	 */
	public void setScheduleElapsed(float scheduleElapsed) {
		this.scheduleElapsed = scheduleElapsed;
	}

	/**
	 * Gets the current priority for scheduling.  May be set by a number
	 * of variables, the critical path being only one.
	 * @return the schedulePriority
	 */
	public float getSchedulePriority() {
		return schedulePriority;
	}

	/**
	 * Sets the scheduling priority for this task.
	 * @param schedulePriority the schedulePriority to set
	 */
	public void setSchedulePriority(float schedulePriority) {
		this.schedulePriority = schedulePriority;
	}

	/**
	 * Used for the critical path graph.
	 * @return the criticalPredecessors
	 */
	public Set<Dependency> getCriticalPredecessors() {
		if(criticalPredecessors == null){
			criticalPredecessors = new HashSet<Dependency>();
		}
		return criticalPredecessors;
	}

	/**
	 * Used for the critical path graph.
	 * @return the criticalSuccessors
	 */
	public Set<Dependency> getCriticalSuccessors() {
		if(criticalSuccessors == null){
			criticalSuccessors = new HashSet<Dependency>();
		}
		return criticalSuccessors;
	}

	/**
	 * Sets the critical time for this task.  The critical time
	 * is the time from the start of the task, to the end of the
	 * project.
	 * @return the criticalTime.
	 */
	public float getCriticalTime() {
		return criticalTime;
	}

	/**
	 * Gets the critical time for this task.
	 * @param criticalTime the criticalTime to set
	 */
	public void setCriticalTime(float criticalTime) {
		this.criticalTime = criticalTime;
	}

	/**
	 * Determines whether this task has been marked as being on the
	 * critical path.
	 * @return the isCriticalPath, true if this task is on the critical path.
	 */
	public boolean isCriticalPath() {
		return isCriticalPath;
	}

	/**
	 * Marks this task as being on the critical path.
	 * @param isCriticalPath the isCriticalPath to set
	 */
	public void setCriticalPath(boolean isCriticalPath) {
		this.isCriticalPath = isCriticalPath;
	}

	/**
	 * Used for scheduling, the index is determined by traversing the
	 * task tree from top to bottom, left to right. This allows the order
	 * in which tasks are presented in the project plan to influence
	 * the scheduling of the tasks.
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Convenience method to reset all the scheduling information for this
	 * task to default values.
	 */
	public void resetSchedulingInformation(){
		schedulePriority = 0;
		getCriticalPredecessors().clear();
		getCriticalSuccessors().clear();
		criticalTime = UNDEFINED;
		isCriticalPath = false;
		isScheduled = false;
		getInheritedAllocations().clear();
		getInheritedConstraints().clear();
	}

	/**
	 * Used during scheduling to provide a simple way of marking
	 * a task as scheduled.
	 * @return the isScheduled
	 */
	public boolean isScheduled() {
		return isScheduled;
	}

	/**
	 * @param b
	 */
	public void setScheduled(boolean b) {
		isScheduled = b;
		
	}

	/**
	 * Set of resource allocations that includes any inherited from
	 * parent tasks.  Transient - used during scheduling and not part
	 * of the core model.
	 * @return the inheritedAllocations
	 */
	public Set<Allocation> getInheritedAllocations() {
		if(inheritedAllocations == null){
			inheritedAllocations = new HashSet<Allocation>();
		}
		return inheritedAllocations;
	}

	/**
	 * Set of constraints that includes any inherited from
	 * parent tasks.  Transient - used during scheduling and not part
	 * of the core model.
	 * @return the inheritedConstraints
	 */
	public Set<Constraint> getInheritedConstraints() {
		if(inheritedConstraints == null){
			inheritedConstraints = new HashSet<Constraint>();
		}
		return inheritedConstraints;
	}


	

}
