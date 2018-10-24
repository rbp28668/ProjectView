/*
 * ProjectsExplorerFrame.java
 * Project: ProjectView
 * Created on 29 Dec 2007
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import uk.co.alvagem.swingui.GUIBuilder;
import uk.co.alvagem.util.SettingsManager;
import uk.co.alvagem.util.SettingsManager.Element;

public class ProjectsExplorerFrame extends JInternalFrame {

    private static final long serialVersionUID = 1L;

    private ProjectsExplorer tree;
    private final static String SETTINGS_KEY = "/Windows/ProjectsExplorer";
    private SettingsManager settings;

    /**
     * @param arg0
     */
    public ProjectsExplorerFrame(Main app, String title) {
        super(title);
        
        setTitle(title);
        setResizable(true);
        setMaximizable(false);
        setIconifiable(true);
        setClosable(false);
        
        settings = app.getSettings();
        GUIBuilder.loadBounds(this, settings, SETTINGS_KEY);
        
        ProjectsExplorerActionSet actions = new ProjectsExplorerActionSet(app);
        tree = new ProjectsExplorer(app,actions);
        actions.setExplorer(tree);
        
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        
        setLayout(new BorderLayout());
        
        JToolBar toolbar = new JToolBar();
        Element cfg = app.getConfig().getOrCreateElement("/ProjectsExplorer/toolbar");
        if(cfg.getChildCount() > 0){
            GUIBuilder.buildToolbar(toolbar, actions, cfg);
            getContentPane().add(toolbar, java.awt.BorderLayout.NORTH);
        }
        
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        scrollPane.setViewportView(tree);
        getContentPane().add(scrollPane,java.awt.BorderLayout.CENTER);
        setVisible(true);
    }

    public void dispose() {
        GUIBuilder.saveBounds(this, settings, SETTINGS_KEY);
        tree.dispose();
        tree = null;
    }

    /**
     * Clears the underlying tree.  Use when any objects in the tree may no
     * longer match the underlying database.
     */
    public void clear(){
    	tree.clearContents();
    }
}
