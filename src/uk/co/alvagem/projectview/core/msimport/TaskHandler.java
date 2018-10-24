/**
 * 
 */
package uk.co.alvagem.projectview.core.msimport;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import uk.co.alvagem.projectview.dao.TaskDAO;
import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.projectview.model.WorkingDay;

/**
 * http://msdn.microsoft.com/en-us/library/aa679870(office.11).aspx
 * 
 * @author bruce.porteous
 *
 */
class TaskHandler extends Handler{

	private String projectName="unknown";
	private TaskDAO taskDAO;
	
	/** Map of proxies keyed by string outline number */
	private Map<String,TaskProxy> proxies = new HashMap<String,TaskProxy>();
	
	private final static String ROOT = "ROOT";
	
	/**
	 * @param msimport
	 */
	public TaskHandler(MSProjectXMLImport msimport) {
		super(msimport);
		taskDAO = msimport.getFactory().getTaskDAO();
		
		
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.projectview.core.msimport.Handler#process(uk.co.alvagem.projectview.core.msimport.Node)
	 */
	@Override
	public void process(Node node) throws Exception{
		
		// Set up the root (i.e. project) task first time through
		if(!proxies.containsKey(ROOT)){
			Task task = new Task();
			String name = getImport().getValueAt("/Name");
			task.setName(name);
			
			TaskProxy proxy = new TaskProxy();
			proxy.task = task;
			proxy.uid = ROOT;
			
			proxies.put(ROOT,proxy);
		}
		
		String uid = getChildText(node,"UID");
		String name = getChildText(node,"Name");
		String priority = getChildText(node,"Priority");
		Date start = getChildDate(node, "Start");
		Date finish = getChildDate(node, "Finish");
		String wbs = getChildText(node,"WBS");
		int pctComplete = getChildInt(node, "PercentComplete");
		long work = getChildDuration(node, "Work");
		String notes = getChildText(node,"Notes");
		
		
		String key = projectName + ":" + uid;
		Task task = taskDAO.findByExternalKey(key);
		
		if(task == null){
			task = new Task();
			task.setExternalId(key);
		}
		
		
		task.setName(name);
		task.setPriority(Integer.parseInt(priority));
		task.setStartDate(start);
		task.setFinishDate(finish);
		task.setFractionComplete((float)pctComplete / 100.0f);
		task.setEstimatedEffort((float)work / (1000 * 3600 * WorkingDay.DEFAULT.getHoursPerDay()));
		task.setNotes(notes);
		
		// TODO - predecessors.
		
		List<Node> predecessors = node.getChildren("PredecessorLink");
		for(Node predecessor : predecessors){
			String predecessorUID = getChildText(predecessor, "PredecessorUID");
			int type = getChildInt(predecessor,"Type");  // 0=FF, 1=FS, 2=SF and 3=SS
			int linkLag = getChildInt(predecessor,"LinkLag");  // in multiples of 6 seconds
			int lagFormat = getChildInt(predecessor,"LagFormat");
			
		}
		
		// Figure out where the task goes in the hierarchy using the outline numbers
		// Will set parentProxy and index being the index of the child in the parent's children.
		String outlineNumber = getChildText(node,"OutlineNumber");
		String parentOutline = ROOT;
		int index = 0;
		int idx = outlineNumber.lastIndexOf('.');
		if(idx >= 0){
			parentOutline = outlineNumber.substring(0,idx);
			String indexString = outlineNumber.substring(idx + 1, outlineNumber.length());
			index = Integer.parseInt(indexString);
		} else {
			index = Integer.parseInt(outlineNumber);
		}
		TaskProxy parentProxy = proxies.get(parentOutline);
		
		TaskProxy proxy = new TaskProxy();
		proxy.task = task;
		proxy.uid = uid;
		
		proxies.put(outlineNumber,proxy);
		parentProxy.children.add(index,proxy);
	}


	private static class TaskProxy{
		Task task;
		String uid;
		Vector<TaskProxy> children = new Vector<TaskProxy>();
	}


	
}
