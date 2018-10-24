/*
 * Scheduler.java
 * Project: ProjectView
 * Created on 4 Feb 2008
 *
 */
package uk.co.alvagem.projectview.core.schedule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math.MathException;

import uk.co.alvagem.projectview.model.Allocation;
import uk.co.alvagem.projectview.model.Calendar;
import uk.co.alvagem.projectview.model.Constraint;
import uk.co.alvagem.projectview.model.DefaultCalendar;
import uk.co.alvagem.projectview.model.Dependency;
import uk.co.alvagem.projectview.model.Resource;
import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.projectview.model.WorkingDay;

public class Scheduler {

	/** Weights to use to determine task priority */
	private TaskWeights weights = new TaskWeights();

	/** Track availability for each resource used. */
	private Map<Resource,ResourceSchedule> resources = new HashMap<Resource,ResourceSchedule>();

	/** Earliest date we can schedule a task is today */
	private Date earliest = new Date();

	/** Dummy resource for unallocated tasks. */
	private ResourceSchedule defInfo = new ResourceSchedule(earliest);

	private static final long TICKS_PER_DAY = 1000L * 60L * 60L * 24L;
	

	public Scheduler(){

    	// Magic up some weights for scheduling.  Note can use getWeights() to change defaults.
    	weights.setCriticalTime(1.0f);
    	weights.setPriority(0.2f);
    	weights.setRelativeUncertainty(0.1f);
    	weights.setPosition(0.05f);


    	// Set up the default allocation.
    	Allocation defAlloc = new Allocation();
    	Resource defResource = new Resource();
    	defResource.setName("DEFAULT");
    	defResource.setAvailability(DefaultCalendar.getInstance()); 
    	defAlloc.setResource(defResource);
    	defAlloc.setUtilisation(1.0f);
    	defInfo.setAllocation(defAlloc);
    	resources.put(defResource,defInfo);
    	
    	// Start scheduling tasks the following day at the earliest.
    	Date tomorrow = new Date();
    	tomorrow.setTime(tomorrow.getTime() + TICKS_PER_DAY -1);
    	earliest = DateUtils.zeroTime(tomorrow);
	}
	
	/**
	 * Sets the earliest time that any task can be scheduled for.  Defaults to tomorrow but can
	 * be set in the future.
	 * @param earliest
	 */
	public void setEarliestStart(Date earliest){
		this.earliest.setTime(earliest.getTime());
	}
	
	/**
	 * Allows editing of the task weights.
	 * @return
	 */
	public TaskWeights getWeights(){
		return weights;
	}
	
	/**
	 * Gets the resource schedules that show how the resources are scheduled.
	 * @return map of resource schedule.
	 */
	public  Map<Resource,ResourceSchedule> getScheduledResources(){
		return  Collections.unmodifiableMap(resources);
	}
	
    /**
     * Clears the scheduling information
     * for the given hierarchy rooted at task.
	 * @param task is the root of the hierarchy to clear.
	 */
	private void clearSchedulingInformation(Task task) {
    	task.resetSchedulingInformation();
    	for(Task child : task.getSubTasks()){
    		clearSchedulingInformation(child);
    	}
	}

	/**
	 * Runs through the task hierarchy making sure that any resource allocations
	 * are inherited by their (primitive) children.
	 * @param root is the root of the task hierarchy.
	 * @param inherited is the set of allocations. Should be an empty set
	 * when called with the root node of the hierarchy
	 */
	private void inheritAllocations(Task root, Set<Allocation> inherited){
		if(root.getSubTasks().isEmpty()){
			root.getInheritedAllocations().addAll(root.getAllocations());
			root.getInheritedAllocations().addAll(inherited);
		} else {
			inherited.addAll(root.getAllocations());
			for(Task sub : root.getSubTasks()){
				inheritAllocations(sub, inherited);
			}
		}
	}

	/**
	 * Runs through the task hierarchy making sure that any constraints.
	 * are inherited by their (primitive) children.
	 * @param root is the root of the task hierarchy.
	 * @param inherited is the set of constraints. Should be an empty set
	 * when called with the root node of the hierarchy
	 */
	private void inheritConstraints(Task root, Set<Constraint> inherited){
		if(root.getSubTasks().isEmpty()){
			root.getInheritedConstraints().addAll(root.getConstraints());
			root.getInheritedConstraints().addAll(inherited);
		} else {
			inherited.addAll(root.getConstraints());
			for(Task sub : root.getSubTasks()){
				inheritConstraints(sub, inherited);
			}
		}
	}

