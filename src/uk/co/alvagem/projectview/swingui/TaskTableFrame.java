/*
 * TaskTableFrame.java
 * Project: ProjectView
 * Created on 13 Jan 2008
 *
 */
package uk.co.alvagem.projectview.swingui;

import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.swingui.ActionSet;
import uk.co.alvagem.swingui.GUIBuilder;
import uk.co.alvagem.util.SettingsManager;

public class TaskTableFrame extends JInternalFrame {

    /**  */
    private static final long serialVersionUID = 1L;
    
    private final static String MENU_CONFIG = "/TaskTable/menus";
    
    private TaskTableModel model;
    private JMenuBar menuBar;


    public TaskTableFrame(Task root, Main app) {
        super(root.getName());
        
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        
        setLocation(100,100);
        setSize(300,200);

        //settings = app.getSettings();
        //GUIBuilder.loadBounds(this, settings, SETTINGS_KEY);
        
        SettingsManager config = app.getConfig();
        SettingsManager.Element cfg = config.getElement(MENU_CONFIG);
        ActionSet actions = new TaskTableActionSet(app,this);
        setMenuBar(cfg, actions);
        

        model = new TaskTableModel(root);
        JTable table = new TaskTable(model);
        JScrollPane scroll = new JScrollPane(table);
        getContentPane().add(scroll);
        setVisible(true);
        
    }
    
    /**
     * Sets the menu bar from the configuration.
     * @param cfg is the configuration of the menus.
     * @param actions is the ActionSet containing the actions for the menu.
     */
    protected void setMenuBar(SettingsManager.Element cfg, ActionSet actions) {
        menuBar = new JMenuBar();
        GUIBuilder.buildMenuBar(menuBar, actions, cfg);
        setJMenuBar(menuBar);
    }
    
    /**
     * @return the model
     */
    public TaskTableModel getModel() {
        return model;
    }
    

    private static class TaskTable extends JTable {
        
 		private static final long serialVersionUID = 1L;

		TaskTable(TableModel model){
            super(model);
            setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }
        
        public boolean getScrollableTracksViewportWidth() {
            return false;
        }

    }






}
