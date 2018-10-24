/**
 * 
 */
package uk.co.alvagem.projectview.core.schedule;

import java.util.Random;

import org.apache.commons.math.MathException;

import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.projectview.model.UncertaintyType;

/**
 * Estimate strategy that uses the uncertainty to pick the effort using random probability.
 * @author bruce.porteous
 *
 */
public class VariableEstimateStrategy implements EstimateStrategy {

	private Random random = new Random();
	/* (non-Javadoc)
	 * @see uk.co.alvagem.projectview.core.schedule.EstimateStrategy#getEffortNeeded(uk.co.alvagem.projectview.model.Task)
	 */
	public float getEffortNeeded(Task task) throws MathException {
		UncertaintyType uncertainty = task.getUncertaintyType();
		float rnd = random.nextFloat();
		float effort = uncertainty.getEstimatedEffort(task.getEstimatedEffort(), task.getEstimateSpread(), rnd);
		return effort;
	}

}
