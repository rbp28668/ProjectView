/*
 * Resource.java
 * Created on 28-Apr-2005
 * By Bruce.Porteous
 *
 */
package uk.co.alvagem.projectview.model;

/**
 * Resource models a resource that can be used to fulfill a task.
 * @author Bruce.Porteous
 */
public class Resource  extends PersistentBase implements Persistent {

    
	/** name of this resource */
	private String name;
	
	/** cost per hour of this resource */
	private float cost;
	
	/** availability of this resource */
	private Calendar availability;
	/**
	 * 
	 */
	public Resource() {
		super();
	}



    /**
     * @return Returns the availability.
     */
    public Calendar getAvailability() {
        return availability;
    }
    /**
     * @param availability The availability to set.
     */
    public void setAvailability(Calendar availability) {
        this.availability = availability;
    }
    /**
     * @return Returns the cost.
     */
    public float getCost() {
        return cost;
    }
    /**
     * @param cost The cost to set.
     */
    public void setCost(float cost) {
        this.cost = cost;
    }
     /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
    	return getName() + " at cost " + getCost();
    }
}
