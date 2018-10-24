/*
 * ResourceSerialiser.java
 * Project: ProjectView
 * Created on 4 Jan 2008
 *
 */
package uk.co.alvagem.projectview.core;

import java.io.IOException;

import org.xml.sax.Attributes;

import uk.co.alvagem.database.DAOFactory;
import uk.co.alvagem.projectview.dao.AllocationDAO;
import uk.co.alvagem.projectview.dao.DependencyDAO;
import uk.co.alvagem.projectview.dao.TaskDAO;
import uk.co.alvagem.projectview.model.Allocation;
import uk.co.alvagem.projectview.model.Dependency;
import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.util.IXMLContentHandler;
import uk.co.alvagem.util.InputException;
import uk.co.alvagem.util.XMLWriter;

public class DependencySerialiser extends FactoryBase implements IXMLContentHandler {


	public final static String ENTITY = "Dependency";
	private Dependency current = null;
    /**
	 * @param factory
     * @param cache 
	 */
	protected DependencySerialiser(DAOFactory factory, ObjectCache cache) {
		super(factory,cache);
	}


    public void endElement(String uri, String local) throws InputException {
    	if(local.equals(ENTITY)){
    	    DependencyDAO dao = getFactory().getDependencyDAO();
   	    	dao.makePersistent(current);
    		current = null;
    	} else if( local.equals("Predecessor")){
    		String uid = getText();
    		Task task = getCache().getTask(uid);
    		if(task == null){
    			throw new InputException("Can't find predecessor task with uid: " + uid);
    		}
    		current.setPredecessor(task);
    		task.getSuccessors().add(current);
    	} else if( local.equals("Successor")){
    		String uid = getText();
    		Task task = getCache().getTask(uid);
    		if(task == null){
    			throw new InputException("Can't find successor task with uid: " + uid);
    		}
    		current.setSuccessor(task);
    		task.getPredecessors().add(current);
    		
    	} else if (local.equals("Lag")){
    		current.setLag(Float.parseFloat(getText()));
    	}

    }

    public void startElement(String uri, String local, Attributes attrs)
            throws InputException {
    	if(local.equals(ENTITY)){
    	    String uid = getUid(attrs);
    	    DependencyDAO dao = getFactory().getDependencyDAO();
    	    current = dao.getByUid(uid);
    	    if(current == null){
    	        current = new Dependency();
    	        current.setUid(uid);
    	    }
    	    getAttributes(current,attrs);
    	    getCache().add(current);

    	}
    }
    
 

    public void writeXML(XMLWriter out, Dependency dependency) throws IOException{
        out.startEntity(ENTITY);
        addPersistentAttributes(out,dependency);
        out.textEntity("Predecessor", dependency.getPredecessor().getUid());
        out.textEntity("Successor", dependency.getSuccessor().getUid());
        out.textEntity("Lag", Float.toString(dependency.getLag()));
        out.stopEntity();
    }
    
}
