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
import uk.co.alvagem.projectview.model.Allocation;
import uk.co.alvagem.projectview.model.Resource;
import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.util.IXMLContentHandler;
import uk.co.alvagem.util.InputException;
import uk.co.alvagem.util.XMLWriter;

public class AllocationSerialiser extends FactoryBase implements IXMLContentHandler {

    public final static String ENTITY = "Allocation";
    private Allocation current = null;
    
    public AllocationSerialiser(DAOFactory factory, ObjectCache cache){
    	super(factory, cache);
    }
    

    public void endElement(String uri, String local) throws InputException {
    	if(local.equals(ENTITY)){
    	    AllocationDAO dao = getFactory().getAllocationDAO();
	        dao.makePersistent(current);
    		current = null;
    	} else if (local.equals("TaskRef")){
    		String uid = getText();
    		Task task = getCache().getTask(uid);
    		if(task == null){
    			throw new InputException("Allocation - can't get task: " + uid);
    		}
    		current.setTask(task);
    	} else if (local.equals("ResourceRef")){
    		String uid = getText();
    		Resource resource = getCache().getResource(uid);
    		if(resource == null){
    			throw new InputException("Allocation - can't get resource: " + uid);
    		}
    		current.setResource(resource);
    	} else if (local.equals("Utilisation")){
    		current.setUtilisation(Float.parseFloat(getText()));
    	}
    }

    public void startElement(String uri, String local, Attributes attrs)
            throws InputException {
    	if(local.equals(ENTITY)){
    	    String uid = getUid(attrs);
    	    AllocationDAO dao = getFactory().getAllocationDAO();
    	    current = dao.getByUid(uid);
    	    if(current == null){
    	        current = new Allocation();
    	        current.setUid(uid);
    	    }
    	    getAttributes(current,attrs);
    		getCache().add(current);
    	}
    }

    public void writeXML(XMLWriter out, Allocation allocation) throws IOException{
        out.startEntity(ENTITY);
        addPersistentAttributes(out,allocation);
        out.textEntity("TaskRef", allocation.getTask().getUid().toString());
        out.textEntity("ResourceRef", allocation.getResource().getUid().toString());
        out.textEntity("Utilisation", Float.toString(allocation.getUtilisation()));
        out.stopEntity();
    }
    
}
