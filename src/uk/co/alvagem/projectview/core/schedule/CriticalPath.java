/**
 * 
 */
package uk.co.alvagem.projectview.core.schedule;

import java.util.HashSet;
import java.util.Set;

import uk.co.alvagem.projectview.model.Allocation;
import uk.co.alvagem.projectview.model.Calendar;
import uk.co.alvagem.projectview.model.Dependency;
import uk.co.alvagem.projectview.model.Task;

/**
 * Converts a task hierarchy to a critical path graph.  The graph is
 * created using the tasks' criticalPredecessors and criticalSuccessors
 * dependency collections.  Once created all the primitive tasks are linked
 * into the start and finish nodes.
 * @author bruce.porteous
 *
 */
public class CriticalPath {

	private Task start;
	private Task finish;

	/**
	 * Creates a new critical path for the given hierarchy.
	 * Building the critical path network also sets the critical
	 * time values for each simple task in the hierarchy.
	 * @param root is the root node for the hierarchy.
	 */
	public CriticalPath(Task root){
		buildCriticalPath(root);
		backflow();
	}
	
    /**
     * Builds the critical path network from the hierarchy of tasks rooted
     * at root.  A tasks constraints are inherited by its children e.g.
     * if a composite task can't start before another task if complete that
     * implies that none of the composite's children can start.  
     * @param root
     */
    private void buildCriticalPath(Task root){
    	start = new Task();
    	start.setName("START");
    	finish = new Task();
    	finish.setName("FINISH");
    	
    	Set<Dependency> dependencies = getDependencies(root, new HashSet<Dependency>());
    	expandComposites(dependencies);
    	linkStartFinishNodes(root);
    }

 
	// 1. Copy dependencies from normal to critical list.
	// 2. traverse hierarchy and flow down any composite dependencies
	// 3. Link in start and finish nodes.
	
	/**
	 * Gets the complete set of dependencies for a task hierarchy. Note that any
	 * dependencies to inactive tasks are ignored.
	 * @param root is the root task of the hierarchy.
	 * @param set is the set of dependencies to fill.
	 * @return the supplied set.
	 */
	private Set<Dependency> getDependencies(Task root, Set<Dependency> set){
		// Bidirectional so only use successors to avoid duplication.
		for(Dependency dep : root.getSuccessors()){
			if(dep.getPredecessor().isActive() && dep.getSuccessor().isActive()){
				set.add(dep);
			}
		}
		
		for(Task sub : root.getSubTasks()){
			getDependencies(sub, set);
		}
		return set;
	}
	
	/**
	 * Sets the critical dependencies from the given set of dependencies.
	 * Any dependencies to composite tasks are expanded to dependencies to
	 * the composite's primitive children.
	 * @param dependencies
	 */
	private void expandComposites(Set<Dependency> dependencies){
		
		for(Dependency dep : dependencies){
			if(dep.getPredecessor().getSubTasks().isEmpty() && 
					dep.getSuccessor().getSubTasks().isEmpty()){
				assert(dep.getPredecessor().isActive());
				assert(dep.getSuccessor().isActive());
				dep.getPredecessor().getCriticalSuccessors().add(dep);
				dep.getSuccessor().getCriticalPredecessors().add(dep);
			} else {
				// predecessor or successor task is a composite - expand.
				Set<Task> predecessors = getPrimitiveChildren(dep.getPredecessor(), new HashSet<Task>());
				Set<Task> successors = getPrimitiveChildren(dep.getSuccessor(), new HashSet<Task>());
				
				for(Task pred : predecessors){
					for(Task succ : successors){
						assert(pred.isActive());
						assert(succ.isActive());
						Dependency link = new Dependency();
						link.setLag(dep.getLag());
						link.setPredecessor(pred);
						link.setSuccessor(succ);
						pred.getCriticalSuccessors().add(link);
						succ.getCriticalPredecessors().add(link);
					}
				}
			}
		}
	}
	
	/**
	 * Gets the set of primitive tasks in the project.  These are the
	 * "real" tasks that need to be scheduled.  Any tasks that are not
	 * active are ignored.  
	 * @param root is the root of the task hierarchy.
	 * @param set is the set to be filled with primitive tasks.
	 * @return the given set.
	 */
	private Set<Task> getPrimitiveChildren(Task root, Set<Task>set) {
		
		if(!root.isActive()){
			return set;
		}
		
		if(root.getSubTasks().isEmpty()){
			set.add(root);
		} else {
			for(Task sub : root.getSubTasks()){
				getPrimitiveChildren(sub, set);
			}
		}
		return set;
	}

