/*
 * ProjectsExplorerActionSet.java
 * Project: ProjectView
 * Created on 29 Dec 2007
 *
 */
package uk.co.alvagem.projectview.swingui;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import uk.co.alvagem.database.DAOFactory;
import uk.co.alvagem.projectview.core.schedule.MonteCarloScheduleAnalysis;
import uk.co.alvagem.projectview.core.schedule.Scheduler;
import uk.co.alvagem.projectview.dao.AllocationDAO;
import uk.co.alvagem.projectview.dao.ConstraintDAO;
import uk.co.alvagem.projectview.dao.DependencyDAO;
import uk.co.alvagem.projectview.dao.ResourceDAO;
import uk.co.alvagem.projectview.dao.TaskDAO;
import uk.co.alvagem.projectview.gantt.TimeDiagram;
import uk.co.alvagem.projectview.gantt.TimeDiagramViewer;
import uk.co.alvagem.projectview.model.Allocation;
import uk.co.alvagem.projectview.model.Constraint;
import uk.co.alvagem.projectview.model.Dependency;
import uk.co.alvagem.projectview.model.Resource;
import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.swingui.ActionSet;
import uk.co.alvagem.swingui.ExceptionDisplay;

public class ProjectsExplorerActionSet extends ActionSet {

    private Main app;
    private ProjectsExplorer explorer;

    public ProjectsExplorerActionSet(Main app) {
        this.app = app;
        
        addAction("LookupProject", actionLookupProject);
        addAction("NewProject", actionNewProject);
        addAction("RemoveProject", actionRemoveProject);
        
        addAction("EditTask", actionEditTask);
        addAction("InsertTask", actionInsertTask);
        addAction("AddChildTask", actionAddChildTask);
        addAction("PushdownTask", actionPushdownTask);
        addAction("DeleteTask",actionDeleteTask); 

        addAction("PromoteTask", actionPromoteTask);
        addAction("DemoteTask", actionDemoteTask);
        addAction("MoveUpTask", actionMoveUpTask);
        addAction("MoveDownTask", actionMoveDownTask);
        
        addAction("TaskToTable", actionTaskToTable);
        
        addAction("LinkTasks",actionLinkTasks);
        addAction("UnlinkTasks",actionUnlinkTasks);
        
        addAction("EditDependency" ,actionEditDependency);
        addAction("RemoveDependency" ,actionRemoveDependency);
        
        addAction("AddConstraint" ,actionAddConstraint);
        addAction("EditConstraint" ,actionEditConstraint);
        addAction("RemoveConstraint" ,actionRemoveConstraint);
        
        addAction("AllocateResource",actionAllocateResource); 
        addAction("EditAllocation" ,actionEditAllocation);
        addAction("RemoveAllocation" ,actionRemoveAllocation);

        addAction("ScheduleTask", actionScheduleTask);
        addAction("DisplayGantt", actionDisplayGantt);
        addAction("MonteCarloSchedule",actionMonteCarloSchedule);
        addAction("ShowGraph", actionShowGraph);
        addAction("ShowHistory", actionShowHistory);
        addAction("ShowUncertaintyCurve", actionShowUncertaintyCurve);
        addAction("ShowResourceUtilisation", actionShowResourceUtilisation);
       
   }
   
    /**
     * Allows the explorer to be set after construction.
     * @param explorer
     */
    void setExplorer(ProjectsExplorer explorer){
        this.explorer = explorer;
    }
    
    private void showException(Throwable t){
        new ExceptionDisplay(explorer, app.getAppTitle(),t);
    }
    
    private Task selectTask(Collection<Task> tasks) {
        Task[] options = tasks.toArray(new Task[tasks.size()]);
        Task task = null;
        if(options.length == 1){
            task = options[0];
        } else if(options.length > 1) {
            Arrays.sort(options, new Comparator<Task>() {
                public int compare(Task t0, Task t1) {
                    return t0.getName().compareTo(t1.getName());
                }

            });
            task = (Task)JOptionPane.showInputDialog(
                explorer,  "Select Task", app.getAppName(),
                JOptionPane.QUESTION_MESSAGE, null,
                options, null
            );
        }
        return task;
    }

