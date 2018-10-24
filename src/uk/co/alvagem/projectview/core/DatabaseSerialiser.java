/*
 * DatabaseSerialiser.java
 * Project: ProjectView
 * Created on 4 Jan 2008
 *
 */
package uk.co.alvagem.projectview.core;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uk.co.alvagem.database.DAOFactory;
import uk.co.alvagem.projectview.dao.AllocationDAO;
import uk.co.alvagem.projectview.dao.CalendarDAO;
import uk.co.alvagem.projectview.dao.DependencyDAO;
import uk.co.alvagem.projectview.dao.ResourceDAO;
import uk.co.alvagem.projectview.dao.TaskDAO;
import uk.co.alvagem.projectview.model.Allocation;
import uk.co.alvagem.projectview.model.Calendar;
import uk.co.alvagem.projectview.model.Dependency;
import uk.co.alvagem.projectview.model.Resource;
import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.util.IXMLContentHandler;
import uk.co.alvagem.util.XMLWriter;

public class DatabaseSerialiser {

    private DAOFactory factory;
    private XMLWriter out;
    
    public void writeDB(XMLWriter out) throws Exception {
        
        this.out = out;
        this.factory = DAOFactory.instance(DAOFactory.HIBERNATE);
        
        try {
            factory.beginTransaction();
            try {
                writeCalendars();
                writeResources();
                writeTasks();
                writeDependencies();
                writeAllocations();
                factory.commit();
            } catch (Exception e) {
                factory.rollback();
                throw e;
            }
            
        } finally {
            this.factory = null;
            this.out = null;
        }
    }
    
    public Map<String,IXMLContentHandler> getHandlers(){
        DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
        ObjectCache cache = new ObjectCache();
        
        Map<String,IXMLContentHandler> handlers = new HashMap<String,IXMLContentHandler>();
        
        handlers.put(AllocationSerialiser.ENTITY, new AllocationSerialiser(factory,cache));

        CalendarSerialiser cs = new CalendarSerialiser(factory,cache);
        handlers.put(CalendarSerialiser.ENTITY, cs);
        CalendarItemSerialiser cis = new CalendarItemSerialiser(factory,cache);
        handlers.put(CalendarItemSerialiser.ENTITY, cis);
        cs.setCis(cis);
        
        handlers.put(DependencySerialiser.ENTITY, new DependencySerialiser(factory,cache));
        handlers.put(ResourceSerialiser.ENTITY, new ResourceSerialiser(factory,cache));
        
        TaskSerialiser ts = new TaskSerialiser(factory,cache);
        handlers.put(TaskSerialiser.ENTITY, ts);
        TaskHistorySerialiser ths = new TaskHistorySerialiser(factory,cache);
        handlers.put(TaskHistorySerialiser.ENTITY, ths);
        ts.setThs(ths);
        
        
        return handlers;
    }
    
    private void writeTasks() throws IOException{
        
        TaskDAO dao = factory.getTaskDAO();
        TaskSerialiser taskSerialiser = new TaskSerialiser(factory, null);
        List<Task> tasks = dao.findTopLevelTasks();
        
        //Set<Task>serialised = new HashSet<Task>();
        for(Task task: tasks){
            writeTask(task,dao, taskSerialiser);
        }
        
        taskSerialiser = null;
    }

    private void writeTask(Task task,TaskDAO dao,TaskSerialiser taskSerialiser) throws IOException {
       dao.makePersistent(task);
       taskSerialiser.writeXML(out, task);
        for(Task sub : task.getSubTasks()){
             writeTask(sub,dao, taskSerialiser);
        }
    }

    private void writeResources() throws IOException{
        ResourceDAO dao = factory.getResourceDAO();
        List<Resource> resources = dao.findAll();
        ResourceSerialiser serialiser = new ResourceSerialiser(factory, null);
        for(Resource resource : resources){
            dao.makePersistent(resource);
            serialiser.writeXML(out, resource);
        }
    }
    
    /**
     * Write the calendars.  Note that these are sorted by parent relationship
     * so that any parents are written first.  In the sort, if there is no 
     * parent child relationship then they are considered equal, otherwise the
     * parent calendar is "less" than the child calendar.  This ensures that
     * on reading back, the parent calendars are always read before the child
     * calendars.
     * parent, child -> 1
     * child, parent -> -1
     * @throws IOException
     */
    private void writeCalendars() throws IOException{
        CalendarDAO dao = factory.getCalendarDAO();
        List<Calendar> calendars = new LinkedList<Calendar>();
        calendars.addAll(dao.findAll());
        Collections.sort(calendars, new Comparator<Calendar>() {

			public int compare(Calendar arg0, Calendar arg1) {
				Calendar parent = arg1.getParent();
				if(arg0.equals(parent)){
					return -1;
				}
				parent = arg0.getParent();
				if(arg1.equals(parent)){
					return 1;
				}
				return 0;
			}
        	
        });
        CalendarSerialiser serialiser = new CalendarSerialiser(factory,null);
        for(Calendar calendar : calendars){
            dao.makePersistent(calendar);
            serialiser.writeXML(out, calendar);
        }
    }
    
    private void writeAllocations() throws IOException {
        AllocationDAO dao = factory.getAllocationDAO();
        List<Allocation> allocations = dao.findAll();
        AllocationSerialiser serialiser = new AllocationSerialiser(factory,null);
        for(Allocation allocation : allocations){
            dao.makePersistent(allocation);
            serialiser.writeXML(out, allocation);
        }
        
    }
    
    private void writeDependencies() throws IOException {
        DependencyDAO dao = factory.getDependencyDAO();
        List<Dependency> dependencies = dao.findAll();
        DependencySerialiser serialiser = new DependencySerialiser(factory,null);
        for(Dependency dependency : dependencies){
            dao.makePersistent(dependency);
            serialiser.writeXML(out, dependency);
        }
        
    }

    
}
