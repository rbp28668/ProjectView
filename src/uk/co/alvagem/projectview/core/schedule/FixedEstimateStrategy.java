/**
 * 
 */
package uk.co.alvagem.projectview.core.schedule;

import org.apache.commons.math.MathException;

import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.projectview.model.UncertaintyType;

/**
 * Estimate strategy that uses the uncertainty to pick the effort using fixed probability.
 * @author bruce.porteous
 *
 */
public class FixedEstimateStrategy implements EstimateStrategy {

	/** Desired probability to estimate task length at */
	private float p;
	
	public FixedEstimateStrategy(float p){
		if(p <=0 || p >= 1){
			throw new IllegalArgumentException("task estimate probability must be 0<p<1");
		}
		this.p = p;
	}
	
	/* (non-Javadoc)
	 * @see uk.co.alvagem.projectview.core.schedule.EstimateStrategy#getEffortNeeded(uk.co.alvagem.projectview.model.Task)
	 */
	public float getEffortNeeded(Task task) throws MathException {
		UncertaintyType uncertainty = task.getUncertaintyType();
		float effort = uncertainty.getEstimatedEffort(task.getEstimatedEffort(), task.getEstimateSpread(), p);
		return effort;
	}

}