	/**
	 * Sets up the scheduleElapsed time for all the leaf tasks.  This depends on the
	 * task allocations and estimate strategy.
	 * @param root
	 * @param estimateStrategy
	 */
	private void setScheduleElapsed(Task root, EstimateStrategy estimateStrategy) throws SchedulingException{
		if(root.getSubTasks().isEmpty()){
			calculateNominalElapsedTime(root, estimateStrategy);
		} else {
			for(Task sub : root.getSubTasks()){
				setScheduleElapsed(sub, estimateStrategy);
			}
		}
		
	}

	/**
	 * Gets a nominal value for elapsed time for scheduling. Only valid for
	 * simple tasks (i.e. ones without child tasks).  This should be called for each leaf
	 * task at the start of a scheduling pass to set up a value for the estimated effort and
	 * hence elapsed time using the estimate uncertainty.  
	 * Nominal elapsed time uses the standard working week for the resources calendars as, at this 
	 * point in time, we don't know when the tasks etc will take place.  Hence we
	 * can't allow for holidays etc.
	 * Sets both scheduleEffort and scheduleElapsed.
	 */
	private void calculateNominalElapsedTime(Task task, EstimateStrategy estimateStrategy) throws SchedulingException{
		assert(task.getSubTasks().isEmpty());
		float time = 0;
		if(task.isEffortDriven()){
			try {
				float burnRate = 0;
				// Find out the number of resources allocated to this task and figure the burn rate in
				// terms of days of effort per elapsed day.
				for(Allocation alloc : task.getAllocations()){
					float utilisation = alloc.getUtilisation();
					Calendar calendar = alloc.getResource().getAvailability();
					// If resource has no calendar assigned, assume 100% else calculate. 
					float availability = 1.0f;
					if(calendar != null){
						availability = calendar.estimateAvailability();
					}
					burnRate += utilisation * availability;
				}
				
				// If no resource explicitly assigned make the assumption that it will be resourced so 
				// that 5 days effort will be expended every week.  
				if(burnRate == 0) {
					burnRate = DefaultCalendar.getInstance().estimateAvailability();
				}
				
				float effort = estimateStrategy.getEffortNeeded(task);
				task.setScheduleEffort(effort);
				time = effort / WorkingDay.DEFAULT.getHoursPerDay();
				time /= burnRate;
				//System.out.println("Task " + task.getName() + " est " + time + " days for " + effort + " hours effort");
			} catch (MathException e) {
				throw new SchedulingException("Unable to estimate elapsed time for task " + task.getName(),e);
			}
			
		} else {
			// Not effort driven so use tasks stated elapsed time.
			time = task.getElapsedTime();
		}
		task.setScheduleElapsed(time);
	}