    private Constraint selectConstraint(Set<Constraint> constraints) {
        Constraint[] options = constraints.toArray(new Constraint[constraints.size()]);
        Constraint constraint = null;
        if(options.length == 1){
            constraint = options[0];
        } else if(options.length > 1){
            constraint = (Constraint)JOptionPane.showInputDialog(
                    explorer,  "Select Constraint", app.getAppName(),
                    JOptionPane.QUESTION_MESSAGE, null,
                    options, null
            );

        }
        return constraint;
    }

    private Dependency selectDependency(Collection<Dependency> dependencies) {
        Dependency[] options = dependencies.toArray(new Dependency[dependencies.size()]);
        Dependency dependency = null;
        if(options.length == 1){
            dependency = options[0];
        } else if(options.length > 1){
            dependency = (Dependency)JOptionPane.showInputDialog(
                    explorer,  "Select Dependency", app.getAppName(),
                    JOptionPane.QUESTION_MESSAGE, null,
                    options, null
            );

        }
        return dependency;
    }

    private Allocation selectAllocation(Set<Allocation> allocations) {
        Allocation[] options = allocations.toArray(new Allocation[allocations.size()]);
        Allocation allocation = null;
        if(options.length == 1){
            allocation = options[0];
        } else if(options.length > 1){
            allocation = (Allocation)JOptionPane.showInputDialog(
                    explorer,  "Select Resource Allocation", app.getAppName(),
                    JOptionPane.QUESTION_MESSAGE, null,
                    options, null
            );

        }
        return allocation;
    }
    
    private final Action actionLookupProject = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                factory.beginTransaction();
                TaskDAO dao = factory.getTaskDAO();
                List<Task> tasks;
                try {
                    tasks = dao.findTopLevelTasks();
                    factory.commit();
                } catch (Throwable t) {
                    factory.rollback();
                    throw t;
                }
    
                Set<Task>existing = explorer.getTreeModel().getTopLevelProjects();
                tasks.removeAll(existing);
                Task task =  selectTask(tasks);
                
                if(task != null){
                	factory.beginTransaction();
                	try {
                		dao.makePersistent(task);
                		factory.commit();
                	} catch(Exception x){
                		factory.rollback();
                		throw x;
                	}
                    explorer.getTreeModel().addTopLevelProject(task);
                }

              

            } catch(Throwable t) {
                showException(t);
            }
        }
    };   
    
    private final Action actionNewProject = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                Task task = new Task();
                TaskEditor editor = new TaskEditor(explorer,task);
                editor.setVisible(true);
                if(editor.wasEdited()){
                    task.commitHistory();
                    DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                    factory.beginTransaction();
                    TaskDAO dao = factory.getTaskDAO();
                    try {
                        dao.makePersistent(task);
                        factory.commit();
                    } catch (Throwable t) {
                        factory.rollback();
                        throw t;
                    }
                    
                    explorer.getTreeModel().addTopLevelProject(task);
                }
            } catch(Throwable t) {
                showException(t);
            }
        }
    };
    private final Action actionRemoveProject = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                Set<Task> tasks = explorer.getTreeModel().getTopLevelProjects();
                Task task = selectTask(tasks);
                if(task != null){
                    explorer.getTreeModel().removeTask(task);
                }
            } catch(Throwable t) {
                showException(t);
            }
        }
    };
    
    private final Action actionEditTask = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                Task task = explorer.getSelectedTask();
                explorer.editTask(explorer, task);

            } catch(Throwable t) {
                showException(t);
            }
        }
    };
    
    private final Action actionInsertTask = new AbstractAction() { 
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                Task predecessor = explorer.getSelectedTask();
                Task parent = predecessor.getParent();
                
                if(parent != null){
                    Task task = new Task();
                    TaskEditor editor = new TaskEditor(explorer,task);
                    editor.setVisible(true);
                    if(editor.wasEdited()){
                        task.commitHistory();
                        
                        int index = parent.getSubTasks().indexOf(predecessor);
                        parent.getSubTasks().add(index + 1,task);
                        task.setParent(parent);
                        
                        DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                        factory.beginTransaction();
                        TaskDAO dao = factory.getTaskDAO();
                        try {
                            dao.makePersistent(task);
                            dao.makePersistent(parent);
                            factory.commit();
                        } catch (Throwable t) {
                            factory.rollback();
                            throw t;
                        }
                        
                        explorer.getTreeModel().insertTaskAfter(predecessor, task);
                    }
                }
            } catch(Throwable t) {
                showException(t);
            }
        }
    };


    
    private final Action actionAddChildTask = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                Task parent = explorer.getSelectedTask();

                Task task = new Task();
                TaskEditor editor = new TaskEditor(explorer,task);
                editor.setVisible(true);
                if(editor.wasEdited()){
                    task.commitHistory();
                    parent.getSubTasks().add(task);
                    task.setParent(parent);
                    
                    DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                    factory.beginTransaction();
                    TaskDAO dao = factory.getTaskDAO();
                    try {
                        dao.makePersistent(task);
                        dao.makePersistent(parent);
                        factory.commit();
                    } catch (Throwable t) {
                        factory.rollback();
                        throw t;
                    }
                    
                    explorer.getTreeModel().addChildTask(parent, task);
                }
                
            } catch(Throwable t) {
                showException(t);
            }
        }
    };
    
    private final Action actionPushdownTask = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                Task selected = explorer.getSelectedTask();

                Task task = new Task();
                task.setName(selected.getName());
                task.setDescription(selected.getDescription());
                task.setNotes(selected.getNotes());
                
                task.setActualWork(selected.getActualWork());
                task.setFractionComplete(selected.getFractionComplete());
                task.setEstimatedEffort(selected.getEstimatedEffort());
                task.setEstimateSpread(selected.getEstimateSpread());
                task.setUncertaintyType(selected.getUncertaintyType());
                
                task.setStartDate(selected.getStartDate());
                task.setFinishDate(selected.getFinishDate());
                
                task.getConstraints().addAll(selected.getConstraints());
                selected.getConstraints().clear();
                
                task.commitHistory();
                selected.getSubTasks().add(task);
                task.setParent(selected);
                
                DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                factory.beginTransaction();
                TaskDAO dao = factory.getTaskDAO();
                try {
                    dao.makePersistent(task);
                    dao.makePersistent(selected);
                    factory.commit();
                } catch (Throwable t) {
                    factory.rollback();
                    throw t;
                }
                    
                explorer.getTreeModel().addChildTask(selected, task);
                
            } catch(Throwable t) {
                showException(t);
            }
        }
    };
    
    
