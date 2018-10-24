/**
 * 
 */
package uk.co.alvagem.swingui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import uk.co.alvagem.util.IXMLContentHandler;
import uk.co.alvagem.util.InputException;
import uk.co.alvagem.util.OutputException;
import uk.co.alvagem.util.SettingsManager;
import uk.co.alvagem.util.XMLLoader;
import uk.co.alvagem.util.XMLWriter;
import uk.co.alvagem.util.XMLWriterSAX;


/**
 * @author bruce.porteous
 *
 */
public abstract class AppBase {

	private SettingsManager config;
	private SettingsManager settings;
	private CommandFrame commandFrame;
	
	private String configPath = "config.xml";
	private String settingsPath = "settings.xml";

	private String currentPath = null;	// path of currently loaded file.

    private List<AppEventListener> listeners = new LinkedList<AppEventListener>();
	

	/**
	 * 
	 */
	public AppBase() {
		super();
	}

	public void dispose(){
		settings.save(settingsPath);
	}
	
	/**
	 * @return Returns the commandFrame.
	 */
	public CommandFrame getCommandFrame() {
		return commandFrame;
	}

	/**
	 * @return Returns the config.
	 */
	public SettingsManager getConfig() {
		return config;
	}

	/**
	 * @return Returns the configPath.
	 */
	public String getConfigPath() {
		return configPath;
	}

	/**
	 * @return Returns the settings.
	 */
	public SettingsManager getSettings() {
		return settings;
	}

	/**
	 * @return Returns the settingsPath.
	 */
	public String getSettingsPath() {
		return settingsPath;
	}

	/**
	 * Get a name for the application.  Used for the root of the settings file
	 * and the name of the file itself.  Note - must not contain spaces!
	 * @return an application name.
	 */
	public abstract String getAppName();
	
	/**
	 * Get a title for the application - this is displayed to users and can
	 * contain spaces etc.
	 * @return
	 */
	public abstract String getAppTitle();
	
	/**
	 * Gets the action set for the command frame.
	 * @return
	 */
	protected abstract CommandActionSetBase getActions();
	
