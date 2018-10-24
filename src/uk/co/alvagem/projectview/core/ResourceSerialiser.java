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
import uk.co.alvagem.projectview.dao.CalendarDAO;
import uk.co.alvagem.projectview.dao.ResourceDAO;
import uk.co.alvagem.projectview.model.Calendar;
import uk.co.alvagem.projectview.model.Resource;
import uk.co.alvagem.util.IXMLContentHandler;
import uk.co.alvagem.util.InputException;
import uk.co.alvagem.util.XMLWriter;

public class ResourceSerialiser extends FactoryBase implements IXMLContentHandler {


	public final static String ENTITY = "Resource";
	private Resource current = null;
	
    /**
	 * @param factory
     * @param cache 
	 */
	protected ResourceSerialiser(DAOFactory factory, ObjectCache cache) {
		super(factory,cache);
	}


    public void endElement(String uri, String local) throws InputException {
    	if(local.equals(ENTITY)){
    	    ResourceDAO dao = getFactory().getResourceDAO();
   	        dao.makePersistent(current);
    		current = null;
    		
    	} else if (local.equals("Cost")) {
    		current.setCost(Float.parseFloat(getText()));
    	} else if (local.equals("Availability")) {
    		String uid = getText();
    		Calendar calendar = getCache().getCalendar(uid);
    		if(calendar == null){
    			throw new InputException("Can't get resource calendar with uid: " + uid);
    		}
    		current.setAvailability(calendar);
    	}
   }

    public void startElement(String uri, String local, Attributes attrs)
            throws InputException {
    	if(local.equals(ENTITY)){
    	    String uid = getUid(attrs);
    	    ResourceDAO dao = getFactory().getResourceDAO();
    	    current = dao.getByUid(uid);
    	    if(current == null){
    	        current = new Resource();
    	        current.setUid(uid);
    	    }
    	    getAttributes(current,attrs);
    	    String name = attrs.getValue("name");
    	    current.setName(name);
    		
    	    getCache().add(current);
    	}
    }

    public void writeXML(XMLWriter out, Resource resource) throws IOException{
        out.startEntity(ENTITY);
        addPersistentAttributes(out,resource);
        out.addAttribute("name",resource.getName());
 
        out.textEntity("Cost", Float.toString(resource.getCost()));
        
        Calendar availability = resource.getAvailability();
        if(availability != null){
        	out.textEntity("Availability", availability.getUid());
        }
        out.stopEntity();
    }
    
}
