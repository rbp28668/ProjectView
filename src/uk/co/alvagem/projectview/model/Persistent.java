/*
 * Persistent.java
 * Project: ProjectView
 * Created on 5 Jan 2008
 *
 */
package uk.co.alvagem.projectview.model;

import java.util.Date;

public interface Persistent {

    /**
     * Gets the ID used for persistence.
     * @return Returns the id.
     */
    public Integer getId();
    
    /** Sets the ID used for persistence.
     * @param id The id to set.
     */
    void setId(Integer id);

    /**
     * Gets the version number used for optimistic locking.
     * @return Returns the version.
      */
    public int getVersion();
    
    /**
     * Sets the version number used for optimistic locking.
     * @param version The version to set.
     */
    public void setVersion(int version);
    
    /**
     * Gets the timestamp for when this was last modified.
     * @return Returns the timestamp.
     */
    public Date getTimestamp();

    /**
     * Sets the last modification timestamp.
     * @param timestamp The timestamp to set.
     */
    public void setTimestamp(Date timestamp);
    
    
    /**
     * Gets the user who created or last updated this record.
     * @return the user.
     */
    public String getUser();
    
     /**
     * Sets the user who created or last updated this record.
     * @param user
     */
    public void setUser(String user);
    
    /**
     * Gets a unique identified (UUID) for this object.  This will
     * remain constant irrespective of any exports/imports which
     * may change the database ID. Note - needs 32 chars to store.
     * @return
     */
    public String getUid();
    
    
    /**
     * Sets a universally unique, unchanging, ID for this object. 
     * @param uid
     */
    public void setUid(String uid);
    
    /**
     * Gets the external ID that identifies this object in an external package.
     * @return the external ID.
     */
    public String getExternalId();
    
    /**
     * Sets an external ID that identifies this object in an external package.
     * This should be set during an import so that subsequent imports can
     * update the appropriate objects.
     * @param externalID is a string representation of the external ID.
     */
    public void setExternalId(String externalId);

}
