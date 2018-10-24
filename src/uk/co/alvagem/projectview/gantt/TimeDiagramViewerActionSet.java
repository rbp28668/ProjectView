/*
 * TimeDiagramViewerActionSet.java
 * Project: EATool
 * Created on 26-Oct-2006
 *
 */
package uk.co.alvagem.projectview.gantt;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import uk.co.alvagem.projectview.swingui.Main;
import uk.co.alvagem.swingui.ExceptionDisplay;

/**
 * TimeDiagramViewerActionSet is the set of actions for a TimeDiagramViewer.
 * 
 * @author rbp28668
 */
public class TimeDiagramViewerActionSet extends DiagramViewerActionSet {


    /**
     * 
     */
    public TimeDiagramViewerActionSet(TimeDiagramViewer viewer, Main app) {
        super(viewer,app);
		addAction("ScaleDays", actionScaleDays); 
		addAction("ScaleWeeks", actionScaleWeeks); 
		addAction("ScaleMonths", actionScaleMonths); 
		addAction("ScaleYears", actionScaleYears); 

    }


	private final Action actionScaleDays = new AbstractAction() {
        
        private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
		    
			try {
		        TimeDiagramViewer viewer = (TimeDiagramViewer)getViewer();
			    TimeDiagram diagram = (TimeDiagram)viewer.getDiagram();
			    diagram.setTimeAxis(TimeDiagram.SCALE_DAY);
			    viewer.repaint();
			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(), getApp().getAppTitle(),ex);
			}
		    
		}
	};

	private final Action actionScaleWeeks = new AbstractAction() {
        
        private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
		    
			try {
		        TimeDiagramViewer viewer = (TimeDiagramViewer)getViewer();
			    TimeDiagram diagram = (TimeDiagram)viewer.getDiagram();
			    diagram.setTimeAxis(TimeDiagram.SCALE_WEEK);
			    viewer.repaint();
			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(), getApp().getAppTitle(),ex);
			}
		    
		}
	};

	private final Action actionScaleMonths = new AbstractAction() {
        
        private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
		    
			try {
		        TimeDiagramViewer viewer = (TimeDiagramViewer)getViewer();
			    TimeDiagram diagram = (TimeDiagram)viewer.getDiagram();
			    diagram.setTimeAxis(TimeDiagram.SCALE_MONTH);
			    viewer.repaint();
			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(), getApp().getAppTitle(),ex);
			}
		    
		}
	};

	private final Action actionScaleYears = new AbstractAction() {
        
        private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
		    
			try {
		        TimeDiagramViewer viewer = (TimeDiagramViewer)getViewer();
			    TimeDiagram diagram = (TimeDiagram)viewer.getDiagram();
			    diagram.setTimeAxis(TimeDiagram.SCALE_YEAR);
			    viewer.repaint();
			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(), getApp().getAppTitle(),ex);
			}
		    
		}
	};

	

}
