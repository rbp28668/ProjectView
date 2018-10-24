/*
 * DiagramViewer.java
 * Project: EATool
 * Created on 24-Nov-2006
 *
 */
package uk.co.alvagem.projectview.gantt;

import java.awt.Color;

import javax.swing.JInternalFrame;

/**
 * DiagramViewer is the abstract base class for all types of diagram viewer. It describes the
 * absolute minimum functionality that any diagram type may want.  Note that no assumptions are made
 * as to the content of the diagram.
 * 
 * @author rbp28668
 */
public abstract class DiagramViewer extends JInternalFrame {

    private Diagram diagram;

    
    /**
     * @param title
     */
    public DiagramViewer(String title, Diagram diagram) {
        super(title);
        this.diagram = diagram;
    }

    public Diagram getDiagram() {
        return diagram;
    }
    
	public Color getBackgroundColour(){
	    return diagram.getBackgroundColour();
	}
	
	public void setBackgroundColour(Color background){
	    diagram.setBackgroundColour(background);
	    refresh();
	}

    /**
	 * 
	 */
	public abstract void refresh();

	public abstract void setZoom(float zoom);
	
	public abstract float getZoom();
	
	public abstract void fitDiagramToWindow();

}
