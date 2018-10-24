/*
 * LocalServer.java
 * Project: ProjectView
 * Created on 31 Dec 2007
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.io.File;

import org.hsqldb.Server;
import org.hsqldb.ServerConstants;

import uk.co.alvagem.util.SettingsManager;
import uk.co.alvagem.util.SettingsManager.Element;

/**
 * LocalServer provides a local (HSQLDB) database server.
 * 
 * @author rbp28668
 */
public class LocalServer {

    private Server server = null;
    private boolean trace = false;
    private boolean silent = true;
    private boolean autostart = true;
    private String name = "projectview";

    /**
     * Starts the local database server.
     */
    public void start(){

        if(server != null){
            stop();
        }
        
        String dir = System.getProperty("user.home");

        server = new Server();
        server.setDatabaseName(0,name);
        server.setDatabasePath(0, dir + File.separator + name);
        server.setTrace(trace);
        server.setSilent(silent);
        server.start();
        try {
            while(server.getState() != ServerConstants.SERVER_STATE_ONLINE){
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the local database server if the autostart property is true.
     */
    public void autoStart(){
        if(autostart){
            start();
        }
    }
    
    public void stop(){
        if(server != null){
            server.stop();
            server = null;
        }
    }

    /**
     * @return the trace
     */
    public boolean isTrace() {
        return trace;
    }

    /**
     * @param trace the trace to set
     */
    public void setTrace(boolean trace) {
        this.trace = trace;
    }

    /**
     * @return the silent
     */
    public boolean isSilent() {
        return silent;
    }

    /**
     * @param silent the silent to set
     */
    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the autostart
     */
    public boolean isAutostart() {
        return autostart;
    }

    /**
     * @param autostart the autostart to set
     */
    public void setAutostart(boolean autostart) {
        this.autostart = autostart;
    }

    void configureFrom(SettingsManager settings){
        Element e = settings.getOrCreateElement("/LocalServer");
        String val = e.attribute("name");
        if(val != null) setName(val);

        val = e.attribute("trace");
        if(val != null) setTrace(val.equals("true"));
        
        val = e.attribute("silent");
        if(val != null) setSilent(val.equals("true"));
        
        val = e.attribute("autostart");
        if(val != null) setAutostart(val.equals("true"));
    }
    
    void updateConfig(SettingsManager settings){
        Element e = settings.getOrCreateElement("/LocalServer");
        e.setAttribute("name",getName());
        e.setAttribute("trace", isTrace() ? "true" : "false");
        e.setAttribute("silent", isSilent() ? "true" : "false");
        e.setAttribute("autostart", isAutostart() ? "true" : "false");
    }
    
}