	/**
	 * @param root
	 * @throws SchedulingException
	 */
	public void schedule(Task root) throws SchedulingException{
		schedule(root, new FixedEstimateStrategy(0.5f));
	}
	
	
	/**
	 * @param root
	 * @param estimateStrategy
	 * @throws SchedulingException
	 */
	public void schedule(Task root, EstimateStrategy estimateStrategy) throws SchedulingException{
		
     	clearSchedulingInformation(root);
    	inheritAllocations(root, new HashSet<Allocation>());
    	inheritConstraints(root, new HashSet<Constraint>());
    	setScheduleElapsed(root, estimateStrategy);
    	
    	CriticalPath cp = new CriticalPath(root);
    	PriorityList queue = new PriorityList(root);
    	queue.orderTasks(weights);
    	
    	List<Task> taskList = queue.getList();
//    	for(Task task : taskList){
//    		System.out.println("Task " + task.getName() + " priority " + task.getSchedulePriority());
//    	}
    	
    	cp.getStart().setScheduled(true);
    	cp.getStart().setFinishDate(earliest);
    	
    	// Anything that's underway or complete shouldn't have the start date modified
    	// and completed tasks shouldn't have the finish date modified.
    	try {
			scheduleStartedTasks(taskList);
		} catch (MathException e1) {
			throw new SchedulingException("Unable to schedule task list", e1);
		}
    	
    	// Remove any constrained tasks for special treatment
    	List<Task> constrained = removeConstrained(taskList);

    	while(!taskList.isEmpty() || !constrained.isEmpty()){

    		Set<ResourceSchedule> resourceSet = Collections.emptySet();
    		Date startDate = new Date(earliest.getTime());
    		
    		// Look for the first schedulable task.  First look in the list of constrained tasks
    		// then, if not, try the unconstrained tasks.
    		Task toSchedule = null;
    		
    		for(Task task : constrained){
        		resourceSet = getResourceSet(task);
        		startDate = getEarliestStartDate(resourceSet);
    			if(canSchedule(task,startDate)){
    				toSchedule = task;
    				break;
    			}
    		}
    		if(toSchedule != null){
    			constrained.remove(toSchedule);
    		}
    		                                                                                                     
    		if(toSchedule == null){
	    		for(Task task : taskList){
	        		resourceSet = getResourceSet(task);
	        		startDate = getEarliestStartDate(resourceSet);
	    			if(canSchedule(task,startDate)){
	    				toSchedule = task;
	    				break;
	    			}
	    		}
	    		taskList.remove(toSchedule);
    		}
    		
    		if(toSchedule == null){
    			throw new IllegalStateException("Can't find task to schedule");
    		}
    		
    		//System.out.println("Scheduling " + toSchedule.getName() + " priority " + toSchedule.getSchedulePriority());
    		//System.out.println(toSchedule.getName() + " est start date " + df.format(startDate));
    		
    		// Now we've got a start date for the task, figure out how 
    		// long it will take.
			float effortNeeded = toSchedule.getScheduleEffort();
			Date finishDate = scheduleTask(resourceSet, startDate, effortNeeded);
			
			float daysNeeded = (float)(finishDate.getTime() - startDate.getTime()) / (float)(1000L * 3600 * 24);

			toSchedule.setStartDate(startDate);
			toSchedule.setFinishDate(finishDate);
			toSchedule.setElapsedTime(daysNeeded);
			toSchedule.setScheduled(true);
    	}
    }

    
	/**
	 * Schedules a single task
	 * @param resourceSet is the set of ResourceSchedule that identifies which resources
	 * are needed for this task and when they're available.
	 * @param startDate is the start date for this task.
	 * @param hoursNeeded is the number of hours effort needed to complete this task.  The
	 * effort may be distributed across multiple resources.
	 * @return the finish date for the task.
	 */
	private Date scheduleTask(Set<ResourceSchedule> resourceSet, Date startDate,
			float hoursNeeded) {

		float maxHours = 0; // number of hours in partial day needed;
		Date day = new Date(startDate.getTime());

		float hoursSoFar = 0;
        //System.out.println("Starting on "+ df.format(startDate));
		while (hoursNeeded > 0){

			Date thisDay = DateUtils.zeroTime(day);
			
			// Calculate the number of hours of work available on this day 
			// from the resource set.
			float hoursWorkThisDay = 0.0f;
			for(ResourceSchedule info : resourceSet){
				float work = info.availableOn(day);
				hoursWorkThisDay += work;
			}

			//System.out.println("Working " + hoursWorkThisDay + " on " + df.format(thisDay));
			
			// Check to see if work actually starts on task today. Quite possible start date
			// will move onto next (or subsequent if w/e or holiday) days.
			if(hoursSoFar == 0 && hoursWorkThisDay > 0){
				Date zeroHour = WorkingDay.DEFAULT.getStartTimeOn(thisDay);
				if(startDate.before(zeroHour)){
					startDate.setTime(zeroHour.getTime());
					//System.out.println("Start date set to zero hour " + df.format(startDate));
				}
			}
			hoursSoFar += hoursWorkThisDay;
			
			float burnFraction = 1.0f;  // use all available time.
			if(hoursNeeded > hoursWorkThisDay){
			    hoursNeeded -= hoursWorkThisDay;
			    day.setTime(thisDay.getTime() + TICKS_PER_DAY);
			} else {
				// Don't need all the time to finish this task.
				burnFraction = hoursNeeded / hoursWorkThisDay;
				hoursNeeded = 0;
			}
			
			maxHours = 0;
			for(ResourceSchedule info : resourceSet){
				float hours = info.availableOn(thisDay);
				hours *= burnFraction; // may only need partial day
				maxHours = Math.max(hours,maxHours);
				info.burnHoursOn(thisDay, hours);
			}
		}

		Date finishDate = WorkingDay.DEFAULT.getStartTimeOn(day);
		long offset = (long)(maxHours * 60.0 * 60.0 * 1000.0);
		finishDate.setTime(finishDate.getTime() + offset);
		
        // Update resource info record(s) to track utilisation of
        // each resource.
		for(ResourceSchedule info : resourceSet){
			info.setDate(finishDate);
		}
		
		return finishDate;
	}

	
    /**
     * Anything that's underway or complete shouldn't have the start date modified
     * and completed tasks shouldn't have the finish date modified.
	 * @param taskList
	 */
	private void scheduleStartedTasks(List<Task> taskList) throws MathException {
		List<Task>ongoing = new LinkedList<Task>();
		
		Date today = DateUtils.zeroTime(new Date());
		
		for(Task task : taskList){
			// If task is underway then just work out how long it will take to complete given
			// the current fraction complete.
			if(task.getFractionComplete() > 0){

				if(task.getFractionComplete() < 1){ // Although if it's complete don't re-schedule!
					Date finishDate;
		    		Set<ResourceSchedule> resourceSet = getResourceSet(task);
	
		    		if(task.isEffortDriven()){
			    		float workNeeded = (1 - task.getFractionComplete()) * task.getScheduleEffort();
			    		finishDate = scheduleTask(resourceSet,today,workNeeded);

			    		DateFormat fmt = SimpleDateFormat.getDateInstance();
			    		System.out.println("Started task " + task.getName() + " " + workNeeded + " finish " + fmt.format(finishDate));
			    		task.setFinishDate(finishDate);
					} else {
						// not effort driven so take existing finish date.
						finishDate = task.getFinishDate();
					}
		    		
			        // Update resource info record(s) to track utilisation of
			        // each resource.
		    		for(ResourceSchedule info : resourceSet){
		    			info.setDate(finishDate);
		    		}
				}

				//System.out.println("Task " + task.getName() + " has been started: " + task.getFractionComplete());
	    		task.setScheduled(true);
	    		ongoing.add(task);

			}
		}
		
		taskList.removeAll(ongoing);
	}

//	private void scheduleConstrained(List<Task> toSchedule){
//		for(Task task : toSchedule){
//			for(Constraint constraint : task.getInheritedConstraints()){
//				constraint.applyToTask(task);
//			}
//		}
//	}
	
