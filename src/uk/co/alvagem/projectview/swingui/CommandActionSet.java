/*
 * CommandActionSet.java
 * Project: ProjectView
 * Created on 29 Dec 2007
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import uk.co.alvagem.database.DAOFactory;
import uk.co.alvagem.database.HibernateConfigurator;
import uk.co.alvagem.database.HibernateUtil;
import uk.co.alvagem.projectview.core.msimport.MSProjectXMLImport;
import uk.co.alvagem.projectview.dao.CalendarDAO;
import uk.co.alvagem.projectview.dao.ResourceDAO;
import uk.co.alvagem.projectview.dao.TaskDAO;
import uk.co.alvagem.projectview.model.Calendar;
import uk.co.alvagem.projectview.model.Resource;
import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.swingui.AboutBox;
import uk.co.alvagem.swingui.CommandActionSetBase;
import uk.co.alvagem.swingui.ExceptionDisplay;
import uk.co.alvagem.util.SettingsManager;
import uk.co.alvagem.util.XMLFileFilter;

public class CommandActionSet extends CommandActionSetBase {

    private Main app;
    
    public CommandActionSet(Main app){
        super();
        this.app = app;
        
        addAction("ImportMSPXML", actionImportMSPXML);
        addAction("ResourceNew", actionResourceNew);
        addAction("ResourceEdit", actionResourceEdit);
        addAction("ResourceDelete", actionResourceDelete);

        addAction("CalendarNew", actionCalendarNew);
        addAction("CalendarEdit", actionCalendarEdit);
        addAction("CalendarDelete", actionCalendarDelete);
        
        addAction("CheckDerived", actionCheckDerived);
        addAction("RollbackHistory", actionRollbackHistory);

        addAction("CreateSchema", actionCreateSchema);
        addAction("UpdateSchema", actionUpdateSchema);
        addAction("SelectConfiguration", actionSelectConfiguration);
        addAction("EditConnection", actionEditConnection);
        addAction("ConfigureLocalServer", actionConfigureLocalServer);
        addAction("StartLocalServer", actionStartLocalServer);
        addAction("StopLocalServer", actionStopLocalServer);
    }
    
    
    /**
     * @param calendars
     * @return
     */
    private Calendar selectCalendar(List<Calendar> calendars) {
        Calendar[] options = calendars.toArray(new Calendar[calendars.size()]);
        Calendar calendar = null;
        if(options.length == 1){
            calendar = options[0];
        } else if(options.length > 1) {
            Arrays.sort(options, new Comparator<Calendar>() {
                public int compare(Calendar t0, Calendar t1) {
                    return t0.getName().compareTo(t1.getName());
                }

            });
            calendar = (Calendar)JOptionPane.showInputDialog(
                app.getCommandFrame(),  "Select Calendar", app.getAppName(),
                JOptionPane.QUESTION_MESSAGE, null,
                options, null
            );
        }
        return calendar;
    }

    /**
     * @param resources
     * @return
     */
    private Resource selectResource(List<Resource> resources) {
        Resource[] options = resources.toArray(new Resource[resources.size()]);
        Resource resource = null;
        if(options.length == 1){
            resource = options[0];
        } else if(options.length > 1) {
            Arrays.sort(options, new Comparator<Resource>() {
                public int compare(Resource t0, Resource t1) {
                    return t0.getName().compareTo(t1.getName());
                }

            });
            resource = (Resource)JOptionPane.showInputDialog(
                app.getCommandFrame(),  "Select Resource", app.getAppName(),
                JOptionPane.QUESTION_MESSAGE, null,
                options, null
            );
        }
        return resource;
    }
    

    
    @Override
    protected AboutBox getAbout() {
        String[] rubric = {
                "Project View",
                "Version 0.1",
                "Written by R Bruce Porteous",
                "",
                "This product includes software developed by the",
                "Apache Software Foundation (http://www.apache.org/)"
        };
        AboutBox about = new AboutBox(getAppTitle(),rubric);
        return about;
    }

    private void showException(Throwable t){
        new ExceptionDisplay(app.getCommandFrame(), app.getAppTitle(),t);
    }
    
    /**
     * Clears any existing projects from the projects explorer.  Needed when the
     * database is re-initialised or when the app is re-pointed to a new database.
     */
    private void clearExistingProjects(){
    	app.getProjectsExplorer().clear();
    }
    
    /** Import MS Project XML */
    private final Action actionImportMSPXML = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {

                SettingsManager.Element cfg = getSettings().getOrCreateElement("/Files/MSPXMLPath");
                String path = cfg.attribute("path");
                
                JFileChooser chooser = new JFileChooser();
                if(path == null) 
                    chooser.setCurrentDirectory( new File("."));
                else
                    chooser.setSelectedFile(new File(path));
                chooser.setFileFilter( new XMLFileFilter());

                if( chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                	app.getCommandFrame().setCursor(Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        			try {
        				path = chooser.getSelectedFile().getPath();
        				cfg.setAttribute("path",path);
                        DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                        factory.beginTransaction();
                        try{
                            MSProjectXMLImport msp = new MSProjectXMLImport();
                            msp.parse(path);
                            factory.commit();
                        } catch (Throwable t) {
                            factory.rollback();
                            throw t;
                        }
        			} finally {
        				app.getCommandFrame().setCursor(Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
        			}
                }
            	
                

            } catch(Throwable t) {
                showException(t);
            }
        }
    }; 


    private final Action actionResourceNew = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {

                List<Calendar> calendars;
                DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                CalendarDAO daoCalendar = factory.getCalendarDAO();
                factory.beginTransaction();
                try{
                    calendars = daoCalendar.findAll();
                    factory.commit();
                } catch (Throwable t) {
                    factory.rollback();
                    throw t;
                }
                
                Resource resource = new Resource();
                ResourceEditor editor = new ResourceEditor(app.getCommandFrame(),resource, calendars);
                editor.setVisible(true);
                if(editor.wasEdited()){
                    ResourceDAO dao = factory.getResourceDAO();
                    factory.beginTransaction();
                    try {
                        dao.makePersistent(resource);
                        factory.commit();
                    } catch (Throwable t) {
                        factory.rollback();
                        throw t;
                    }
                    
                }

            } catch(Throwable t) {
                showException(t);
            }
        }
    }; 
    private final Action actionResourceEdit= new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                
                List<Resource> resources;
                List<Calendar> calendars;
                factory.beginTransaction();
                try {
                    ResourceDAO dao = factory.getResourceDAO();
                    CalendarDAO daoCalendar = factory.getCalendarDAO();
                    calendars = daoCalendar.findAll();
                    resources = dao.findAll();
                    factory.commit();
                } catch (Throwable t) {
                    factory.rollback();
                    throw t;
                }

                
                Resource resource = selectResource(resources);
                if(resource != null){
                    ResourceEditor editor = new ResourceEditor(app.getCommandFrame(),resource, calendars);
                    editor.setVisible(true);
                    if(editor.wasEdited()){
                        factory.beginTransaction();
                        try {
                            ResourceDAO dao = factory.getResourceDAO();
                            dao.makePersistent(resource);
                            factory.commit();
                        } catch (Throwable t) {
                            factory.rollback();
                            throw t;
                        }
                    }
                }
            } catch(Throwable t) {
                showException(t);
            }
        }
    };
    
    private final Action actionResourceDelete= new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                
                List<Resource> resources;
                factory.beginTransaction();
                try {
                    ResourceDAO dao = factory.getResourceDAO();
                    resources = dao.findAll();
                    factory.commit();
                } catch (Throwable t) {
                    factory.rollback();
                    throw t;
                }
                
                Resource resource = selectResource(resources);
                if(resource != null){
                    factory.beginTransaction();
                    try {
                        ResourceDAO dao = factory.getResourceDAO();
                        dao.makeTransient(resource);
                        factory.commit();
                    } catch (Throwable t) {
                        factory.rollback();
                        throw t;
                    }
                }
            } catch(Throwable t) {
                showException(t);
            }
        }
    };

    

    private final Action actionCalendarNew = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {

                Calendar calendar = new Calendar();
                DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                
                
                CalendarEditor editor;
                
                factory.beginTransaction();
                try {
                    CalendarDAO dao = factory.getCalendarDAO();
                    List<Calendar> calendars = dao.findAll();
                    editor = new CalendarEditor(app.getCommandFrame(),calendar, calendars);
                    factory.commit();
                } catch (Exception x){
                    factory.rollback();
                    throw x;
                }
                
                editor.setVisible(true);
                
                if(editor.wasEdited()){
                    factory.beginTransaction();
                    try {
                        CalendarDAO dao = factory.getCalendarDAO();
                        dao.makePersistent(calendar);
                        factory.commit();
                    } catch (Throwable t) {
                        factory.rollback();
                        throw t;
                    }
                    
                }

            } catch(Throwable t) {
                showException(t);
            }
        }
    }; 
    private final Action actionCalendarEdit= new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                
                List<Calendar> calendars;
                factory.beginTransaction();
                try {
                    CalendarDAO dao = factory.getCalendarDAO();
                    calendars = dao.findAll();
                    factory.commit();
                } catch (Throwable t) {
                    factory.rollback();
                    throw t;
                }
                
                Calendar calendar = selectCalendar(calendars);
                
                if(calendar != null){
                    
                    CalendarEditor editor;
                    
                    factory.beginTransaction();
                    try {
                        CalendarDAO dao = factory.getCalendarDAO();
                        dao.makePersistent(calendar);
                        editor = new CalendarEditor(app.getCommandFrame(),calendar,calendars);
                        factory.commit();
                    } catch (Throwable t) {
                        factory.rollback();
                        throw t;
                    }

                    editor.setVisible(true);
                    
                    if(editor.wasEdited()){
                        factory.beginTransaction();
                        try {
                            CalendarDAO dao = factory.getCalendarDAO();
                            dao.makePersistent(calendar);
                            factory.commit();
                        } catch (Throwable t) {
                            factory.rollback();
                            throw t;
                        }
                    }
                }
            } catch(Throwable t) {
                showException(t);
            }
        }
    };
    
    private final Action actionCalendarDelete= new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                CalendarDAO dao = factory.getCalendarDAO();
                
                List<Calendar> calendars;
                factory.beginTransaction();
                try {
                    calendars = dao.findAll();
                    factory.commit();
                } catch (Throwable t) {
                    factory.rollback();
                    throw t;
                }
                
                Calendar calendar = selectCalendar(calendars);
                if(calendar != null){
                    factory.beginTransaction();
                    try {
                        dao.makeTransient(calendar);
                        factory.commit();
                    } catch (Throwable t) {
                        factory.rollback();
                        throw t;
                    }
                }
            } catch(Throwable t) {
                showException(t);
            }
        }
    };

    
    /** Updates all the tasks in the DB ensuring that all the attributes synthesised
     * from child values are set properly.
     */
    private final Action actionCheckDerived = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                factory.beginTransaction();
                TaskDAO dao = factory.getTaskDAO();
                List<Task> tasks;
                try {
                    tasks = dao.findTopLevelTasks();
                    
                    for(Task top : tasks){
                    	top.updateFromSubTree();
                    }
                    
                    factory.commit();
                } catch (Throwable t) {
                    factory.rollback();
                    throw t;
                }
                        
                
            } catch(Throwable t) {
                showException(t);
            }
        }
    };   

    /** Updates all the tasks in the DB ensuring that all the attributes synthesised
     * from child values are set properly.
     */
    private final Action actionRollbackHistory = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                factory.beginTransaction();
                TaskDAO dao = factory.getTaskDAO();
                List<Task> tasks;
                try {
                    tasks = dao.findTopLevelTasks();
                    
                    for(Task top : tasks){
                    	top.rollbackHistory();
                    }
                    
                    factory.commit();
                } catch (Throwable t) {
                    factory.rollback();
                    throw t;
                }
                        
                
            } catch(Throwable t) {
                showException(t);
            }
        }
    };   

    
    
    private final Action actionCreateSchema = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
                        app.getCommandFrame(),
                        "This will create a new EMPTY database schema\n" +
                        "DELETING ALL EXISTING DATA.\n" +
                        "Are you sure you want to do this?",
                        "Create New Schema",
                        JOptionPane.YES_NO_OPTION)){
                    HibernateUtil.createSchema();
	                clearExistingProjects();
                }
                        
                
            } catch(Throwable t) {
                showException(t);
            }
        }
    };   

    private final Action actionUpdateSchema = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
                        app.getCommandFrame(),
                        "This will update the existing database schema\n" +
                        "Are you sure you want to do this?",
                        "Update Schema",
                        JOptionPane.YES_NO_OPTION)){
                    HibernateUtil.updateSchema();
	                clearExistingProjects();
                }
                        
                
            } catch(Throwable t) {
                showException(t);
            }
        }
    };   

    private final Action actionSelectConfiguration = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
            	SettingsManager settings = app.getSettings();
            	List<String> available = HibernateConfigurator.getAvailableConfigurations(settings);
            	String name = null;
            	if(available.isEmpty()){
            		JOptionPane.showMessageDialog(app.getCommandFrame(), "There are no configurations defined", app.getAppTitle(), JOptionPane.INFORMATION_MESSAGE);
            	} else if(available.size() == 1){
            		name = available.get(0);
            		JOptionPane.showMessageDialog(app.getCommandFrame(), "Using configuration " + name, app.getAppTitle(), JOptionPane.INFORMATION_MESSAGE);
            	} else {
            		name = (String)JOptionPane.showInputDialog(
            	        	app.getCommandFrame(),  "Select Configuration", app.getAppTitle(),
            	        	JOptionPane.QUESTION_MESSAGE, null,
            	        	available.toArray(), null
            	        );
            	}
            	if(name != null){
            		HibernateConfigurator hcfg = app.getConfigurator();
            		hcfg.useConfiguration(app.getSettings(), name);
            		app.setConfiguration(hcfg);
            		clearExistingProjects();
            	}
                
            } catch(Throwable t) {
                showException(t);
            }
        }
    };   
    private final Action actionEditConnection = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                HibernateConfigurator hcfg = app.getConfigurator(); // Gets a new configurator.
                hcfg.configureFrom(app.getSettings()); // and make sure it reflects current configuration.
                
                HibernateConfigurator.DataSource ds = hcfg.getDataSource();
                
                DataSourceEditor editor = new DataSourceEditor(app.getCommandFrame(), ds);
                if(editor.wasEdited()){
	                hcfg.setDataSource(ds);
	                hcfg.updateConfig(app.getSettings()); // Save in settings. May be wrong but allows editing!
	                app.setConfiguration(hcfg);
	                clearExistingProjects();
                }
                
            } catch(Throwable t) {
                showException(t);
            }
        }
    };   

    private final Action actionConfigureLocalServer = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                LocalServerEditor editor = new LocalServerEditor(app.getCommandFrame(),app.getLocalServer());
                if(editor.wasEdited()){
                    app.getLocalServer().updateConfig(app.getSettings());
                }
            } catch(Throwable t) {
                showException(t);
            }
        }
    };   
    
    private final Action actionStartLocalServer = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                app.getLocalServer().start();
            } catch(Throwable t) {
                showException(t);
            }
        }
    };   

    private final Action actionStopLocalServer = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                app.reset(); // dump any content that may be tied to this db.
                app.getLocalServer().stop();
            } catch(Throwable t) {
                showException(t);
            }
        }
    };   
    
}
