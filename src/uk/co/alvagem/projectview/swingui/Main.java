/*
 * Main.java
 * Project: ProjectView
 * Created on 28 Dec 2007
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.util.Map;

import javax.security.auth.Subject;

import org.hibernate.Interceptor;
import org.hibernate.cfg.Configuration;

import uk.co.alvagem.database.DAOFactory;
import uk.co.alvagem.database.HibernateConfigurator;
import uk.co.alvagem.database.HibernateUtil;
import uk.co.alvagem.projectview.core.DatabaseSerialiser;
import uk.co.alvagem.projectview.core.security.UserPrincipal;
import uk.co.alvagem.projectview.model.ChangeTrackInterceptor;
import uk.co.alvagem.swingui.AppBase;
import uk.co.alvagem.swingui.CommandActionSetBase;
import uk.co.alvagem.swingui.InitialisationException;
import uk.co.alvagem.swingui.SwingForm;
import uk.co.alvagem.ui.FieldTemplate;
import uk.co.alvagem.util.IXMLContentHandler;
import uk.co.alvagem.util.SettingsManager;
import uk.co.alvagem.util.UUID;
import uk.co.alvagem.util.XMLWriter;

public class Main extends AppBase {

    private LocalServer server;
    private Subject user;
    private ProjectsExplorerFrame explorer;
    
    /**
     * @param args
     */
    public static void main(String[] args) {

        Main main = new Main();
        try {
           
//            // Debug
//            Session session = HibernateUtil.getSession();
//            try {
//                SchemaView.showTables(session.connection());
//            } finally {
//                HibernateUtil.closeSession();
//            }
//                
            main.run(args);     
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Main(){
        super();
    }
 
    public LocalServer getLocalServer(){
        return server;
    }
    
    public ProjectsExplorerFrame getProjectsExplorer(){
    	return explorer;
    }
    
    @Override
    protected CommandActionSetBase getActions() {
        return new CommandActionSet(this);
    }

    @Override
    public String getAppName() {
        return "ProjectView";
    }

    @Override
    public String getAppTitle() {
        return "Project View";
    }

    @Override
    public void reset() {
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.swingui.AppBase#processSettings(uk.co.alvagem.util.SettingsManager)
     */
    @Override
    protected void processSettings(SettingsManager settings) throws InitialisationException {
      
    	HibernateConfigurator hcfg = getConfigurator();
    	hcfg.configureFrom(settings);
    	// TODO - include option to turn-off local server.  Make explicit as always possible that
    	// someone is running a shared instance of HSQLDB.
    	
    	server = new LocalServer();
        server.configureFrom(settings);
        server.autoStart();
        
        // Setup the current user.
        user = new Subject();
        String name = System.getProperty("user.name");
        user.getPrincipals().add(new UserPrincipal(name));
        
        try {
        	setConfiguration(hcfg);
        } catch(ClassNotFoundException cnf){
        	throw new InitialisationException("JDBC driver not found: " + cnf.getMessage(),cnf);
        }

        initUUID(settings);
    }

	/**
	 * @param hcfg
	 * @throws ClassNotFoundException 
	 */
	public void setConfiguration(HibernateConfigurator hcfg) throws ClassNotFoundException {
		Interceptor interceptor = new ChangeTrackInterceptor(user);
        Configuration cfg = hcfg.getConfig();
        cfg.setInterceptor(interceptor);
        
        HibernateUtil.setConfig(cfg);
	}

	/**
	 * @return
	 */
	public HibernateConfigurator getConfigurator() {
		HibernateConfigurator hcfg = new HibernateConfigurator();
        hcfg.addClass(uk.co.alvagem.projectview.model.Task.class);
        hcfg.addClass(uk.co.alvagem.projectview.model.TaskHistory.class);
        hcfg.addClass(uk.co.alvagem.projectview.model.TaskRole.class);
        hcfg.addClass(uk.co.alvagem.projectview.model.Allocation.class);
        hcfg.addClass(uk.co.alvagem.projectview.model.Resource.class);
        hcfg.addClass(uk.co.alvagem.projectview.model.Dependency.class);
        hcfg.addClass(uk.co.alvagem.projectview.model.Constraint.class);
        hcfg.addClass(uk.co.alvagem.projectview.model.Calendar.class);
        hcfg.addClass(uk.co.alvagem.projectview.model.CalendarItem.class);
		return hcfg;
	}

    /**
     * Method initUUID initialises the UUID handling for this session..
     */
    private void initUUID(SettingsManager settings) {
        String mac = UUID.findMACAddress();
        String state = null;

        try {
            SettingsManager.Element me = null;
            me = settings.getElement("/UUID");
            state = me.attribute("state");
        } catch (Exception e) {
            // NOP - fail quietly with null state.
        }

        UUID.initialise(mac, state);
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.swingui.AppBase#postGUIInit()
     */
    @Override
    protected void postGUIInit() {
        explorer = new ProjectsExplorerFrame(this,"Projects");
        getCommandFrame().getDesktop().add(explorer);
        
        // Register any special type handlers.
        FieldTemplate.register(new ElapsedTimeHandler(), ElapsedTimeHandler.TYPE_KEY);
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.swingui.AppBase#dispose()
     */
    @Override
    public void dispose() {
        server.stop();

        SettingsManager.Element me = getSettings().getOrCreateElement("/UUID");
        me.setAttribute("state",UUID.getState());

        super.dispose();
        
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.swingui.AppBase#getContentHandlers()
     */
    @Override
    protected Map<String, IXMLContentHandler> getContentHandlers() {
        DatabaseSerialiser dbs = new DatabaseSerialiser();
        return dbs.getHandlers();
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.swingui.AppBase#getNamespace()
     */
    @Override
    protected String getNamespace() {
        return "http://alvagem.co.uk/projectview";
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.swingui.AppBase#getNsPrefix()
     */
    @Override
    protected String getNsPrefix() {
        return "pv";
    }

    /* (non-Javadoc)
     * @see uk.co.alvagem.swingui.AppBase#writeXML(uk.co.alvagem.util.XMLWriter)
     */
    @Override
    protected void writeXML(XMLWriter writer) throws Exception {
        DatabaseSerialiser dbs = new DatabaseSerialiser();
        dbs.writeDB(writer);
    }

	/* (non-Javadoc)
	 * @see uk.co.alvagem.swingui.AppBase#loadXML(java.lang.String)
	 */
	@Override
	public void loadXML(String path) throws Exception {
		DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
		factory.beginTransaction();
		try {
			super.loadXML(path);
		} catch (Exception e) {
			factory.rollback();
			throw e;
		}
		factory.commit();
	}

//	/* (non-Javadoc)
//	 * @see uk.co.alvagem.swingui.AppBase#saveXML(java.lang.String)
//	 */
//	@Override
//	public void saveXML(String path) throws Exception{
//		DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
//		factory.beginTransaction();
//		try {
//			super.saveXML(path);
//		} catch (Exception e) {
//			factory.rollback();
//			throw e;
//		}
//		factory.commit();
//	}

}
