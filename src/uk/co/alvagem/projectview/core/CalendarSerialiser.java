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
import uk.co.alvagem.projectview.model.Calendar;
import uk.co.alvagem.projectview.model.CalendarItem;
import uk.co.alvagem.util.IXMLContentHandler;
import uk.co.alvagem.util.InputException;
import uk.co.alvagem.util.XMLWriter;

public class CalendarSerialiser extends FactoryBase implements IXMLContentHandler {

    public final static String ENTITY = "Calendar";
    private CalendarItemSerialiser cis = null;
    private Calendar current = null;

    public CalendarSerialiser(DAOFactory factory, ObjectCache cache){
    	super(factory,cache);
    }
    

    public void endElement(String uri, String local) throws InputException {
    	if(local.equals(ENTITY)){
    	    CalendarDAO dao = getFactory().getCalendarDAO();
	        dao.makePersistent(current);
    		cis.setCurrentCalendar(null);
    		current = null;
    	}

    }

    public void startElement(String uri, String local, Attributes attrs)
            throws InputException {
    	if(local.equals(ENTITY)){
    	    String uid = getUid(attrs);
    	    CalendarDAO dao = getFactory().getCalendarDAO();
    	    current = dao.getByUid(uid);
    	    if(current == null){
    	        current = new Calendar();
    	        current.setUid(uid);
    	    }
    	    getAttributes(current,attrs);

    	    current.setName(attrs.getValue("name"));
    	    
    	    String parentUid = attrs.getValue("parent");
    	    if(parentUid != null){
    	    	Calendar parent = getCache().getCalendar(parentUid);
    	    	if(parent == null){
    	    		throw new InputException("Can't find parent calendar with uid: "+ parentUid);
    	    	}
    	    	current.setParent(parent);
    	    }
    	    
    	    String wdText = attrs.getValue("workingDay");
    	    if(wdText != null){
    	    	current.setWorkingDayLength(Float.parseFloat(wdText));
    	    }
    	    
    	    cis.setCurrentCalendar(current);
    	    getCache().add(current);

    	}

    }

    public void writeXML(XMLWriter out, Calendar calendar) throws IOException{
        out.startEntity(ENTITY);
        addPersistentAttributes(out,calendar);
        out.addAttribute("name",calendar.getName());
        if(calendar.getParent() != null) {
        	out.addAttribute("parent", calendar.getParent().getUid());
        }
        
        out.addAttribute("workingDay", Float.toString(calendar.getWorkingDayLength()));
        
        CalendarItemSerialiser cis = new CalendarItemSerialiser(getFactory(),getCache());
        for(CalendarItem item : calendar.getItems()){
        	cis.writeXML(out,item);
        }
        out.stopEntity();
    }


	/**
	 * Sets the item serialiser so we can set the item's parent calendar.
	 * @param cis
	 */
	void setCis(CalendarItemSerialiser cis) {
		this.cis = cis;
	}
    
}
