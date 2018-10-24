/**
 * 
 */
package uk.co.alvagem.projectview.core;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.xml.sax.Attributes;

import uk.co.alvagem.database.DAOFactory;
import uk.co.alvagem.projectview.dao.TaskHistoryDAO;
import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.projectview.model.TaskHistory;
import uk.co.alvagem.projectview.model.UncertaintyType;
import uk.co.alvagem.util.IXMLContentHandler;
import uk.co.alvagem.util.InputException;
import uk.co.alvagem.util.XMLWriter;

/**
 * Serialises and de-serialises a TaskHistory to and from XML.
 * @author bruce.porteous
 *
 */
public class TaskHistorySerialiser extends FactoryBase implements IXMLContentHandler {

    public final static String ENTITY = "TaskHistory";
	private static final DateFormat FMT = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	private TaskHistory current = null;
	private Task currentTask = null;
	
	/**
	 * @param cache 
	 * 
	 */
	public TaskHistorySerialiser(DAOFactory factory, ObjectCache cache) {
		super(factory,cache);
	}

	/**
	 * Sets the current task so that history items can be attached.
	 * @param task
	 */
	void setCurrentTask(Task task){
	    currentTask = task;
	}
	
	void setCurrentHistory(TaskHistory history){
		current = history;
	}
	
	/* (non-Javadoc)
	 * @see uk.co.alvagem.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String local, Attributes attrs)
			throws InputException {
	    if(local.equals(ENTITY)){
	        String uid = getUid(attrs);
            TaskHistoryDAO dao = getFactory().getTaskHistoryDAO();
            current = dao.getByUid(uid);
            if(current == null){
                current = new TaskHistory();
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
	        currentTask.getHistory().remove(current);
	        currentTask.getHistory().add(current);
	        current = null;
	    } else if(local.equals("TimePoint")){
            try {
            	String text = getText();
                current.setTimePoint(FMT.parse(text));
            } catch (ParseException e) {
                throw new InputException("Unable to parse start date");
            }
	    } else if(local.equals("ActualWork")) {
	        current.setActualWork(Float.parseFloat(getText()));
	    } else if(local.equals("EstimatedEffort")){
	        current.setEstimatedEffort(Float.parseFloat(getText()));
	    } else if(local.equals("EffortSpread")){
            current.setEstimateSpread(Float.parseFloat(getText()));
	    } else if(local.equals("Alpha")){
            current.setAlpha(Float.parseFloat(getText()));
	    } else if(local.equals("Beta")){
            current.setBeta(Float.parseFloat(getText()));
	    } else if(local.equals("ElapsedTime")){
            current.setElapsedTime(Float.parseFloat(getText()));
	    } else if(local.equals("IsEffortDriven")){
            current.setEffortDriven(Boolean.parseBoolean(getText()));
	    } else if(local.equals("UncertaintyType")){
	        current.setUncertaintyType(UncertaintyType.lookup(getText()));
	    } else if(local.equals("FractionComplete")){
            current.setFractionComplete(Float.parseFloat(getText()));
	    } else if(local.equals("StartDate")){
            try {
                current.setStartDate(FMT.parse(getText()));
            } catch (ParseException e) {
                throw new InputException("Unable to parse start date");
            }
	    } else if(local.equals("FinishDate")){
	        try {
                current.setFinishDate(FMT.parse(getText()));
            } catch (ParseException e) {
                throw new InputException("Unable to parse finish date");
            }
	    }
	     

	}

	public void writeXML(XMLWriter out, TaskHistory history) throws IOException{
		out.startEntity(ENTITY);
		addPersistentAttributes(out,history);
		writeCoreXML(out, history);
		out.stopEntity();
	}

	/**
	 * @param out
	 * @param history
	 * @throws IOException
	 */
	public void writeCoreXML(XMLWriter out, TaskHistory history)
			throws IOException {
		out.textEntity("TimePoint", FMT.format(history.getTimePoint()));
		out.textEntity("ActualWork",Float.toString(history.getActualWork()));
		out.textEntity("EstimatedEffort",Float.toString(history.getEstimatedEffort()));
		out.textEntity("EffortSpread",Float.toString(history.getEstimateSpread()));
		out.textEntity("Alpha",Float.toString(history.getAlpha()));
		out.textEntity("Beta",Float.toString(history.getBeta()));
		out.textEntity("ElapsedTime", Float.toString(history.getElapsedTime()));
		out.textEntity("IsEffortDriven", Boolean.toString(history.isEffortDriven()));
		out.textEntity("UncertaintyType",history.getUncertaintyType().toString());
		out.textEntity("FractionComplete",Float.toString(history.getFractionComplete()));
		out.textEntity("StartDate",FMT.format(history.getStartDate()));
		out.textEntity("FinishDate",FMT.format(history.getFinishDate()));
	}

}
