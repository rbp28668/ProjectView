/*
 * TaskTableActionSet.java
 * Project: ProjectView
 * Created on 18 Jan 2008
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.table.TableModel;

import uk.co.alvagem.projectview.core.filters.TaskFilter;
import uk.co.alvagem.swingui.ActionSet;
import uk.co.alvagem.swingui.ExceptionDisplay;
import uk.co.alvagem.swingui.TableModel2CSV;
import uk.co.alvagem.util.SettingsManager;

public class TaskTableActionSet extends ActionSet {

    private Main app;
    private TaskTableFrame frame;
    
    TaskTableActionSet(Main app, TaskTableFrame frame){
        this.app = app;
        this.frame = frame;
        
        addAction("ExportCSV", actionExportCSV);
        addAction("SelectFields", actionSelectFields);
        addAction("SetFilter", actionSetFilter);
        addAction("ClearFilter", actionClearFilter);
    }
    
    private void showException(Throwable t){
        new ExceptionDisplay(frame, app.getAppTitle(),t);
    }
    
    private final Action actionExportCSV= new AbstractAction() { 
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                SettingsManager.Element cfg = app.getSettings().getOrCreateElement("/Files/CSVExportPath");
                String path = cfg.attribute("path");
 
                JFileChooser chooser = new JFileChooser();
                if(path == null) 
                    chooser.setCurrentDirectory( new File("."));
                else
                    chooser.setSelectedFile(new File(path));

                if( chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    path = chooser.getSelectedFile().getPath();
                    cfg.setAttribute("path",path);
                    
                    TableModel model = frame.getModel();
                    TableModel2CSV export = new TableModel2CSV(model);
                    export.export(path);
                }
 
             } catch(Throwable t) {
                showException(t);
            }
        }
    };

    private final Action actionSelectFields= new AbstractAction() { 
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                TaskTableFieldsSelector dialog = new TaskTableFieldsSelector(frame, frame.getModel());
                dialog.setVisible(true);
             } catch(Throwable t) {
                showException(t);
            }
        }
    };

    private final Action actionSetFilter= new AbstractAction() { 
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                TaskFilter filter = frame.getModel().getFilter();
                TaskFilterEditor editor = new TaskFilterEditor(frame, "Task Filter", app, filter);
                editor.setVisible(true);
                if(editor.wasEdited()){
                    filter = editor.getEditedFilter();
                    frame.getModel().setFilter(filter);
                }
             } catch(Throwable t) {
                showException(t);
            }
        }
    };

    private final Action actionClearFilter= new AbstractAction() { 
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                frame.getModel().removeFilter();
             } catch(Throwable t) {
                showException(t);
            }
        }
    };


//    private final Action xactionExportCSV= new AbstractAction() { 
//        private static final long serialVersionUID = 1L;
//
//        public void actionPerformed(ActionEvent e) {
//            try {
// 
//             } catch(Throwable t) {
//                showException(t);
//            }
//        }
//    };

}
