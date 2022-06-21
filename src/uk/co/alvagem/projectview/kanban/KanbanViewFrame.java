/**
 * 
 */
package uk.co.alvagem.projectview.kanban;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.projectview.swingui.Main;
import uk.co.alvagem.swingui.GUIBuilder;
import uk.co.alvagem.util.SettingsManager;
import uk.co.alvagem.util.SettingsManager.Element;

/**
 * @author bruce_porteous
 *
 */
public class KanbanViewFrame extends JInternalFrame {

    
	private static final long serialVersionUID = 1L;
	private final static String SETTINGS_KEY = "/Windows/KanbanView";
    private SettingsManager settings;
    private KanbanView view;
    private KanbanViewActionSet actions;
	/**
	 * 
	 */
	public KanbanViewFrame(Main app) {

		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);

		// default with magic numbers
		setSize(500,300);   // magic
		setLocation(20,20); // magic
		
		
        settings = app.getSettings();
        GUIBuilder.loadBounds(this, settings, SETTINGS_KEY);
        
        setLayout(new BorderLayout());
 
        view = new KanbanView(app);
        
        actions = new KanbanViewActionSet(app, view);
        JToolBar toolbar = new JToolBar();
        Element cfg = app.getConfig().getOrCreateElement("/KanbanView/toolbar");
        if(cfg.getChildCount() > 0){
            GUIBuilder.buildToolbar(toolbar, actions, cfg);
            getContentPane().add(toolbar, java.awt.BorderLayout.NORTH);
        }
        
        JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setViewportView(view);
        getContentPane().add(scrollPane,java.awt.BorderLayout.CENTER);
        setVisible(true);
	}
	
    public void dispose() {
        GUIBuilder.saveBounds(this, settings, SETTINGS_KEY);
        view.dispose();
        view = null;
    }


	/**
	 * Setting the root task sets the root of the tree. All the tasks under the root are displayed
	 * on the board and any new tasks are added as children of this task.
	 * @param rootTask
	 */
	public void setRootTask(Task rootTask) {
		setTitle(rootTask.getName());
		view.setRootTask(rootTask);
		actions.setRootTask(rootTask);
	}

}
