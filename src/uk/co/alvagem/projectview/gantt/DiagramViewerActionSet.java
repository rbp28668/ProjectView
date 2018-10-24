/*
 * DiagramViewerActionSet.java
 * Project: EATool
 * Created on 17-Jan-2007
 *
 */
package uk.co.alvagem.projectview.gantt;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import uk.co.alvagem.projectview.swingui.Main;
import uk.co.alvagem.swingui.ActionSet;
import uk.co.alvagem.swingui.ExceptionDisplay;
import uk.co.alvagem.util.SettingsManager;

import com.sun.corba.se.spi.activation.Repository;


/**
 * DiagramViewerActionSet
 * 
 * @author rbp28668
 */
public class DiagramViewerActionSet extends ActionSet {

	private DiagramViewer viewer;
	private SettingsManager settings;
	private Main app;
	
    /**
     * 
     */
    public DiagramViewerActionSet(DiagramViewer viewer, Main app) {
        super();

        
		if(viewer == null){
			throw new NullPointerException("Passing null viewer to diagram viewer action set");
		}
		this.app = app;
		this.viewer = viewer;
		this.settings = app.getConfig();
		
		// File
		addAction("FileExportJPG", actionFileEditJPG);
		addAction("FileExportPNG", actionFileEditPNG);
		
		// Edit
		addAction("EditBackground", actionEditBackground);
		
		// Window
		addAction("WindowZoom10", actionWindowZoom10); 
		addAction("WindowZoom25", actionWindowZoom25); 
		addAction("WindowZoom50", actionWindowZoom50); 
		addAction("WindowZoom100", actionWindowZoom100); 
		addAction("WindowZoom200", actionWindowZoom200); 
		addAction("WindowZoom400", actionWindowZoom400); 
		addAction("WindowZoomIn", actionWindowZoomIn); 
		addAction("WindowZoomOut", actionWindowZoomOut); 
		addAction("WindowFit", actionWindowFit);

    }
    
	/**
	 * Get the DiagramViewer (or its subclass) that these actions will
	 * control.
	 * @return the attached DiagramViewer.
	 */
	protected DiagramViewer getViewer(){
	    return viewer;
	}

	/**
	 * @return
	 */
	protected Main getApp(){
		return app;
	}
	
	/**
     * ImageFileFilter provides a simple file filter that only accepts files
     * with the given suffix
     * 
     * @author Bruce.Porteous
     *  
     */
	private class ImageFileFilter extends FileFilter {
		private String suffix;
		private String description;
		
		/**
		 * Constructor for the filter.
		 */
		ImageFileFilter(String suffix, String description) {
			super();
			assert(suffix != null);
			assert(description != null);
			
			if(suffix.startsWith(".")){
				this.suffix = suffix;
			} else {
				this.suffix = "." + suffix;
			}
			this.description = description;
		}
        
		/* (non-Javadoc)
		 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
		 * Accepts .xml files
		 */
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().toLowerCase().endsWith(suffix);
		}
        
		/* (non-Javadoc)
		 * @see javax.swing.filechooser.FileFilter#getDescription()
		 */
		public String getDescription() {
			return description;
		}
	}

	/** File Export JPG action */
	private final Action actionFileEditJPG = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
				String path = getImageExportPath("jpg","JPEG Files");
				if(path != null){
					exportImage(path, "jpg");
				}
			} catch(Exception ex) {
				new ExceptionDisplay(viewer, app.getAppTitle(),ex);
			}
		}
	};   

	/** File Export PNG action */
	private final Action actionFileEditPNG = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
				String path = getImageExportPath("png","PNG Files");
				if(path != null){
					exportImage(path, "png");
				}
			} catch(Exception ex) {
				new ExceptionDisplay(viewer, app.getAppTitle(),ex);
			}
		}
	};   

	private String getImageExportPath(String format, String description ){
		SettingsManager.Element cfg = settings.getOrCreateElement("/Files/ImageExport" + format);
		String path = cfg.attribute("path");
 
		JFileChooser chooser = new JFileChooser();
		if(path == null) 
			chooser.setCurrentDirectory( new File("."));
		else
			chooser.setSelectedFile(new File(path));
		chooser.setFileFilter( new ImageFileFilter(format, description));

		if( chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			path = chooser.getSelectedFile().getPath();
			cfg.setAttribute("path",path);
		} else {
			path = null;
		}
		return path;
	}

	private void exportImage(String path, String format) throws IOException{
		assert(path != null);
		assert(format != null);
		assert(format.equals("png") || format.equals("jpg"));
		
		Diagram diagram = viewer.getDiagram();
		diagram.export(new File(path),format);
	}

	



	/** Edit Background action */
	private final Action actionEditBackground = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
				Color colour = JColorChooser.showDialog(viewer,"Background Colour", viewer.getBackgroundColour());
				if(colour != null){
					viewer.setBackgroundColour(colour);
				}
				
			} catch(Exception ex) {
				new ExceptionDisplay(viewer, app.getAppTitle(),ex);
			}
		}
	};   

	

	/** Window Zoom 10% action */
	private final Action actionWindowZoom10 = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
				viewer.setZoom(0.1f);
			} catch(Exception ex) {
				new ExceptionDisplay(viewer, app.getAppTitle(),ex);
			}
		}
	};   

	/** Window Zoom 25% action */
	private final Action actionWindowZoom25 = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
				viewer.setZoom(0.25f);
			} catch(Exception ex) {
				new ExceptionDisplay(viewer, app.getAppTitle(),ex);
			}
		}
	};   

	/** Window Zoom 50% action */
	private final Action actionWindowZoom50 = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
				viewer.setZoom(0.5f);
			} catch(Exception ex) {
				new ExceptionDisplay(viewer, app.getAppTitle(),ex);
			}
		}
	};   

	/** Window Zoom 100% action */
	private final Action actionWindowZoom100 = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
				viewer.setZoom(1.0f);
			} catch(Exception ex) {
				new ExceptionDisplay(viewer, app.getAppTitle(),ex);
			}
		}
	};   

	/** Window Zoom 200% action */
	private final Action actionWindowZoom200 = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
				viewer.setZoom(2.0f);
			} catch(Exception ex) {
				new ExceptionDisplay(viewer, app.getAppTitle(),ex);
			}
		}
	};   

	/** Window Zoom 400% action */
	private final Action actionWindowZoom400 = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
				viewer.setZoom(4.0f);
			} catch(Exception ex) {
				new ExceptionDisplay(viewer, app.getAppTitle(),ex);
			}
		}
	};   

	
	/** Window Zoom In action */
	private final Action actionWindowZoomIn = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
				viewer.setZoom(1.2f * viewer.getZoom());
			} catch(Exception ex) {
				new ExceptionDisplay(viewer, app.getAppTitle(),ex);
			}
		}
	};   
	
	/** Window Zoom  out action */
	private final Action actionWindowZoomOut = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
				viewer.setZoom(viewer.getZoom() / 1.2f );
			} catch(Exception ex) {
				new ExceptionDisplay(viewer, app.getAppTitle(),ex);
			}
		}
	};   

	/** Window Fit action */
	private final Action actionWindowFit = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
				viewer.fitDiagramToWindow();				
			} catch(Exception ex) {
				new ExceptionDisplay(viewer, app.getAppTitle(),ex);
			}
		}
	};   
	


}
