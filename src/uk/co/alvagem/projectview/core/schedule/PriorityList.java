/**
 * 
 */
package uk.co.alvagem.projectview.core.schedule;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import uk.co.alvagem.projectview.model.Task;

/**
 * A priority list used for scheduling.  Create the list from a task hierarchy then
 * call orderTasks(TaskWeights) to set the ordering for the list.  The getList() method
 * then returns a copy of the underlying list that can be used to schedule the tasks.
 * 
 * @author bruce.porteous
 *
 */
public class PriorityList {

	private List<Task> list = new LinkedList<Task>();
	private TaskWeights maxima = new TaskWeights();
	
	
	/**
	 * Creates a task list with the task hierarchy based at root.
	 * @param root is the root of the task hierarchy to use in the list.
	 */
	public PriorityList(Task root){
		add(root,0);
		invertPosition();
		setMaxima(maxima);
	}
	
	/**
	 * Get a copy of the underlying list to use for scheduling.
	 * @return a copy of the list.
	 */
	public List<Task> getList(){
		return new LinkedList<Task>(list);
	}
	
	/**
	 * Adds the task to the task list and sets its index value.  Only simple tasks
	 * are added, composites are not.
	 * @param task is the task to add.
	 * @param index is the index to use for this task.
	 * @return an updated index value.
	 */
	private int add(Task task, int index){
		if(task.getSubTasks().isEmpty()){
			//System.out.println("Adding task " + task.getName() + " to list at position " + index);
			task.setIndex(index);
			++index;
			list.add(task);
		} else {
			for(Task sub : task.getSubTasks()){
				index = add(sub, index);
			}
		}
		return index;
	}

	/**
	 * The indices are set up with 0 being the first task.  We
	 * want it the other way round...
	 */
	private void invertPosition(){
		int top = list.size() - 1;
		for(Task task : list){
			task.setIndex(top - task.getIndex());
		}
	}
	

	/**
	 * Orders the tasks in the list according to the supplied weights.
	 * @param weights are the weights to use to determine the ordering.
	 */
	public void orderTasks(TaskWeights weights){

		TaskWeights use = new TaskWeights();
		use.setCriticalTime((maxima.getCriticalTime() > 0) ? weights.getCriticalTime() / maxima.getCriticalTime() : 0);
		use.setUncertainty((maxima.getUncertainty() > 0) ? weights.getUncertainty() / maxima.getUncertainty() : 0);
		use.setRelativeUncertainty((maxima.getRelativeUncertainty() > 0) ? weights.getRelativeUncertainty() / maxima.getRelativeUncertainty() : 0);
		use.setPriority((maxima.getPriority() > 0) ? weights.getPriority() / maxima.getPriority() : 0);
		use.setTaskLength((maxima.getTaskLength() > 0) ? weights.getTaskLength() / maxima.getTaskLength() : 0);
		use.setPosition((maxima.getPosition() > 0) ? weights.getPosition() / maxima.getPosition() : 0);

		for(Task task : list ){
			float val = weightTask(task, use);
			task.setSchedulePriority(val);
		}
		
		Collections.sort(list, new TaskComparator());
	}
	
	/**Gets the weighted priority score for a task.  Note that the
	 * weights should already have been normalised by the maxima.
	 * @param task is the task to get the priority score for.
	 * @param weights is the set of weights to give the different
	 * aspects of the task.
	 * @return a numerical weighting that will be used to set the
	 * priority of the task.
	 */
	private float weightTask(Task task, TaskWeights weights){
		
		float val = 0.0f;
		val += task.getCriticalTime() * weights.getCriticalTime();
		val += task.getEstimateSpread() * weights.getUncertainty();
		if(task.getEstimatedEffort() > 0.0f){
			val += (task.getEstimateSpread()/task.getEstimatedEffort())	* weights.getRelativeUncertainty();
		}
		val += task.getPriority() * weights.getPriority();
		val += task.getEstimatedEffort() * weights.getTaskLength();
		val += task.getIndex() * weights.getPosition();
		return val;
	}
	
	/**
	 * Gets the maximum values for each of the values to be weighted so that the different
	 * parameters can be normalised to the range 0..1.
	 * @param weights is used to return the weights.
	 */
	private void setMaxima(TaskWeights weights){
		for(Task task : list){
			weights.setCriticalTime(Math.max(weights.getCriticalTime(), task.getCriticalTime()));
			weights.setUncertainty(Math.max(weights.getUncertainty(), task.getEstimateSpread()));
			if(task.getEstimatedEffort() > 0.0f){
				weights.setRelativeUncertainty(Math.max(weights.getRelativeUncertainty(), 
						task.getEstimateSpread()/task.getEstimatedEffort()));
			}
			weights.setPriority(Math.max(weights.getPriority(), task.getPriority()));
			weights.setTaskLength(Math.max(weights.getTaskLength(), task.getEstimatedEffort()));
		}
		weights.setPosition(list.size());
	}
	
	
	/**
	 * Comparator to order tasks by their scheduling priority into 
	 * descending order.
	 * @author bruce.porteous
	 */
	private  static class TaskComparator implements Comparator<Task>{

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Task o1, Task o2) {
			return (int) Math.signum(o2.getSchedulePriority() - o1.getSchedulePriority());
		}
		
	}
}
