/**
 * 
 */
package uk.co.alvagem.projectview.core.schedule;


/**
 * Weightings to be applied to different aspects of a task to 
 * get an overall priority score for scheduling.
 * @author bruce.porteous
 *
 */
public class TaskWeights{
	/** Weight for critical time */
	private float criticalTime;
	
	/** Weight for absolute value of estimate spread */
	private float uncertainty;
	
	/** Weight for estimate spread relative to effort */
	private float relativeUncertainty;
	
	/** Weight for user priority value */
	private float priority;
	
	/** Weight for estimate effort */
	private float taskLength;
	
	/** Weight for original position in the hierarchy */
	private float position;

	/**
	 * @return the criticalTime
	 */
	public float getCriticalTime() {
		return criticalTime;
	}

	/**
	 * @param criticalTime the criticalTime to set
	 */
	public void setCriticalTime(float criticalTime) {
		this.criticalTime = criticalTime;
	}

	/**
	 * @return the uncertainty
	 */
	public float getUncertainty() {
		return uncertainty;
	}

	/**
	 * @param uncertainty the uncertainty to set
	 */
	public void setUncertainty(float uncertainty) {
		this.uncertainty = uncertainty;
	}

	/**
	 * @return the relativeUncertainty
	 */
	public float getRelativeUncertainty() {
		return relativeUncertainty;
	}

	/**
	 * @param relativeUncertainty the relativeUncertainty to set
	 */
	public void setRelativeUncertainty(float relativeUncertainty) {
		this.relativeUncertainty = relativeUncertainty;
	}

	/**
	 * @return the priority
	 */
	public float getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(float priority) {
		this.priority = priority;
	}

	/**
	 * @return the taskLength
	 */
	public float getTaskLength() {
		return taskLength;
	}

	/**
	 * @param taskLength the taskLength to set
	 */
	public void setTaskLength(float taskLength) {
		this.taskLength = taskLength;
	}

	/**
	 * @return the position
	 */
	public float getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(float position) {
		this.position = position;
	}
}