	/**
	 * Runs through the task hierarchy identifying and linking potential
	 * start and finish tasks.  Potential start tasks have no predecessors.
	 * Potential finish tasks have no successors.  Potential start tasks 
	 * become successors of the START node, potential finish tasks become
	 * predecessors of the FINISH node.  Inactive tasks are ignored.
	 * @param root is the root of the task hierarchy.
	 */
	private void linkStartFinishNodes(Task root){

		if(!root.isActive()){
			return;
		}
		
		// If a task has no successors then it could be
		// a final task.
		if(root.getCriticalSuccessors().isEmpty()){
			Dependency dep = new Dependency();
			dep.setPredecessor(root);
			dep.setSuccessor(finish);
			root.getCriticalSuccessors().add(dep);
			finish.getCriticalPredecessors().add(dep);
		}
		
		// Similarly, if a task has no predecessors, then it could
		// be a start task.
		if(root.getCriticalPredecessors().isEmpty()){
			Dependency dep = new Dependency();
			dep.setPredecessor(start);
			dep.setSuccessor(root);
			start.getCriticalSuccessors().add(dep);
			root.getCriticalPredecessors().add(dep);
		}

		for(Task task : root.getSubTasks()){
			linkStartFinishNodes(task);
    	}
		
	}
	
	

    
    /**
     * Determines the critical time for each node based on the backflow algorithm.
     */
    private void backflow(){
    	finish.setCriticalTime(0);
    	
    	Set<Task> toProcess = new HashSet<Task>();
    	
    	//Prime the set with all the precursors to finish.
    	for(Dependency succ : finish.getCriticalPredecessors()){
    		toProcess.add(succ.getPredecessor());
    	}
    	
    	while(!toProcess.isEmpty()){
    		Task task = getFirstReady(toProcess);
    		
        	float maxCrit = 0;
        	float elapsed = task.getElapsedTime();
        	elapsed = getNominalElapsedTime(task);
        	for(Dependency succ : task.getCriticalSuccessors()){
        		float crit = elapsed + succ.getLag() + succ.getSuccessor().getCriticalTime();
        		if(crit > maxCrit){
        			maxCrit = crit;
        		}
        	}
        	task.setCriticalTime(maxCrit);
        	
        	// Flow back down the predecessors list and put them on the 
        	// to process list.
        	for(Dependency pred : task.getCriticalPredecessors()){
        		Task pTask = pred.getPredecessor();
        		toProcess.add(pTask);  // maybe on already but idempotent as it's a set.
        	}

    	}
    }

    
    /**
     * Gets the first ready task off the run queue.
	 * @param toProcess is the set of tasks to process.
	 * @return a ready task from the set.
	 */
	private Task getFirstReady(Set<Task> toProcess) {
		Task run = null;
		for(Task task : toProcess){
			if(isTaskReady(task)){
				run = task;
				break;
			}
		}
		assert(run != null);
		toProcess.remove(run);
		return run;
	}

	/**
	 * Determines if a task is ready to have its critical
	 * time calculated.  A task is ready if all its successors
	 * have their critical times set.
	 * @param task is the task to check.
	 * @return true if it can be processed.
	 */
	private boolean isTaskReady(Task task) {
    	for(Dependency succ : task.getCriticalSuccessors()){
    		if(succ.getSuccessor().getCriticalTime() == Task.UNDEFINED){
    			return false;
    		}
    	}
    	return true;
	}

	/**
	 * Gets a nominal value for elapsed time for scheduling. Only valid for
	 * simple tasks (i.e. ones without child tasks).
	 * @return nominal elapsed time in days.
	 */
	private float getNominalElapsedTime(Task task){
		assert(task.getSubTasks().isEmpty());
		return task.getScheduleElapsed();
	}

	/**
	 * Gets the start task of the critical path graph.
	 * @return
	 */
	public Task getStart() {
		return start;
	}
	
	/**
	 * Gets the finish task of the critical path graph.
	 * @return
	 */
	public Task getFinish() {
		return finish;
	}
	
    /**
     * Dumps the Critical Path graph to stdout starting at
     * START and working through the dependency list.
     */
    public void dumpCPGraph(){
    	dumpCPGraph(start,new HashSet<Task>());
    }
    
    /**
     * Recursive implementation for dumping the Critical Path
     * graph to stdout.  Use by calling dumpCPGraph().
     * @param task is the current task to dump.
     * @param visited is the set of visited nodes.
     */
    private void dumpCPGraph(Task task, Set<Task>visited){
    	
    	if(visited.contains(task)){
    		return;
    	}
    	visited.add(task);
    	
    	for(Dependency dep : task.getCriticalSuccessors()){
    		Task pred = dep.getPredecessor();
    		Task succ = dep.getSuccessor();
    		System.out.print(pred.getName());
    		System.out.print("[" + Float.toString(pred.getCriticalTime()) + "]");
    		System.out.print("-->");
    		System.out.print(succ.getName());
    		System.out.print("[" + Float.toString(succ.getCriticalTime()) + "]");
    		System.out.println();
    	}

    	for(Dependency dep : task.getCriticalSuccessors()){
    		dumpCPGraph(dep.getSuccessor(), visited);
    	}
    }

}
