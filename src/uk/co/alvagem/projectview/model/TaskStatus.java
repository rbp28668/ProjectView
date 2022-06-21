/**
 * 
 */
package uk.co.alvagem.projectview.model;

/**
 * @author bruce_porteous
 *
 */
public enum TaskStatus {
	NEW("new","Initial state before work starts"),
	TODO("to do","Scheduled to start work"),
	ON_HOLD("on hold","Temporary stay of progress"),
	IN_PROGRESS("in progress","Task is actively being worked on"),
	COMPLETE("complete","Task is completed and no further work is needed"),
	REJECTED("rejected","Task will not be worked on");
	
	//"ToDo","On Hold", "In Progress","Done"
	private final String name;
	private final String description;
	
	TaskStatus(String name, String description) {
		this.name = name;
		this.description = description;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	
}
