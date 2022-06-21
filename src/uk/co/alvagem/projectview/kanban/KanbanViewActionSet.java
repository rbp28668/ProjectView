/**
 * 
 */
package uk.co.alvagem.projectview.kanban;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import uk.co.alvagem.database.DAOFactory;
import uk.co.alvagem.projectview.dao.TaskDAO;
import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.projectview.swingui.Main;
import uk.co.alvagem.projectview.swingui.TaskEditor;
import uk.co.alvagem.swingui.ActionSet;
import uk.co.alvagem.swingui.ExceptionDisplay;

/**
 * @author bruce_porteous
 *
 */
public class KanbanViewActionSet extends ActionSet {

	private Main app;
	private Task rootTask;
	private KanbanView view;
	
	/**
	 * @param app 
	 * 
	 */
	public KanbanViewActionSet(Main app, KanbanView view) {
		this.app = app;
		this.view = view;
        addAction("NewTask", actionNewTask);
	}

	void setRootTask(Task rootTask) {
		this.rootTask = rootTask;
	}

	private void showException(Throwable t){
        new ExceptionDisplay(view, app.getAppTitle(),t);
    }
    

    private final Action actionNewTask = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {

                Task task = new Task();
                TaskEditor editor = new TaskEditor(view,task);
                editor.setVisible(true);
                if(editor.wasEdited()){
                    task.commitHistory();
                    rootTask.getSubTasks().add(task);
                    task.setParent(rootTask);
                    
                    DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                    factory.beginTransaction();
                    TaskDAO dao = factory.getTaskDAO();
                    try {
                        dao.makePersistent(task);
                        dao.makePersistent(rootTask);
                        factory.commit();
                    } catch (Throwable t) {
                        factory.rollback();
                        throw t;
                    }
                    
                    view.addTaskToBoard(task);
                }
                
            } catch(Throwable t) {
                showException(t);
            }
        }
    };

}
