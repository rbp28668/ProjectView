/**
 * 
 */
package uk.co.alvagem.projectview.core.schedule;

import org.apache.commons.math.MathException;

import uk.co.alvagem.projectview.model.Task;

/**
 * An interface for a Strategy pattern where different strategies can be applied
 * to working out how long a task is likely to take.  For example the default
 * strategy might be to just take the estimated effort whereas other strategies might
 * take a random or worst case view.
 * @author bruce.porteous
 *
 */
public interface EstimateStrategy {

	public float getEffortNeeded(Task task) throws MathException;
}
