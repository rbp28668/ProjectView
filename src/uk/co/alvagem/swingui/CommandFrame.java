/*
 * CommandFrame.java
 *
 * Created on 21 January 2002, 21:05
 */

package uk.co.alvagem.swingui;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import uk.co.alvagem.util.SettingsManager;
import uk.co.alvagem.util.SettingsManager.Element;


/**
 * The top level window of the application.
 * @author  rbp28668
 */
public class CommandFrame extends javax.swing.JFrame {

    /** All the basic top level actions */
    private ActionSet actions;

    private javax.swing.JMenuBar menuBar;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JComponent desktop;

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/** Creates new form CommandFrame */
    public CommandFrame(String title, ActionSet actions, SettingsManager config ) {
        this.actions = actions;
    	initComponents(config);
        
        setTitle(title);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screen = tk.getScreenSize();
        setLocation(0,0);
            
        Element cfg = config.getElement("/CommandFrame/menus");
        GUIBuilder.buildMenuBar(menuBar, actions, cfg);
        setJMenuBar(menuBar);
        
       
        cfg = config.getOrCreateElement("/CommandFrame/toolbar");
        if(cfg.getChildCount() > 0){
            GUIBuilder.buildToolbar(toolBar, actions, cfg);
        }
        
        setSize(screen.width,screen.height);
        
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        
    }

    /**
     * Create the main window in the desktop.  Usually a JDesktopPane but may
     * be over-ridden for specialist behaviour e.g. an explorer pane that fills
     * the entire application.
     * @return
     */
    protected JComponent createMainWindow(SettingsManager config) {
      return new javax.swing.JDesktopPane();
    }
    
    /**
     * Gets the component for the main window.  Usually a JDesktopPane but 
     * doesn't have to be.
     * @return
     */
    public JComponent getMainWindow() {
      return desktop;
    }
    
    /**
     * Gets the desktop pane that should contain the application's 
     * child windows. This is a convenience method that returns a JDesktopPane
     * but will throw a class cast exception if it isn't a normal desktop.
	 * @return application's desktop.
	 */
	public JDesktopPane getDesktop() {
        return (JDesktopPane) desktop;
    }

    
    /** This method is called from within the constructor to
     * initialize the form.
      */
    private void initComponents(SettingsManager config) {
        menuBar = new javax.swing.JMenuBar();
        toolBar = new javax.swing.JToolBar();
        desktop = createMainWindow(config);
        
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        
        getContentPane().add(toolBar, java.awt.BorderLayout.NORTH);
        getContentPane().add(desktop, java.awt.BorderLayout.CENTER);

        pack();
    }

    /** 
     * Conditionally exits the Application. 
     * @param evt is the window event that triggered the close.
     */
    private void exitForm(java.awt.event.WindowEvent evt) {
        Action fileExit = actions.getAction("FileExit");
        fileExit.actionPerformed(new ActionEvent(this,0,"FileExit"));
        setVisible(true);   // if System.exit hasn't been called!
    }

	/**
	 * Tiles the given image across the background.
	 * @param image is the Image to be tiled.
	 * @param greyed if true, greys the image.
	 */
	public void tileBackgroundImage(Image image, boolean greyed) {
        ImageIcon icon = new ImageIcon(image); 
        if(greyed){
            icon = new ImageIcon(GrayFilter.createDisabledImage(icon.getImage()));
        }
        tileBackgroundIcon(icon);
	}        
	
   	protected void tileBackgroundIcon(ImageIcon icon) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screen = tk.getScreenSize();

        int nx = icon.getIconWidth();
        int ny = icon.getIconHeight();

        int cx = (screen.width + nx -1) / nx;
        int cy = (screen.height + ny -1) / ny;
        
        int iyCoord = 0;
        for(int iy=0; iy<cy; ++iy) {
            int ixCoord = 0;
            for(int ix = 0; ix<cx; ++ix) {
                JLabel l = new JLabel(icon);
                l.setBounds(ixCoord, iyCoord, nx, ny);
                desktop.add(l, new Integer(Integer.MIN_VALUE)); // on the bottommost layer possible
                ixCoord += nx;
            }
            iyCoord += ny;
        }
    }

	/**
	 * Stretches the given image across the background.
	 * @param image is the Image to be tiled.
	 * @param greyed if true, greys the image.
	 */
	public void stretchBackgroundImage(Image image, boolean greyed) {
        ImageIcon icon = new ImageIcon(image); 
        if(greyed){
            icon = new ImageIcon(GrayFilter.createDisabledImage(icon.getImage()));
        }
        stretchBackgroundIcon(icon);
	}        
	
   	protected void stretchBackgroundIcon(ImageIcon icon) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screen = tk.getScreenSize();

        java.awt.Image source = icon.getImage();
        java.awt.Image scaled = source.getScaledInstance(screen.width, screen.height, java.awt.Image.SCALE_DEFAULT);
        icon.setImage(scaled);
        
        JLabel l = new JLabel(icon);
        l.setBounds(0,0,screen.width,screen.height);
        desktop.add(l, new Integer(Integer.MIN_VALUE)); // on the bottommost layer possible
    }
   	
	/**
	 * Gets the ActionSet of Actions used for the top level menus.
	 * @return ActionSet with the main application actions.
	 */
	public ActionSet getActions(){
	    return actions;
	}
	

}
