/*
 * PersistentBase.java
 * Project: ProjectView
 * Created on 5 Jan 2008
 *
 */
package uk.co.alvagem.projectview.model;

import java.util.Date;

import uk.co.alvagem.util.UUID;

/**
 * PersistentBase provides fields for a persistent object.  Identity is provided by
 * equivalence of uid (as is hashCode).
 * 
 * @author rbp28668
 */
public abstract class PersistentBase implements Persistent {

    /** Persistent ID */
    private Integer id;
    
    /** version of this entity*/
    private int version;
    
    /** timestamp of last update */
    private Date timestamp;
    
    /** user creating last update */
    private String user;
    
    /** UUID */
    private UUID uid;
    
    /** ID used to identify data in external packages */
    private String externalID;

    public PersistentBase(){
        timestamp = new Date();
        this.uid = new UUID();
    }
    
    /* (non-Javadoc)
     * @see uk.co.alvagem.projectview.model.Persistent#getId()
     */
    public Integer getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.projectview.model.Persistent#setId(java.lang.Integer)
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.projectview.model.Persistent#getVersion()
     */
    public int getVersion() {
        return version;
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.projectview.model.Persistent#setVersion(int)
     */
    public void setVersion(int version) {
        this.version = version;
    }
    
    /* (non-Javadoc)
     * @see uk.co.alvagem.projectview.model.Persistent#getTimestamp()
     */
    public Date getTimestamp() {
    	if(timestamp == null){
    		timestamp = new Date();
    	}
        return timestamp;
    }
 
    /* (non-Javadoc)
     * @see uk.co.alvagem.projectview.model.Persistent#setTimestamp(java.util.Date)
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    
    /* (non-Javadoc)
     * @see uk.co.alvagem.projectview.model.Persistent#getExternalId()
     */
    public String getExternalId() {
        return externalID;
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.projectview.model.Persistent#getUID()
     */
    public String getUid() {
        return uid.toString();
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.projectview.model.Persistent#getUser()
     */
    public String getUser() {
        return user;
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.projectview.model.Persistent#setExternalID(java.lang.String)
     */
    public void setExternalId(String externalID) {
        this.externalID = externalID; 
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.projectview.model.Persistent#setUID(java.lang.String)
     */
    public void setUid(String uid) {
    	if(uid == null){
    		this.uid = new UUID();
    	} else {
    		this.uid = new UUID(uid);
    	}
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.projectview.model.Persistent#setUser(java.lang.String)
     */
    public void setUser(String user) {
        this.user = user;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode(){
        return this.uid.hashCode();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object other){
    	if(other == null){
    		return false;
    	}
    	
        if(!(other instanceof PersistentBase)){
            return false;
        }
        
        PersistentBase pbOther = (PersistentBase)other;
        return this.uid.equals(pbOther.uid);
    }
    
    public void copyTo(Persistent dest){
        dest.setId(getId());
        dest.setVersion(getVersion());
        dest.setTimestamp(getTimestamp());
        dest.setUid(getUid());
        dest.setExternalId(getExternalId());
        dest.setUser(getUser());
    }
}
