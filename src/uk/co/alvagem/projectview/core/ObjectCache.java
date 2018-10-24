/**
 * 
 */
package uk.co.alvagem.projectview.core;

import java.util.HashMap;
import java.util.Map;

import uk.co.alvagem.projectview.model.Calendar;
import uk.co.alvagem.projectview.model.Persistent;
import uk.co.alvagem.projectview.model.Resource;
import uk.co.alvagem.projectview.model.Task;

/**
 * @author bruce.porteous
 *
 */
public class ObjectCache {

	private Map<String, Persistent> objects = new HashMap<String, Persistent>();
	/**
	 * 
	 */
	public ObjectCache() {
		super();
	}
	
	public void clear(){
		objects.clear();
	}
	
	public void add(Persistent object){
		objects.put(object.getUid(), object);
	}
	
	public Persistent get(String uid){
		return objects.get(uid);
	}
	
	public Task getTask(String uid){
		Task task = (Task)get(uid);
		return task;
	}

	public Resource getResource(String uid){
		Resource resource = (Resource)get(uid);
		return resource;
	}
	
	public Calendar getCalendar(String uid){
		Calendar calendar = (Calendar)get(uid);
		return calendar;
	}
	
	
}