private final Action actionTaskToTable= new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            Task task = explorer.getSelectedTask();
            DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
            
            TaskTableFrame frame;
            
            // Make sure task is initialised enough to be editable.
            factory.beginTransaction();
            TaskDAO dao = factory.getTaskDAO();
            try {
                dao.makePersistent(task);
                frame = new TaskTableFrame(task,app);
                factory.commit();
            } catch (Throwable t) {
                factory.rollback();
                throw t;
            }
            
            app.getCommandFrame().getDesktop().add(frame);
            frame.setVisible(true);

        } catch(Throwable t) {
            showException(t);
        }
    }
};

private final Action actionDeleteTask= new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            Task task = explorer.getSelectedTask();
            DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
            
            
            // Make sure task is initialised enough to be editable.
            factory.beginTransaction();
            TaskDAO dao = factory.getTaskDAO();
            try {
                dao.makePersistent(task);
                task.setActive(false);
                task.commitHistory();
                factory.commit();
            } catch (Throwable t) {
                factory.rollback();
                throw t;
            }

         } catch(Throwable t) {
            showException(t);
        }
    }
};

// Promote - the task becomes the successor of its immediate parent.
private final Action actionPromoteTask = new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            if(explorer.getSelectionCount() != 1){
                return;
            }

            Task task = explorer.getSelectedTask();
            Task parent = task.getParent();
            
            if(parent != null){
                Task parentParent = parent.getParent();
                if(parentParent != null){
                    // Where task will end up under parentParent.
                    int index = parentParent.getSubTasks().indexOf(parent) + 1;

                    
                    DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                    factory.beginTransaction();
                    TaskDAO dao = factory.getTaskDAO();
                    try {
                        dao.makePersistent(parent);
                        dao.makePersistent(parentParent);
                        dao.makePersistent(task);
                        
                        // Update model.
                        parent.getSubTasks().remove(task);
                        parentParent.getSubTasks().add(index, task);
                        task.setParent(parentParent);

                        // Update tree
                        int selectedRow = explorer.getLeadSelectionRow();
                        explorer.getTreeModel().removeTask(task);
                        explorer.getTreeModel().insertTaskAfter(parent, task);
                        explorer.setSelectionRow(selectedRow ); // keep selection on item moved.
                        
                        factory.commit();
                    } catch (Throwable t) {
                        factory.rollback();
                        throw t;
                    }

                }
            }
            
        } catch(Throwable t) {
            showException(t);
        }
    }
};



