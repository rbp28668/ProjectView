/*
 * TaskHistory.java
 * Project: ProjectView
 * Created on 27 Dec 2007
 *
 */
package uk.co.alvagem.projectview.model;

import java.util.Date;

/**
 * TaskHistory
 * @author Bruce.Porteous
  *
 */
public class TaskHistory  extends PersistentBase implements Persistent {

	/** At which point in time was this history item created */
    private long timePoint;
	
    /** Fraction of task completed (0..1) */
    private float fractionComplete;
    
    /** Estimated effort needed for whole task in man-hours */
    private float estimatedEffort;
    
    /** Measure of uncertainty of estimate */
    private float estimateSpread;
    
    /** Type of uncertainty of estimate */
    private UncertaintyType uncertaintyType;

    /** Extra parameter for defining the shape of the uncertainty probability distribution */
    private float alpha;
    
    /** Extra parameter for defining the shape of the uncertainty probability distribution */
    private float beta;
    
    /** Elapsed time over the task */
    private float elapsedTime;
    
    /** If true task duration (elapsed time) is driven from effort and resource */
    private boolean effortDriven;
    
    /** Actual work done on this task in man-hours */
    private float actualWork;
    
    /** current best guess as to start date */
    private long startDate;

    /** current best guess as to finish date */
    private long finishDate;
    
    /** Whether the task is active (i.e. not deleted ). Inactive tasks are retained for their history   */
    private boolean active;
    
    /** Current state of the task for workflow / Kanban etc */
    private TaskStatus status;
    
    public TaskHistory(){
        Date now = new Date();
        timePoint = now.getTime();
        fractionComplete = 0;
        estimatedEffort = WorkingDay.DEFAULT.getHoursPerDay();  // one working day.
        estimateSpread = 0;
        uncertaintyType = UncertaintyType.LINEAR; // Simple - defaults to none if spread is zero.
        alpha = 0;
        beta = 0;
        elapsedTime = 0;
        effortDriven = true;
        actualWork = 0;
        startDate = now.getTime();
        finishDate = now.getTime();
        active = true;
        status = TaskStatus.NEW;
    }
    
    
    /**
	 * @return the timePoint
	 */
	public Date getTimePoint() {
		return new Date(timePoint);
	}


	/**
	 * @param timePoint the timePoint to set
	 */
	public void setTimePoint(Date timePoint) {
		this.timePoint = timePoint.getTime();
	}


	/**
     * @return Returns the actualWork.
     */
    public float getActualWork() {
        return actualWork;
    }
    /**
     * @param actualWork The actualWork to set.
     */
    public void setActualWork(float actualWork) {
        this.actualWork = actualWork;
    }

    /**
     * @return Returns the estimated effort for this task. Note - returned
     * in man hours.
     */
    public float getEstimatedEffort() {
        return estimatedEffort;
    }
    /**
     * @param estimatedEffort The estimatedEffort to set.
     */
    public void setEstimatedEffort(float estimatedEffort) {
        this.estimatedEffort = estimatedEffort;
    }
    
    /**
     * @return Returns the estimateSpread.
     */
    public float getEstimateSpread() {
        return estimateSpread;
    }
    /**
     * @param estimateSpread The estimateSpread to set.
     */
    public void setEstimateSpread(float estimateSpread) {
        this.estimateSpread = estimateSpread;
    }
    /**
     * @return Returns the fractionComplete.
     */
    public float getFractionComplete() {
        return fractionComplete;
    }
    /**
     * @param fractionComplete The fractionComplete to set.
     */
    public void setFractionComplete(float fractionComplete) {
        this.fractionComplete = fractionComplete;
    }
    
    /**
     * @return Returns the uncertaintyType.
     */
    public UncertaintyType getUncertaintyType() {
        return uncertaintyType;
    }
    
    /**
     * @param uncertaintyType The uncertaintyType to set.
     */
    public void setUncertaintyType(UncertaintyType uncertaintyType) {
        this.uncertaintyType = uncertaintyType;
    }
    
    /**
     * Gets the name of the uncertainty type.  Provided primarily for 
     * persistance as it allows it to be set by string.
     * @return Returns the uncertaintyType.
     */
    public String getUncertaintyTypeName() {
        return uncertaintyType.toString();
    }

    /**
     * Sets the uncertainty type by specifying the name of the type.
     * @param name is the name of the uncertainty type to set.
     */
    public void setUncertaintyTypeName(String name) {
    	UncertaintyType type = UncertaintyType.lookup(name);
    	if(type == null){
    		throw new IllegalArgumentException("Unknown uncertainty type " + name);
    	}
        this.uncertaintyType = type;
    }
    
    
    
    /**
     * Get the first shape parameter for the uncertainty curve.  This allows the
     * curve shape to be parameterised.  Meaning is dependent on the uncertainty
     * type.
	 * @return the alpha
	 */
	public float getAlpha() {
		return alpha;
	}


	/**
	 * @param alpha the alpha to set
	 */
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}


    /**
     * Get the second shape parameter for the uncertainty curve.  This allows the
     * curve shape to be parameterised.  Meaning is dependent on the uncertainty
     * type.
	 * @return the beta
	 */
	public float getBeta() {
		return beta;
	}


	/**
	 * @param beta the beta to set
	 */
	public void setBeta(float beta) {
		this.beta = beta;
	}


	/**
     * @return the elapsedTime
     */
    public float getElapsedTime() {
        return elapsedTime;
    }


    /**
     * @param elapsedTime the elapsedTime to set
     */
    public void setElapsedTime(float elapsedTime) {
        this.elapsedTime = elapsedTime;
    }


    /**
     * @return the effortDriven
     */
    public boolean isEffortDriven() {
        return effortDriven;
    }


    /**
     * @param effortDriven the effortDriven to set
     */
    public void setEffortDriven(boolean effortDriven) {
        this.effortDriven = effortDriven;
    }


    /**
     * Gets the the start date.  Note - provides a defensive copy so you
     * must use setStartDate to set the start date.
     * @return the startDate
     */
    public Date getStartDate() {
        return new Date(startDate);
    }
    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate.getTime();
    }
    
    /**
     * Gets the finish date for the task.  Note defensive copy.
     * @return the finishDate
     */
    public Date getFinishDate() {
        return new Date(finishDate);
    }
    
    /**
     * @param finishDate the finishDate to set
     */
    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate.getTime();
    }
    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }
    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }


	/**
	 * @return the status
	 */
	public TaskStatus getStatus() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(TaskStatus status) {
		this.status = status;
	}
 
	/**
	 * Used to map the enum into a standard int field in the db.
	 * @return the status
	 */
	public int getIntStatus() {
		return status.ordinal();
	}

	/**
	 * @param status the status to set
	 */
	public void setIntStatus(Integer status) {
		if(status == null) {
			this.status = TaskStatus.NEW;
		} else {
			this.status = TaskStatus.values()[status];
		}
	}
    
}
