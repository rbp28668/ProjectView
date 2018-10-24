/*
 * Dependency.java
 * Project: ProjectView
 * Created on 27 Dec 2007
 *
 */
package uk.co.alvagem.projectview.model;

public class Dependency extends PersistentBase implements Persistent {

    
    private Task predecessor;
    private Task successor;
    private float lag;
    
    public Dependency(){
        super();
    }
    
    

    /**
     * @return the predecessor
     */
    public Task getPredecessor() {
        return predecessor;
    }
    /**
     * @param predecessor the predecessor to set
     */
    public void setPredecessor(Task predecessor) {
        this.predecessor = predecessor;
    }
    /**
     * @return the successor
     */
    public Task getSuccessor() {
        return successor;
    }
    /**
     * @param successor the successor to set
     */
    public void setSuccessor(Task successor) {
        this.successor = successor;
    }
    /**
     * @return the lag
     */
    public float getLag() {
        return lag;
    }
    /**
     * @param lag the lag to set
     */
    public void setLag(float lag) {
        this.lag = lag;
    }
    
    public String toString(){
        return predecessor.getName() + " -> " + successor.getName();
    }
    
}