/**
 * Demote tasks - a task becomes the last child of its predecessor in the tree.
 */
private final Action actionDemoteTask = new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            if(explorer.getSelectionCount() != 1){
                return;
            }

            Task task = explorer.getSelectedTask();
            Task parent = task.getParent();
            
            if(parent != null){
                int index = parent.getSubTasks().indexOf(task);
                if(index > 0){

                    
                    DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                    factory.beginTransaction();
                    TaskDAO dao = factory.getTaskDAO();
                    try {
                        dao.makePersistent(parent);
                        
                        Task predecessor = parent.getSubTasks().get(index-1);
                        dao.makePersistent(predecessor);

                        // Update model.
                        parent.getSubTasks().remove(task);
                        predecessor.getSubTasks().add(task);
                        task.setParent(predecessor);
                        
                        // Update tree
                        int selectedRow = explorer.getLeadSelectionRow();
                        explorer.getTreeModel().removeTask(task);
                        explorer.getTreeModel().addChildTask(predecessor, task);
                        explorer.setSelectionRow(selectedRow ); // keep selection on item moved.
                        
                        factory.commit();
                    } catch (Throwable t) {
                        factory.rollback();
                        throw t;
                    }

                }
            }
            
        } catch(Throwable t) {
            showException(t);
        }
    }
};



private final Action actionMoveUpTask = new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            if(explorer.getSelectionCount() != 1){
                return;
            }
            
            Task task = explorer.getSelectedTask();
            Task parent = task.getParent();
            
            if(parent != null){
                int index = parent.getSubTasks().indexOf(task);
                if(index > 0){
                    Task predecessor = parent.getSubTasks().get(index-1);
                    parent.getSubTasks().set(index-1, task);
                    parent.getSubTasks().set(index, predecessor);
                    
                    DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                    factory.beginTransaction();
                    TaskDAO dao = factory.getTaskDAO();
                    try {
                        dao.makePersistent(parent);
                        factory.commit();
                    } catch (Throwable t) {
                        factory.rollback();
                        throw t;
                    }

                    int selectedRow = explorer.getLeadSelectionRow();
                    explorer.getTreeModel().swapAdjacentTasks(predecessor, task);
                    explorer.setSelectionRow(selectedRow - 1); // keep selection on item moved.
                }
            }
            
        } catch(Throwable t) {
            showException(t);
        }
    }
};



private final Action actionMoveDownTask = new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            if(explorer.getSelectionCount() != 1){
                return;
            }

            Task task = explorer.getSelectedTask();
            Task parent = task.getParent();
            
            if(parent != null){
                int index = parent.getSubTasks().indexOf(task);
                if(index < parent.getSubTasks().size()-1){

                    Task successor = parent.getSubTasks().get(index+1);
                    parent.getSubTasks().set(index+1, task);
                    parent.getSubTasks().set(index, successor);
                    
                    DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                    factory.beginTransaction();
                    TaskDAO dao = factory.getTaskDAO();
                    try {
                        dao.makePersistent(parent);
                        factory.commit();
                    } catch (Throwable t) {
                        factory.rollback();
                        throw t;
                    }

                    int selectedRow = explorer.getLeadSelectionRow();
                    explorer.getTreeModel().swapAdjacentTasks(task,successor);
                    explorer.setSelectionRow(selectedRow + 1); // keep selection on item moved.
                }
            }
        } catch(Throwable t) {
            showException(t);
        }
    }
};


