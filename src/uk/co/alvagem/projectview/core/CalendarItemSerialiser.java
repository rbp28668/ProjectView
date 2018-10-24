/**
 * 
 */
package uk.co.alvagem.projectview.core;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

import uk.co.alvagem.database.DAOFactory;
import uk.co.alvagem.projectview.dao.CalendarItemDAO;
import uk.co.alvagem.projectview.model.Calendar;
import uk.co.alvagem.projectview.model.CalendarItem;
import uk.co.alvagem.projectview.model.DayCalendarItem;
import uk.co.alvagem.projectview.model.DayOfWeekCalendarItem;
import uk.co.alvagem.util.IXMLContentHandler;
import uk.co.alvagem.util.InputException;
import uk.co.alvagem.util.XMLWriter;

/**
 * @author bruce.porteous
 *
 */
public class CalendarItemSerialiser extends FactoryBase implements
		IXMLContentHandler {

    public final static String ENTITY = "CalendarItem";
    private CalendarItem current = null;
    private Calendar currentCalendar = null;
    private PolySerialiser poly = null;
    
    private static Map<String,PolySerialiser> inputPolyMap = new HashMap<String,PolySerialiser>();
    private static Map<Class<? extends CalendarItem>,PolySerialiser> outputPolyMap = new HashMap<Class<? extends CalendarItem>,PolySerialiser>();
    
    static {
    	register(new DaySerialiser(), DayCalendarItem.class);
    	register(new DoWSerialiser(), DayOfWeekCalendarItem.class);
    }
    
    private static void register(PolySerialiser poly, Class<? extends CalendarItem> itemClass){
    	inputPolyMap.put(poly.getDiscriminator(), poly);
    	outputPolyMap.put(itemClass, poly);
    }
    
	/**
	 * @param factory
	 * @param cache 
	 */
	public CalendarItemSerialiser(DAOFactory factory, ObjectCache cache) {
		super(factory,cache);
	}
	
	/**
	 * Sets the parent Calendar that de-serialised items should be added to.
	 * @param calendar
	 */
	public void setCurrentCalendar(Calendar calendar){
		currentCalendar = calendar;
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String local, Attributes attrs)
			throws InputException {
    	if(local.equals(ENTITY)){
    		String oftype = attrs.getValue("oftype");
    		if(oftype == null){
    			throw new InputException("Missing oftype attribute in CalendarItem");
    		}
    		poly = inputPolyMap.get(oftype);
    		if(poly == null){
    			throw new InputException("Unknown type of CalendarItem: " + oftype);
    		}
    		
    	    String uid = getUid(attrs);
    	    CalendarItemDAO dao = getFactory().getCalendarItemDAO();
    	    current = dao.getByUid(uid);
    	    if(current == null){
    	        current = poly.create();
    	        current.setUid(uid);
    	    }
    	    getAttributes(current,attrs);
    	    getCache().add(current);
    	} 
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String local) throws InputException {
    	if(local.equals(ENTITY)){
	        currentCalendar.getItems().remove(current);
	        currentCalendar.getItems().add(current);
    		current = null;
    		poly = null;
    	} else if(local.equals("DayFraction")) {
    		current.setDayFraction(Float.parseFloat(getText()));
		} else {
    		poly.processElement(local, getText(), current);
    	}
	}

    public void writeXML(XMLWriter out, CalendarItem item) throws IOException{
        out.startEntity(ENTITY);
        PolySerialiser poly = outputPolyMap.get(item.getClass());
        if(poly == null) {
        	throw new IllegalArgumentException("No polymorphic serialiser for " + item.getClass().getName());
        }
        
        addPersistentAttributes(out,item);
        out.addAttribute("oftype",poly.getDiscriminator());
        
        out.textEntity("DayFraction", Float.toString(item.getDayFraction()));
        poly.writeXML(out,item);
        out.stopEntity();
    }

    private abstract static class PolySerialiser {
    	
    	abstract String getDiscriminator();
    	abstract CalendarItem create();
    	abstract void writeXML(XMLWriter out, CalendarItem item) throws IOException;
    	abstract void processElement(String local, String text, CalendarItem item) throws InputException;
    }
    
    private static class DaySerialiser extends PolySerialiser{

    	private static final DateFormat FMT = new SimpleDateFormat("yyyyMMdd");
		/* (non-Javadoc)
		 * @see uk.co.alvagem.projectview.core.CalendarItemSerialiser.PolySerialiser#getDiscriminator()
		 */
		@Override
		String getDiscriminator() {
			return("day");
		}

		/* (non-Javadoc)
		 * @see uk.co.alvagem.projectview.core.CalendarItemSerialiser.PolySerialiser#create()
		 */
		@Override
		CalendarItem create() {
			return new DayCalendarItem();
		}

		/* (non-Javadoc)
		 * @see uk.co.alvagem.projectview.core.CalendarItemSerialiser.PolySerialiser#writeXML(uk.co.alvagem.util.XMLWriter, uk.co.alvagem.projectview.model.CalendarItem)
		 */
		@Override
		void writeXML(XMLWriter out, CalendarItem item) throws IOException {
			DayCalendarItem dci = (DayCalendarItem)item;
			out.textEntity("Day", FMT.format(dci.getDay()));
		}

		/* (non-Javadoc)
		 * @see uk.co.alvagem.projectview.core.CalendarItemSerialiser.PolySerialiser#processElement(java.lang.String)
		 */
		@Override
		void processElement(String local, String text, CalendarItem item) throws InputException{
			if(local.equals("Day")){
				try {
					Date day = FMT.parse(text);
					DayCalendarItem dci = (DayCalendarItem)item;
					dci.setDay(day);
				} catch (ParseException e) {
					throw new InputException("Invalid date for DayCalendarItem");
				}
			}
		}
    	
    }
    
    private static class DoWSerialiser extends PolySerialiser{

		/* (non-Javadoc)
		 * @see uk.co.alvagem.projectview.core.CalendarItemSerialiser.PolySerialiser#getDiscriminator()
		 */
		@Override
		String getDiscriminator() {
			return "dow";
		}

		/* (non-Javadoc)
		 * @see uk.co.alvagem.projectview.core.CalendarItemSerialiser.PolySerialiser#create()
		 */
		@Override
		CalendarItem create() {
			return new DayOfWeekCalendarItem();
		}

		/* (non-Javadoc)
		 * @see uk.co.alvagem.projectview.core.CalendarItemSerialiser.PolySerialiser#writeXML(uk.co.alvagem.util.XMLWriter, uk.co.alvagem.projectview.model.CalendarItem)
		 */
		@Override
		void writeXML(XMLWriter out, CalendarItem item) throws IOException {
			DayOfWeekCalendarItem dowci = (DayOfWeekCalendarItem)item;
			out.textEntity("DayOfWeek", Integer.toString(dowci.getWhichDay()));
		}

		/* (non-Javadoc)
		 * @see uk.co.alvagem.projectview.core.CalendarItemSerialiser.PolySerialiser#processElement(java.lang.String)
		 */
		@Override
		void processElement(String local, String text,CalendarItem item) throws InputException {
			if(local.equals("DayOfWeek")){
				DayOfWeekCalendarItem dowci = (DayOfWeekCalendarItem)item;
				int dayOfWeek = Integer.parseInt(text);
				dowci.setWhichDay(dayOfWeek);
			}
		}
    	
    }
}
