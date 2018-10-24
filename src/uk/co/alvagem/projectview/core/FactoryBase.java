/*
 * FactoryBase.java
 * Project: ProjectView
 * Created on 7 Jan 2008
 *
 */
package uk.co.alvagem.projectview.core;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.xml.sax.Attributes;

import uk.co.alvagem.database.DAOFactory;
import uk.co.alvagem.projectview.model.Persistent;
import uk.co.alvagem.util.InputException;
import uk.co.alvagem.util.XMLWriter;

public class FactoryBase {

    private static final DateFormat FMT = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private DAOFactory factory;
    private ObjectCache cache;
    private String text;

    protected FactoryBase(DAOFactory factory, ObjectCache cache){
    	this.factory = factory;
    	this.cache = cache;
    }
    
    /**
     * Gets the text in the entity. Note- one shot as text is set empty 
     * after reading.
     * @return
     */
    protected String getText(){
    	String value = text;
    	text = "";
    	return value;
    }
    
    protected DAOFactory getFactory(){
    	return factory;
    }
    
    protected ObjectCache getCache(){
    	return cache;
    }
    
    public void characters(String str) throws InputException {
    	text = str;
    }

    protected void addPersistentAttributes(XMLWriter out, Persistent p) throws IOException{
        out.addAttribute("id",p.getId());
        out.addAttribute("version",p.getVersion());
        out.addAttribute("timestamp",FMT.format(p.getTimestamp()));
        out.addAttribute("uid", p.getUid());
        String eid = p.getExternalId();
        if(eid != null){
            out.addAttribute("eid", eid);
        }
        
        String user = p.getUser();
        if(user != null){
            out.addAttribute("user", user);
        }
    }

    protected String getUid(Attributes attrs){
        return attrs.getValue("uid");
    }
    
    protected void getAttributes(Persistent p, Attributes attrs)
    throws InputException {
        int id = Integer.parseInt(attrs.getValue("id"));
        int version = Integer.parseInt(attrs.getValue("version"));
        String timestamp = attrs.getValue("timestamp");
        String eid = attrs.getValue("eid");
        String user = attrs.getValue("user");
        
        try {
            p.setTimestamp(FMT.parse(timestamp));
        } catch (ParseException e) {
            throw new InputException("Invalid format of timestamp");
        }
        
        p.setExternalId(eid);
        p.setUser(user);
    }

}
