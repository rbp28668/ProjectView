/**
 * 
 */
package uk.co.alvagem.projectview.core.schedule;

import uk.co.alvagem.projectview.model.Task;

/**
 * @author bruce.porteous
 *
 */
public class SimpleEstimateStrategy implements EstimateStrategy {

	/* (non-Javadoc)
	 * @see uk.co.alvagem.projectview.core.schedule.EstimateStrategy#getEffortNeeded(uk.co.alvagem.projectview.model.Task)
	 */
	public float getEffortNeeded(Task task) {
		return task.getEstimatedEffort();
	}

}