	/**
	 * Runs through the main task list and removes all the constrained tasks returning them
	 * in another list.  Task order is preserved.
	 * @param toSchedule is the initial complete list of tasks.
	 * @return an ordered (by priority) list of tasks which also have constraints.
	 */
	private List<Task> removeConstrained(List<Task> toSchedule){
		List<Task> constrained = new LinkedList<Task>();
		
		for(Task task : toSchedule){
			if(!task.getInheritedConstraints().isEmpty()){
				constrained.add(task);
			}
		}
		toSchedule.removeAll(constrained);
		return constrained;
	}

	/**
	 * Given a resource set, work out the earliest a task can start that
	 * uses that resource set.  This is the first date that any of the 
	 * task's resources can supply resource.
	 * @param resourceSet is the resource set
	 * @return
	 */
	private Date getEarliestStartDate(Set<ResourceSchedule> resourceSet) {
		// Find the earliest date the task can start.  It's the earliest
		// one of its resources can start.
		Date startDate = null;
		for(ResourceSchedule info : resourceSet){
			if(startDate == null){
				startDate = info.getDate();
			} else if(info.getDate().before(startDate)){
				startDate = info.getDate();
			}
		}
		if(startDate.before(earliest)){
			startDate = earliest;
		}


		return new Date(startDate.getTime());
	}

	/**
	 * Gets the set of ResourceInfo that describes the availability of
	 * the set of resources needed for a task.
	 * @param toSchedule
	 * @return
	 */
	private Set<ResourceSchedule> getResourceSet(Task toSchedule) {
		// Schedule based on workload for the task's resource(s).
		Set<Allocation> allocations = toSchedule.getInheritedAllocations();
		
		// Get the set of ResourceInfo that track the utilisation of
		// resources needed for this task.
		Set<ResourceSchedule> resourceSet = new HashSet<ResourceSchedule>();
		if(allocations.size() == 0) {
			// Not allocated - use dummy
			resourceSet.add(defInfo);
		} else {
			for(Allocation allocation : allocations){
				ResourceSchedule info = resources.get(allocation.getResource());
				if(info == null){
					info = new ResourceSchedule(earliest);
					resources.put(allocation.getResource(),info);
				}
				info.setAllocation(allocation);
				resourceSet.add(info);
			}
		}
		return resourceSet;
	}

    /**
     * Determines whether a task can start on the given date.  A task
     * can only start if its predecessors have all been scheduled and
     * completed by the given date.  Any task constraints should also
     * be satisfied.
	 * @param task
	 * @return
	 */
	private boolean canSchedule(Task task, Date start) {

		for(Dependency pred : task.getCriticalPredecessors()){

			// If a predecessor task hasn't been scheduled then can't
			// schedule this one.
			if(!pred.getPredecessor().isScheduled()){
				//System.out.println(task.getName() + " - predecessor " + pred.getPredecessor().getName() + " not scheduled");
				return false;
			}

			float lag = pred.getLag();
			long targetStart = pred.getPredecessor().getFinishDate().getTime();
			targetStart += (long)((double)lag * TICKS_PER_DAY);
			if(targetStart > start.getTime()){
				//System.out.println(task.getName() + " - predecessor not finished");
				return false;
			}
			
			for(Constraint constraint : task.getInheritedConstraints()){
				if(!constraint.canStartOn(task, start)){
					return false;
				}
			}
		}
		return true;
	}

	
	

}
