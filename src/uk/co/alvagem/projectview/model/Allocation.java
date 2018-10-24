/*
 * Allocation.java
 * Created on 28-Apr-2005
 * By Bruce.Porteous
 *
 */
package uk.co.alvagem.projectview.model;

/**
 * Allocation describes the allocation of a resource to a task.
 * @author Bruce.Porteous
 * @hibernate.class table="Allocation"
 */
public class Allocation extends PersistentBase implements Persistent{

	/** Task to which this resource is allocated */
	private Task task;
	
	/** Resource that is allocated to the task */
	private Resource resource;
	
	/** Fraction [0..1] of resource allocated to this task.  Normally a resource
	 * will work the number of hours in a working day on an allocated task with an
	 * allocation of 1.0. This can reduce that e.g. 0.5 is half a working day.  
	 * It is possible to over-allocate- that implies overtime! 
	*/
	private float utilisation;
	
	/**
	 * 
	 */
	public Allocation() {
		super();
	}


    /**
     * @return Returns the fraction.
     */
    public float getUtilisation() {
        return utilisation;
    }
    /**
     * @param fraction The fraction to set.
     */
    public void setUtilisation(float fraction) {
        this.utilisation = fraction;
    }
 
    /**
     * @return Returns the resource.
     */
    public Resource getResource() {
        return resource;
    }
    /**
     * @param resource The resource to set.
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }
    /**
     * @return Returns the task.
     */
    public Task getTask() {
        return task;
    }
    /**
     * @param task The task to set.
     */
    public void setTask(Task task) {
        this.task = task;
    }
    
    public String toString(){
        return getResource().getName() + "[" + getUtilisation() + "]";
    }
}