private final Action actionLinkTasks = new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            if(explorer.getSelectionCount() > 1){
                List<DefaultMutableTreeNode> nodes = explorer.getSelectedNodes();
                Iterator<DefaultMutableTreeNode> iter =  nodes.iterator();
                DefaultMutableTreeNode node = iter.next();
                Task prevTask = (Task)node.getUserObject();
                
                DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                factory.beginTransaction();
                TaskDAO daoTask = factory.getTaskDAO();
                DependencyDAO daoDependency = factory.getDependencyDAO();
                try {
                    daoTask.makePersistent(prevTask);
                    while(iter.hasNext()){
                        node = iter.next();
                        Task task = (Task)node.getUserObject();
                        Dependency dependency = new Dependency();
                        dependency.setPredecessor(prevTask);
                        dependency.setSuccessor(task);
                        prevTask.getSuccessors().add(dependency);
                        task.getPredecessors().add(dependency);

                        daoDependency.makePersistent(dependency);
                        daoTask.makePersistent(task);
                        prevTask = task;
                    }

                    factory.commit();
                } catch (Throwable t) {
                    factory.rollback();
                    throw t;
                }
                
            }
         } catch(Throwable t) {
            showException(t);
        }
    }
};

private final Action actionUnlinkTasks = new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            if(explorer.getSelectionCount() > 1){
                List<DefaultMutableTreeNode> nodes = explorer.getSelectedNodes();
                
                DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                factory.beginTransaction();
                TaskDAO daoTask = factory.getTaskDAO();
                DependencyDAO daoDependency = factory.getDependencyDAO();
                Set<Dependency> predecessors = new HashSet<Dependency>();
                Set<Dependency> successors = new HashSet<Dependency>();
                
                try {
                    for(DefaultMutableTreeNode node: nodes){
                        Task task = (Task)node.getUserObject();
                        daoTask.makePersistent(task);
                        
                        predecessors.addAll(task.getPredecessors());
                        successors.addAll(task.getSuccessors());
                    }
                    // Only those dependencies that are in both sets link the
                    // selected tasks.
                    Set<Dependency> dependencies = new HashSet<Dependency>();
                    dependencies.addAll(predecessors);
                    dependencies.retainAll(successors);
                    
                    for(Dependency dependency : dependencies){
                        dependency.getPredecessor().getSuccessors().remove(dependency);
                        dependency.getSuccessor().getPredecessors().remove(dependency);
                        daoDependency.makeTransient(dependency);
                    }
                    factory.commit();
                    
                } catch (Throwable t) {
                    factory.rollback();
                    throw t;
                }
                
            }
         } catch(Throwable t) {
            showException(t);
        }
    }
};

private final Action actionEditDependency= new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            Task selected = explorer.getSelectedTask();

            DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
            TaskDAO daoTask = factory.getTaskDAO();

            factory.beginTransaction();
            Set<Dependency> predecessors;
            Set<Dependency> successors;
            try {
                daoTask.makePersistent(selected);
                predecessors = selected.getPredecessors();
                successors = selected.getSuccessors();
                predecessors.isEmpty(); // force loading.
                successors.isEmpty();
                factory.commit();
            } catch (Throwable t) {
                factory.rollback();
                throw t;
            }

            List<Dependency> dependencies = new LinkedList<Dependency>();
            dependencies.addAll(predecessors);
            dependencies.addAll(successors);
            
            Dependency dep = selectDependency(dependencies);
            
            if(dep != null){
                DependencyDAO daoDependency = factory.getDependencyDAO();
                factory.beginTransaction();
                try {
                    daoDependency.makePersistent(dep);
                    factory.commit();
                } catch (Throwable t) {
                    factory.rollback();
                    throw t;
                }
                // TODO edit dependency!
            }

         } catch(Throwable t) {
            showException(t);
        }
    }
};

private final Action actionRemoveDependency= new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            Task selected = explorer.getSelectedTask();

            DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
            TaskDAO daoTask = factory.getTaskDAO();

            factory.beginTransaction();
            Set<Dependency> predecessors;
            Set<Dependency> successors;
            try {
                daoTask.makePersistent(selected);
                predecessors = selected.getPredecessors();
                successors = selected.getSuccessors();
                factory.commit();
            } catch (Throwable t) {
                factory.rollback();
                throw t;
            }

            List<Dependency> dependencies = new LinkedList<Dependency>();
            dependencies.addAll(predecessors);
            dependencies.addAll(successors);
            
            Dependency dep = selectDependency(dependencies);
            
            if(dep != null){
                DependencyDAO daoDependency = factory.getDependencyDAO();
                
                factory.beginTransaction();
                try {
                    Task pred = dep.getPredecessor();
                    Task succ = dep.getPredecessor();
                    
                    daoTask.makePersistent(pred);
                    daoTask.makePersistent(succ);
                    
                    pred.getSuccessors().remove(dep);
                    succ.getPredecessors().remove(dep);
                    
                    daoDependency.makeTransient(dep);
                    factory.commit();
                } catch (Throwable t) {
                    factory.rollback();
                    throw t;
                }

            }

         } catch(Throwable t) {
            showException(t);
        }
    }
};

