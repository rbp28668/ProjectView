/*
 * CommandActionSet.java
 *
 * Created on 23 January 2002, 22:49
 */

package uk.co.alvagem.swingui;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import uk.co.alvagem.util.SettingsManager;
import uk.co.alvagem.util.XMLFileFilter;


/**
 * CommandActionSet provides the ActionSet for the main menus.
 * @author Bruce.Porteous
 *
 */
public  abstract class CommandActionSetBase extends ActionSet {

	/** parent command frame */
    private CommandFrame frame;
    
    /** Skeleton application - has settings and knows how to load & save using XML */
    private AppBase appBase;
    

    /** Creates new CommandActionSet */
    public CommandActionSetBase() {
        super();
 
        addAction("FileNew", actionFileNew);
        addAction("FileOpen", actionFileOpen);
        addAction("FileSave", actionFileSave);
        addAction("FileSaveAs", actionFileSaveAs);
        
        addAction("FileProperties",actionFileProperties);
        addAction("FileExit",actionFileExit);

       
		// -- Window --
		addAction("WindowPLAFMetal", actionWindowPLAFMetal);        
		addAction("WindowPLAFMotif", actionWindowPLAFMotif);        
		addAction("WindowPLAFWindows", actionWindowPLAFWindows);        

        // -- Help --
        addAction("HelpAbout", actionHelpAbout);
        
    }    

    /**
     * @param appBase
     */
    void setAppBase(AppBase appBase) {
        this.appBase = appBase;
        this.frame = appBase.getCommandFrame();
    }

    /**
     * @return
     */
    protected SettingsManager getSettings(){
        return appBase.getSettings();
    }

    /**
     * 
     */
    protected void onFileNew(){
        appBase.reset();
    }
    
    protected String getCurrentPath(){
        return appBase.getCurrentPath();
    }
    
    protected void loadFile(String path) throws Exception{
        appBase.loadXML(path);
    }
    
    /**
     * @param path
     */
    protected void saveFile(String path) throws Exception{
         appBase.saveXML(path);
    }

    protected String getAppTitle(){
        return appBase.getAppTitle();
    }
    
    /** File new*/
    private final Action actionFileNew = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
            try {
            	onFileNew();
            } catch(Throwable t) {
                new ExceptionDisplay(frame, getAppTitle(),t);
            }
        }
    };   

    
    
    /** File Open action*/
    private final Action actionFileOpen = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
            try {
                SettingsManager.Element cfg = getSettings().getOrCreateElement("/Files/XMLPath");
                String path = cfg.attribute("path");
                
                JFileChooser chooser = new JFileChooser();
                if(path == null) 
                    chooser.setCurrentDirectory( new File("."));
                else
                    chooser.setSelectedFile(new File(path));
                chooser.setFileFilter( new XMLFileFilter());

                if( chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                	frame.setCursor(Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        			try {
        				path = chooser.getSelectedFile().getPath();
        				cfg.setAttribute("path",path);
        				onFileNew();
        				loadFile(path);
        			} finally {
        				frame.setCursor(Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
        			}
                }
            } catch(Throwable t) {
                new ExceptionDisplay(frame,getAppTitle(),t);
            }
        }
    };   

    
    /** File Save action - saves the repository as XML*/
    private final Action actionFileSave = new AbstractAction() {
		private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
            	String path = getCurrentPath();
 
                if(path == null) {
                	JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory( new File("."));
                    chooser.setFileFilter( new XMLFileFilter());

                    if( chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    	path = chooser.getSelectedFile().getPath();
                    }
                }
                if(path != null) {
                    saveFile(path);
                }
            } catch(Throwable t) {
                new ExceptionDisplay(frame,getAppTitle(),t);
            }
        }
    };   

    /** File Save As action - saves the repository as XML*/
    private final Action actionFileSaveAs = new AbstractAction() {
		private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                SettingsManager.Element cfg = getSettings().getOrCreateElement("/Files/XMLPath");
                String path = cfg.attribute("path");
 
                JFileChooser chooser = new JFileChooser();
                if(path == null) 
                    chooser.setCurrentDirectory( new File("."));
                else
                    chooser.setSelectedFile(new File(path));
                chooser.setFileFilter( new XMLFileFilter());

                if( chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    path = chooser.getSelectedFile().getPath();
                    cfg.setAttribute("path",path);
                    saveFile(path);
                }
            } catch(Throwable t) {
                new ExceptionDisplay(frame,getAppTitle(),t);
            }
        }
    };   
    
    
    /** File Properties action - allows the user to edit the repository properties*/
    private final Action actionFileProperties = new AbstractAction() {
		private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                onEditProperties();
            } catch(Throwable t) {
                new ExceptionDisplay(frame,getAppTitle(),t);
            }
        }
    };   
    
    /**
     * 
     */
    protected void onEditProperties(){}

    /** File Exit action - terminates the application. */
    private final Action actionFileExit = new AbstractAction() {
		private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            //System.out.println("File Exit called.");
            if(JOptionPane.showConfirmDialog(null,"Exit application?",getAppTitle(),
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                fileExit();
            }
        }
    };   
    
    /**
     * Close the application.
     */
    public void fileExit(){
        appBase.dispose();
        System.exit(0);
    }

   
    /** WindowPLAFMetal action - displays Metal look and feel */
    private final Action actionWindowPLAFMetal = new AbstractAction() {
		private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
            	setPlaf("javax.swing.plaf.metal.MetalLookAndFeel");
           } catch(Throwable t) {
                new ExceptionDisplay(frame,getAppTitle(),t);
            }
        }
    };   
    /** WindowPLAFMotif action - displays Motif look and feel */
    private final Action actionWindowPLAFMotif = new AbstractAction() {
		private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
            	setPlaf("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
           } catch(Throwable t) {
                new ExceptionDisplay(frame,getAppTitle(),t);
            }
        }
    };   
    /** WindowPLAFWindows action - displays Windows look and feel */
    private final Action actionWindowPLAFWindows = new AbstractAction() {
		private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
            	setPlaf("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
           } catch(Throwable t) {
                new ExceptionDisplay(frame,getAppTitle(),t);
            }
        }
    };   
    
    /** private helper to set Pluggable Look & Feel (PLAF)
     * @param name is the PLAF name
     * @throws one of ClassNotFoundException, 
     *	IllegalAccessException, 
     *	InstantiationException, 
	 *   javax.swing.UnsupportedLookAndFeelException
     */ 
    private void setPlaf(String name) 
	throws ClassNotFoundException, 
	IllegalAccessException, 
	InstantiationException, 
    javax.swing.UnsupportedLookAndFeelException{
    	javax.swing.UIManager.setLookAndFeel(name);
    	SwingUtilities.updateComponentTreeUI(frame.getRootPane());
    }

    /** Help About action - displays the about box */
    private final Action actionHelpAbout = new AbstractAction() {
		private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                AboutBox about = getAbout();
                about.setVisible(true);
           } catch(Throwable t) {
                new ExceptionDisplay(frame,getAppTitle(),t);
            }
        }
    };   
    
    protected abstract AboutBox getAbout();

    
 }
