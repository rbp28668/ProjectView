/*
 * ProjectRole.java
 * Created on 28-Apr-2005
 * By Bruce.Porteous
 *
 */
package uk.co.alvagem.projectview.model;


/**
 * ProjectRole
 * @author Bruce.Porteous
 */
public class ProjectRole extends TaskRole {

 
    private String projectCode;
    
    private String projectURL;
    
    private WorkingDay workingDay = new WorkingDay();
    
    private Calendar defaultCalendar;
    
	/**
	 * 
	 */
	public ProjectRole() {
		super();
		workingDay.setHoursPerDay(WorkingDay.DEFAULT.getHoursPerDay());
	}


    /**
     * @return the projectCode
     */
    public String getProjectCode() {
        return projectCode;
    }


    /**
     * @param projectCode the projectCode to set
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }


    /**
     * @return the projectURL
     */
    public String getProjectURL() {
        return projectURL;
    }


    /**
     * @param projectURL the projectURL to set
     */
    public void setProjectURL(String projectURL) {
        this.projectURL = projectURL;
    }


}