private final Action actionAddConstraint= new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            Task selected = explorer.getSelectedTask();
            Constraint constraint = new Constraint();
            ConstraintEditor editor = new ConstraintEditor(explorer, constraint);
            if(editor.wasEdited()){
                DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
                factory.beginTransaction();
                TaskDAO daoTask = factory.getTaskDAO();
                ConstraintDAO daoConstraint = factory.getConstraintDAO();
                try {
                    daoTask.makePersistent(selected);
                    daoConstraint.makePersistent(constraint);
                    
                    selected.getConstraints().add(constraint);
                    factory.commit();
                } catch (Throwable t) {
                    factory.rollback();
                    throw t;
                }

            }
         } catch(Throwable t) {
            showException(t);
        }
    }
};

private final Action actionEditConstraint= new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            Task selected = explorer.getSelectedTask();
            
            DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);

            factory.beginTransaction();
            Set<Constraint> constraints;
            try {
                TaskDAO daoTask = factory.getTaskDAO();
                daoTask.makePersistent(selected);
                constraints = selected.getConstraints();
                factory.commit();
            } catch (Throwable t) {
                factory.rollback();
                throw t;
            }

            Constraint constraint = selectConstraint(constraints);
            
            ConstraintEditor editor = new ConstraintEditor(explorer, constraint);
            if(editor.wasEdited()){
                factory.beginTransaction();
                try {
                    ConstraintDAO daoConstraint = factory.getConstraintDAO();
                    daoConstraint.makePersistent(constraint);
                    factory.commit();
                } catch (Throwable t) {
                    factory.rollback();
                    throw t;
                }

            }
         } catch(Throwable t) {
            showException(t);
        }
    }
};

private final Action actionRemoveConstraint= new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            Task selected = explorer.getSelectedTask();
            
            DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);

            factory.beginTransaction();
            Set<Constraint> constraints;
            try {
                TaskDAO daoTask = factory.getTaskDAO();
                daoTask.makePersistent(selected);
                constraints = selected.getConstraints();
                factory.commit();
            } catch (Throwable t) {
                factory.rollback();
                throw t;
            }

            Constraint constraint = selectConstraint(constraints);
            
            if(constraint != null){
                ConstraintDAO daoConstraint = factory.getConstraintDAO();
                factory.beginTransaction();
                try {
                    TaskDAO daoTask = factory.getTaskDAO();
                    daoTask.makePersistent(selected);
                    selected.getConstraints().remove(constraint);
                    daoConstraint.makeTransient(constraint);
                    factory.commit();
                } catch (Throwable t) {
                    factory.rollback();
                    throw t;
                }
            }

         } catch(Throwable t) {
            showException(t);
        }
    }

};


private final Action actionAllocateResource= new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            Task selected = explorer.getSelectedTask();
            
            DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);

            List<Resource> resources;
            AllocationEditor editor;
        	Allocation allocation = new Allocation();

            factory.beginTransaction();
        	try {
            	ResourceDAO daoResource = factory.getResourceDAO();
            	resources = daoResource.findAll();
            	
            	editor = new AllocationEditor(explorer,allocation,resources);
                factory.commit();
            } catch (Throwable t) {
                factory.rollback();
                throw t;
            }

            editor.setVisible(true);
            
            if(editor.wasEdited()){
                factory.beginTransaction();
                try {
                	TaskDAO daoTask = factory.getTaskDAO();
                    
                    ResourceDAO daoResource = factory.getResourceDAO();
                    daoResource.makePersistent(allocation.getResource());
                    
                    AllocationDAO daoAlloc = factory.getAllocationDAO();
                    addAllocation(selected,allocation,daoTask, daoAlloc);
                    factory.commit();
                } catch (Throwable t) {
                    factory.rollback();
                    throw t;
                }
            }
        	
         } catch(Throwable t) {
            showException(t);
        }
    }
};

