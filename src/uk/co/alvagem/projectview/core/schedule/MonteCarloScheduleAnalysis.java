/**
 * 
 */
package uk.co.alvagem.projectview.core.schedule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.projectview.model.WorkingDay;

/**
 * Uses Monte-Carlo analysis to re-schedule a project multiple times using the 
 * uncertainty figures to allow overall project scheduling to be expressed.
 * @author bruce.porteous
 *
 */
public class MonteCarloScheduleAnalysis {
	
	private float elapsed[];
	private float effort[];
	
	public MonteCarloScheduleAnalysis(){
		
	}
	
	public void schedule(Task task, int samples) throws SchedulingException{
		
		elapsed = new float[samples];
		effort = new float[samples];
		
		EstimateStrategy estimateStrategy = new VariableEstimateStrategy();
		
		for(int pass = 0; pass < samples; ++pass){
			Scheduler scheduler = new Scheduler();
			System.out.println("Scheduling pass " + pass);
			scheduler.schedule(task, estimateStrategy);
			task.updateFromSubTree();
            task.fireChanged();
            DateFormat fmt = SimpleDateFormat.getDateInstance();
            System.out.println("Start "+ fmt.format(task.getStartDate()) + " to " + fmt.format(task.getFinishDate()));
			
			elapsed[pass] = task.getElapsedTime();
			effort[pass] = getScheduledEffort(task);
			// Note - effort is stored in hours - convert to working days to give same
			// scaling as elapsed time.
			effort[pass] /= WorkingDay.DEFAULT.getHoursPerDay();

			
			System.out.println(elapsed[pass]);
		}
		
		Arrays.sort(elapsed);
		Arrays.sort(effort);
	}
	
	public float[] getElapsed(){
		return elapsed;
	}
	
	public float[] getEffort(){
		return effort;
	}
	
	
	/**
	 * Recursive function that sums the amount of effort estimated for a single pass
	 * of the scheduler.
	 * @param task is the root task to sum from.
	 * @return the total scheduled effort based on that task.
	 */
	private float getScheduledEffort(Task task){
		float effort;
		if(task.getSubTasks().isEmpty()){
			effort = task.getScheduleEffort();
		} else {
			effort = 0;
			for(Task sub: task.getSubTasks()){
				effort += getScheduledEffort(sub);
			}
		}
		
		return effort;
	}
}
