/**
 * 
 */
package uk.co.alvagem.projectview.core;

import java.io.IOException;

import org.xml.sax.Attributes;

import uk.co.alvagem.database.DAOFactory;
import uk.co.alvagem.projectview.dao.TaskDAO;
import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.projectview.model.TaskHistory;
import uk.co.alvagem.util.IXMLContentHandler;
import uk.co.alvagem.util.InputException;
import uk.co.alvagem.util.XMLWriter;

/**
 * @author bruce.porteous
 *
 */
public class TaskSerialiser extends FactoryBase implements IXMLContentHandler {

    public final static String ENTITY = "Task";
    private TaskHistorySerialiser ths = null;
    
    private Task currentTask = null;
    
    
	/**
	 * @param cache 
	 * 
	 */
	public TaskSerialiser(DAOFactory factory, ObjectCache cache) {
		super(factory, cache);
	}
		

	/**
     * @param ths the ths to set
     */
    public void setThs(TaskHistorySerialiser ths) {
        this.ths = ths;
    }

    /* (non-Javadoc)
	 * @see uk.co.alvagem.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String local, Attributes attrs)
			throws InputException {
	    if(local.equals(ENTITY)){
	    	assert(ths != null); // Must have been set up before use.

	        String uid = getUid(attrs);
	        TaskDAO taskDAO = getFactory().getTaskDAO();
            currentTask = taskDAO.getByUid(uid);
            if(currentTask == null){
	            currentTask = new Task();
	            currentTask.setUid(uid);
	        } 
	        	
            getAttributes(currentTask,attrs);

            String name = attrs.getValue("name");
	        currentTask.setName(name);
	        
	        String parentStr = attrs.getValue("parent");
	        if(parentStr != null){
	            Task parent = getCache().getTask(parentStr);//taskDAO.getByUid(parentStr);
	            if(parent == null){
	            	throw new InputException("Can't find parent task with uid " + parentStr);
	            }
	            if(!parent.getSubTasks().contains(currentTask)){
	            	parent.getSubTasks().add(currentTask);
	            }
	            currentTask.setParent(parent);
	        }
	        
	        ths.setCurrentTask(currentTask);
	        
            getCache().add(currentTask);
            
	    }
	    
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String local) throws InputException {
        if(local.equals(ENTITY)){
	        TaskDAO taskDAO = getFactory().getTaskDAO();
            taskDAO.makePersistent(currentTask);
            currentTask = null;
            ths.setCurrentTask(null);
        } else if (local.equals("Description")){
            currentTask.setDescription(getText());
        } else if (local.equals("Notes")){
            currentTask.setNotes(getText());
        } else if (local.equals("WorkPackage")){
            currentTask.setWorkPackage(Long.parseLong(getText()));
        } else if (local.equals("Priority")){
            currentTask.setPriority(Integer.parseInt(getText()));
        } else {
        	// hand off to current state of task.
        	ths.characters(getText());
        	ths.setCurrentHistory(currentTask.getCurrent());
        	ths.endElement(uri, local);
        	ths.setCurrentHistory(null);
        }

	}


	public void writeXML(XMLWriter out, Task task) throws IOException{
		out.startEntity(ENTITY);
        addPersistentAttributes(out, task);
		out.addAttribute("name",task.getName());
		
		Task parent = task.getParent();
		if(parent != null){
			out.addAttribute("parent",parent.getUid());
		}
		
		out.textEntity("Description",task.getDescription());
		out.textEntity("Notes",task.getNotes());
		out.textEntity("WorkPackage",Long.toString(task.getWorkPackage()));
		out.textEntity("Priority", Integer.toString(task.getPriority()));
	
		TaskHistorySerialiser ths = new TaskHistorySerialiser(getFactory(),null);
		
		ths.writeCoreXML(out, task.getCurrent());
		
		for(TaskHistory th : task.getHistory()){
			ths.writeXML(out,th);
		}
		out.stopEntity();
	}
}