/**
 * Adds a resource Allocation to a task or hierarchy of tasks.  Only leaf nodes can have
 * resources allocated to them so this recurses down the hierarchy. Any existing allocation
 * of the same resource is removed before the new allocation is added.  A copy of the 
 * allocation is added to each node - otherwise all the nodes will reference the same
 * allocation.
 * @param task
 * @param allocation
 * @param daoAlloc
 */
private void addAllocation(Task task, Allocation allocation, TaskDAO daoTask, AllocationDAO daoAlloc){
    daoTask.makePersistent(task);
    if(task.getSubTasks().isEmpty()){
        Set<Allocation> toRemove = new HashSet<Allocation>();
        for(Allocation existing : task.getAllocations()){
            if(existing.getResource().equals(allocation.getResource())){
                toRemove.add(existing);
            }
        }
        
        for(Allocation remove : toRemove){
            task.getAllocations().remove(remove);
            daoAlloc.makeTransient(remove);
        }
        
        Allocation copy = new Allocation();
        copy.setResource(allocation.getResource());
        copy.setUtilisation(allocation.getUtilisation());
        copy.setTask(task);
        daoAlloc.makePersistent(copy);
        task.getAllocations().add(copy);
    } else {
        for(Task child : task.getSubTasks()){
            addAllocation(child, allocation, daoTask,daoAlloc);
        }
    }
}

private final Action actionEditAllocation= new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            Task selected = explorer.getSelectedTask();
            
            DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);

            Set<Allocation>allocations;
            factory.beginTransaction();
            try {
                TaskDAO daoTask = factory.getTaskDAO();
                daoTask.makePersistent(selected);
                allocations = selected.getAllocations();
                for(Allocation allocation : allocations){
                    allocation.getResource().getName();
                }
                factory.commit();
            } catch (Throwable t) {
                factory.rollback();
                throw t;
            }

            Allocation allocation = selectAllocation(allocations);
            
            if(allocation != null){
                List<Resource> resources;
                AllocationEditor editor;
                
                factory.beginTransaction();
                try {
                    ResourceDAO daoResource = factory.getResourceDAO();
                    resources = daoResource.findAll();
                    
                    editor = new AllocationEditor(explorer,allocation,resources);
                    factory.commit();
                } catch (Throwable t) {
                    factory.rollback();
                    throw t;
                }
            
                editor.setVisible(true);
                
                if(editor.wasEdited()){
                    factory.beginTransaction();
                    try {
                        TaskDAO daoTask = factory.getTaskDAO();
                        daoTask.makePersistent(selected);
                        
                        AllocationDAO daoAlloc = factory.getAllocationDAO();
                        addAllocation(selected,allocation,daoTask, daoAlloc);
                        factory.commit();
                    } catch (Throwable t) {
                        factory.rollback();
                        throw t;
                    }
                }
            }
                
         } catch(Throwable t) {
            showException(t);
        }
    }
};

private final Action actionRemoveAllocation= new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            Task selected = explorer.getSelectedTask();
            
            DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);

            Set<Allocation>allocations;
            factory.beginTransaction();
            try {
                TaskDAO daoTask = factory.getTaskDAO();
                daoTask.makePersistent(selected);
                allocations = selected.getAllocations();
                for(Allocation allocation : allocations){
                    allocation.getResource().getName();
                }
                factory.commit();
            } catch (Throwable t) {
                factory.rollback();
                throw t;
            }

            Allocation allocation = selectAllocation(allocations);
            
            if(allocation != null){
                factory.beginTransaction();
                try {
                    AllocationDAO daoAlloc = factory.getAllocationDAO();
                    TaskDAO daoTask = factory.getTaskDAO();
                    daoTask.makePersistent(selected);
                    selected.getAllocations().remove(allocation);
                    daoAlloc.makeTransient(allocation);
                    factory.commit();
                } catch (Throwable t) {
                    factory.rollback();
                    throw t;
                }
            }

         } catch(Throwable t) {
            showException(t);
        }
    }
};

private final Action actionScheduleTask= new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            Task selected = explorer.getSelectedTask();
            
            DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);

            factory.beginTransaction();
            try {
	            TaskDAO daoTask = factory.getTaskDAO();
	            daoTask.makePersistent(selected);
	
	            Scheduler scheduler = new Scheduler();
	            scheduler.schedule(selected);
	            
	            selected.updateFromSubTree();
	            selected.fireChanged();
	            
	            factory.commit();
            } catch( Exception x) {
            	factory.rollback();
            	throw x;
            }
            
         } catch(Throwable t) {
            showException(t);
        }
    }
};