	/**
	 * Call this from the application main to run the application.
	 * @param args are the command line args passed to the application.
	 */
	protected void run(String[] args){
		try {


			// Setup basic configuration files
			// Configuration file should be in the jar file.  If it's missing
			// then the app cannot set up its menus so die gracefully.
			config = new SettingsManager(); // configuration
			InputStream stream = getClass().getResourceAsStream(configPath);
			if(stream == null)
				throw new IOException("Missing Config file: " + configPath);
			config.load(stream);

			processConfig(config);

			
			// Settings file however is in user-land.  The first time through
			// there may not be a settings file.
			settings = new SettingsManager(); // user settings
			stream = null;
			String dir = System.getProperty("user.home");
			if(dir != null){
				File file = new File(dir,"." + getAppName() + "rc.xml");
				settingsPath = file.getCanonicalPath();
				if(file.exists()){
				    stream = new FileInputStream(file);
				}
			}
			
			if(stream != null) {
				settings.load(stream);
			} else {
				settings.setEmptyRoot(getAppName());
			}

			processSettings(settings);

			// Init the GUI
			CommandActionSetBase actions = getActions();
			commandFrame = createCommandFrame(getAppTitle(), actions, config);
			actions.setAppBase(this);
			
            commandFrame.setVisible(true);

			
			postGUIInit();
			
			// Load any files specified on the command line.
			int nArgs = args.length;
			if (nArgs > 0) {
				for (int i = 0; i < nArgs; ++i) {
					//System.out.println("Parsing " + args[i]);
					loadXML(args[i]);
				}
			}
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
		
	}

  protected CommandFrame createCommandFrame(String appTitle, CommandActionSetBase actions, SettingsManager config) {
    return new CommandFrame(appTitle, actions, config);
  }

	/**
	 * Called during startup once the config file is loaded.  Over-ride to do
	 * any application specific initialisation from the config file.
	 * @param config is the application Configuration.
	 */
	protected void processConfig(SettingsManager config) throws InitialisationException {
    }

    /**
     * Called during startup once the settings file is loaded.  Over-ride to do
     * any application specific initialisation from the settings file.
     * @param config is the application Configuration.
     */
    protected void processSettings(SettingsManager settings) throws InitialisationException  {
    }

    /**
	 * Called when the GUI is constructed. Over-ride for application specific
	 * GUI configuration.
	 */
	protected void postGUIInit() throws InitialisationException {
	}
	
	/**
	 * Gets the namespace used for loading/saving xml.  Over-ride this method
	 * to change the namespace from the default.
	 * @return the namespace.
	 */
	protected String getNamespace(){
	   return "http://alvagem.co.uk/" + getAppName();
	}
	
	
	/**
	 * Gets the namespace prefix to use for writing files. Over-ride this method
	 * to change the namespace prefix from the default.
     * @return the nsPrefix
     */
    protected String getNsPrefix() {
        return getAppName();
    }


	
    /**
     * Gets a map of IXMLContentHandlers keyed by the entity name they should be invoked
     * for.  By default this method throws an UnsupportedOperationException.
     * @return a Map of IXMLContentHandler keyed by String.
     */
    protected Map<String,IXMLContentHandler> getContentHandlers(){
        throw new UnsupportedOperationException("getContentHandlers");
    }
	
	/**
	 * Clears contents.
	 */
	public abstract void reset();
	
	/**
	 * Loads XML using any registered handlers.
	 * @param string is the path to load from.
	 */
	public void loadXML(String path) throws Exception{
	    currentPath = path;
        XMLLoader loader = new XMLLoader();
        loader.setNameSpaces(true);
        
        // Set up handlers for the different objects.
        String namespace = getNamespace();
        Map<String, IXMLContentHandler>xmlHandlers = getContentHandlers();
        if(xmlHandlers.isEmpty()){
            throw new IllegalStateException("No content handlers provided for loadXML");
        }
        
        for(Map.Entry<String, IXMLContentHandler> entry : xmlHandlers.entrySet()){
            String tag = entry.getKey();
            IXMLContentHandler handler = entry.getValue();
            loader.registerContent(namespace,tag,handler);
        }
		
        try {
            loader.parse(path);
        }
        catch(SAXParseException e) {
            throw new InputException("SAX Parse Exception. Line " + e.toString() +
                " at line " + e.getLineNumber() + ", column " + e.getColumnNumber(), e);
        }
        catch(SAXException e) {
            throw new InputException("Problem parsing xml file: " + e.getMessage(),e);
        }
        
	}
	
	/**
	 * Over-ride this to write XML.
	 * @param writer is the XMLWriter used to output the data.
	 */
	protected void writeXML(XMLWriter writer) throws Exception{
	    throw new UnsupportedOperationException("writeXML");
	}
	
	/**
	 * Save application data to the given path.  Will only work if writeXML(XMLWriter) has
	 * been over-ridden otherwise it will throw an UnsupportedOperationException.
	 * @param path is the path to write to.
	 */
	public void saveXML(String path) throws Exception {
		try {
			aboutToSave();
	        currentPath = path;
			
	        XMLWriter writer = new XMLWriterSAX(new FileOutputStream(path));
	        try{
	            writer.startXML();
	            writer.setNamespace(getNsPrefix(),getNamespace());
	            writer.startEntity(getAppName());
	 
	    		writeXML(writer);
	    		
	            writer.stopEntity();
	            writer.stopXML();
	        } finally {
	            writer.close();
	        }
	    }
	    catch(Exception e) {
	        throw new OutputException("problem saving xml file: " + e.getMessage(),e);
	    }
	}

	/**
	 * Adds a listener to receive application events.
	 * @param listener is the listener to add.
	 */
	public void addListener(AppEventListener listener){
		listeners.add(listener);
	}
	
	/**
	 * Removes a listener which will no longer receive application events.
	 * @param listener is the listener to remove.
	 * @return true if the listener was registered.
	 */
	public boolean removeListener(AppEventListener listener){
		return listeners.remove(listener);
	}
	
	/**
	 * Signals to any listener that the application is about to save its state so
	 * now would be a good time to update that state from any open windows etc.
	 */
	public void aboutToSave() {
		for(Iterator<AppEventListener> iter = listeners.iterator(); iter.hasNext();){
			AppEventListener listener = iter.next();
			listener.aboutToSave();
		}
	}
	
	/**
	 * Asks the listeners whether there are any unsaved changes.  Note this is not guaranteed
	 * to call all listeners - another listener may already have flagged unsaved changes.
	 * @return true if there are unsaved changes.
	 */
	public boolean hasUnsaved(){
		for(Iterator<AppEventListener> iter = listeners.iterator(); iter.hasNext();){
			AppEventListener listener = iter.next();
			if(listener.hasUnsaved()){
				return true;
			}
		}
		return false;
	}

	/**
	 * @return Returns the currentPath.  This is where the application data was
	 * loaded from, or last save to.
	 */
	public String getCurrentPath() {
		return currentPath;
	}

}
