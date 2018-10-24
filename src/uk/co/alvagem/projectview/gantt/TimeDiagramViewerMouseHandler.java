/*
 * TimeDiagramViewerMouseHandler.java
 * Project: EATool
 * Created on 27-Jan-2007
 *
 */
package uk.co.alvagem.projectview.gantt;

import java.awt.event.MouseEvent;

import javax.script.ScriptContext;
import javax.swing.event.MouseInputAdapter;

import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.projectview.swingui.Main;
import uk.co.alvagem.swingui.ActionSet;
import uk.co.alvagem.swingui.ExceptionDisplay;
import uk.co.alvagem.swingui.GUIBuilder;
import uk.co.alvagem.swingui.PositionalPopup;
import uk.co.alvagem.util.SettingsManager;


/**
 * TimeDiagramViewerMouseHandler
 * 
 * @author rbp28668
 */
public class TimeDiagramViewerMouseHandler extends MouseInputAdapter {

	private final TimeDiagramViewer viewer;
	private TimeBar selected = null;
    private SettingsManager.Element cfg;
    private Main app;

    /**
     * 
     */
    public TimeDiagramViewerMouseHandler(Main app, TimeDiagramViewer viewer) {
        super();
        this.viewer = viewer;
        this.app = app;
        cfg = app.getConfig().getElement("/TimeDiagramViewer/popups");

    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
		try{
			if(e.getClickCount() == 1 ) {	
			    if(e.getButton() == MouseEvent.BUTTON1) {
					if(e.isControlDown()){
					    // Ctrl-click - if on selected item then edit settings
					    // of selected item else add new item.
						
					} else if (e.isShiftDown()) {
					    // Shift-click
						TimeBar bar = getSelectedTimeBar(e);
						if(bar != null) {
							// DO something
						}
					} else if (e.isAltDown()) {
					    // Alt-click
					} else {
					    // Usual click
					}
					
			        
			    } else if(e.getButton() == MouseEvent.BUTTON3) {
					if(e.isControlDown()){
					    // Right-Ctrl-click
					} else if (e.isShiftDown()) {
					    // Right-Shift-click
					} else if (e.isAltDown()) {
					    // Right-Alt-click
					} else {
					    // Right-Usual click
				    
					    Task selected = null;
					    
					    TimeBar bar = getSelectedTimeBar(e);
					    if(bar != null) {
					        selected = bar.getTask();
					    }
					    
					    PositionalPopup popup = null;
		                if(selected != null) {
                            popup = getPopupFor(viewer, selected.getClass());
		                } else {
		                    popup = getBackgroundPopup(viewer);
		                }
		                
                        if(popup != null) {
                        	//System.out.println("Popup for " + strClass);
                            popup.setTargetPosition(e.getX(), e.getY());
                            popup.show(viewer, e.getX(), e.getY());
                        }
					}
			    }
			            
			}
			else if(e.getClickCount() == 2 ) {
				if(e.getButton() == MouseEvent.BUTTON1) {
					TimeBar bar = getSelectedTimeBar(e);
					if(bar != null) {
						// Double click on symbol
					    boolean wasEdited = editTask(viewer, bar.getTask());
						if(wasEdited){
							viewer.repaint();
						}
					} else {
						// Double click on background
						addBar(e);
					}
				}
			}
		}catch(Throwable t) {
			new ExceptionDisplay(viewer, app.getAppTitle(), t);
		}
    }
  
    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged(MouseEvent arg0) {
        // TODO Auto-generated method stub
        super.mouseDragged(arg0);
    }
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub
        super.mouseEntered(arg0);
    }
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub
        super.mouseExited(arg0);
    }
    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent arg0) {
        // TODO Auto-generated method stub
        super.mouseMoved(arg0);
    }
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub
        super.mousePressed(arg0);
    }
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub
        super.mouseReleased(arg0);
    }

    
	/**
	 * Gets the symbol at the current mouse position.
	 * 
	 * @param e is the MouseEvent to use for hit-testing
	 * @return if there is a symbol at the given mouse position then it
	 * is returned, otherwise null is returned.
	 */
	private TimeBar getSelectedTimeBar(MouseEvent e) {
		TimeBar selected = null;
		float zoom = viewer.getZoom();
		TimeDiagram diagram = (TimeDiagram)viewer.getDiagram();
		for(TimeBar item : diagram.getBars()){
			if(item.hitTest(e.getX(),e.getY(),zoom)){
				selected = item;
				break;
			}
		}
		return selected;
	}
    
	private PositionalPopup getPopupFor(TimeDiagramViewer viewer, Class<?> targetClass){
	    ActionSet actions = new TimeDiagramViewerActionSet(viewer, app);
        PositionalPopup popup = GUIBuilder.buildPopup(actions,cfg,targetClass);
        return popup;
	}
	
	private PositionalPopup getBackgroundPopup(TimeDiagramViewer viewer){
        ActionSet actions = new TimeDiagramViewerActionSet(viewer,app);
        PositionalPopup popup = GUIBuilder.buildPopup(actions,cfg,"Background");
       return popup;
	    
	}

	  /**
     * @param e
     */
    private void addBar(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param viewer2
     * @param item
     * @return
     */
    private boolean editTask(TimeDiagramViewer viewer2, Task task) {
        // TODO Auto-generated method stub
        return false;
    }
	
}
