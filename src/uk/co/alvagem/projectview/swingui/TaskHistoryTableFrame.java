/*
 * TaskTableFrame.java
 * Project: ProjectView
 * Created on 13 Jan 2008
 *
 */
package uk.co.alvagem.projectview.swingui;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import uk.co.alvagem.projectview.model.Task;

public class TaskHistoryTableFrame extends JInternalFrame {

    /**  */
    private static final long serialVersionUID = 1L;
    
    //private final static String MENU_CONFIG = "/TaskTable/menus";
    
    private TaskHistoryTableModel model;
    //private JMenuBar menuBar;


    public TaskHistoryTableFrame(Task task) {
        super(task.getName());
        
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        
        setLocation(100,100);
        setSize(300,200);

       

        model = new TaskHistoryTableModel(task);
        JTable table = new TaskTable(model);
        JScrollPane scroll = new JScrollPane(table);
        getContentPane().add(scroll);
        
    }
    
    

    private static class TaskTable extends JTable {
        
        TaskTable(TableModel model){
            super(model);
            setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }
        
        public boolean getScrollableTracksViewportWidth() {
            return false;
        }

    }






}