private final Action actionDisplayGantt= new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            Task selected = explorer.getSelectedTask();
            
            DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);

            TaskDAO daoTask = factory.getTaskDAO();
            daoTask.makePersistent(selected);

            TimeDiagram gantt = new TimeDiagram(selected);
            TimeDiagramViewer viewer = new TimeDiagramViewer(gantt,app);

            app.getCommandFrame().getDesktop().add(viewer);
            viewer.setVisible(true);
            
         } catch(Throwable t) {
            showException(t);
        }
    }
};
private final Action actionMonteCarloSchedule= new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            Task selected = explorer.getSelectedTask();
            MonteCarloScheduleAnalysis analysis = new MonteCarloScheduleAnalysis();
            
            DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
            factory.beginTransaction();
            try {
                TaskDAO daoTask = factory.getTaskDAO();
                
                daoTask.makePersistent(selected);
                analysis.schedule(selected, 100);	// start with 100!
            	
            } finally {
            	factory.commit();
            }
            
            float[] estimates = analysis.getElapsed();
            float[] effort = analysis.getEffort();
            
            MonteCarloSchedulingGraph graph = new MonteCarloSchedulingGraph(selected, estimates,effort );
            app.getCommandFrame().getDesktop().add(graph);
            graph.setVisible(true);
            
         } catch(Throwable t) {
            showException(t);
        }
    }
};

private final Action actionShowGraph= new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            Task selected = explorer.getSelectedTask();

            TaskHistoryGraph graph;
            
            DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
            factory.beginTransaction();
            try {
                TaskDAO daoTask = factory.getTaskDAO();
                
                daoTask.makePersistent(selected);
                graph = new TaskHistoryGraph(selected);
            	
            } finally {
            	factory.commit();
            }
            
            app.getCommandFrame().getDesktop().add(graph);
            graph.setVisible(true);
            
         } catch(Throwable t) {
            showException(t);
        }
    }
};

private final Action actionShowHistory = new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            Task selected = explorer.getSelectedTask();

            TaskHistoryTableFrame frame;
            DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
            factory.beginTransaction();
            try {
                TaskDAO daoTask = factory.getTaskDAO();
                
                daoTask.makePersistent(selected);
                frame = new TaskHistoryTableFrame(selected);
            	
            } finally {
            	factory.commit();
            }
            
            app.getCommandFrame().getDesktop().add(frame);
            frame.setVisible(true);
            
         } catch(Throwable t) {
            showException(t);
        }
    }
};

/** Shows the graph of uncertainty of a task.  Note this only applies
 * to leaf nodes - others need to be scheduled multiple times.
 */
private final Action actionShowUncertaintyCurve = new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            Task selected = explorer.getSelectedTask();

            UncertaintyGraph frame;
            DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);
            factory.beginTransaction();
            try {
                TaskDAO daoTask = factory.getTaskDAO();
                
                daoTask.makePersistent(selected);
                frame = new UncertaintyGraph(selected);
            	
            } finally {
            	factory.commit();
            }
            
            app.getCommandFrame().getDesktop().add(frame);
            frame.setVisible(true);
            
         } catch(Throwable t) {
            showException(t);
        }
    }
};

private final Action actionShowResourceUtilisation= new AbstractAction() { 
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        try {
            Task selected = explorer.getSelectedTask();
            
            DAOFactory factory = DAOFactory.instance(DAOFactory.HIBERNATE);

            factory.beginTransaction();
            try {
	            TaskDAO daoTask = factory.getTaskDAO();
	            daoTask.makePersistent(selected);
	
	            Scheduler scheduler = new Scheduler();
	            scheduler.schedule(selected);
	            
	            factory.commit();
	            
	            ResourceScheduleGraph frame = new ResourceScheduleGraph(selected.getName(), scheduler.getScheduledResources());
	            app.getCommandFrame().getDesktop().add(frame);
	            frame.setVisible(true);
	            
	            
            } catch( Exception x) {
            	factory.rollback();
            	throw x;
            }
            
         } catch(Throwable t) {
            showException(t);
        }
    }
};


}
