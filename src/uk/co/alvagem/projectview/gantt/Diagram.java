/*
 * Diagram.java
 * Project: EATool
 * Created on 20-Aug-2006
 *
 */
package uk.co.alvagem.projectview.gantt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Diagram
 * 
 * @author rbp28668
 */
public abstract class Diagram  {


	private Color backColour = Color.lightGray;
	private List diagramsListeners = new LinkedList();
	private List diagramListeners = new LinkedList();
    private boolean mustDoLayout = false;

	

	public Diagram() {
	}
   
	
	
    /**
	 * Allows automatic layout to be deferred until the next draw.
	 */
	public void deferLayout(boolean defer){
    	mustDoLayout = defer;
    }
	
	   /**
     * @return
     */
    public boolean isLayoutDeferred() {
        return mustDoLayout;
        
    }


	public Color getBackgroundColour(){
		return backColour;
	}
	
	public void setBackgroundColour(Color colour){
		backColour = colour;
	}
	
	public abstract void draw(Graphics2D g, float zoom);

	/**
	 * Get the bounds for a zoom of 1.0
	 * @return bounds.
	 */
	public Rectangle2D.Float getBounds() {
		return getBounds(1.0f);
	}
	
	/**
	 * Get the bounds for a given zoom factor.
	 * @param zoom is the zoom factor to get bounds for.
	 * @return Rectangle with the bounds
	 */
	public abstract Rectangle2D.Float getBounds(float zoom);

	/**
	 * Removes the content of the diagram and sets any diagram attributes to their defaults. 
	 */
	public abstract void reset();

    /**
     * Sets the colours and any other properties of the diagram to their
     * default values (possibly determined by the diagram type).
     */
    public abstract void resetPropertiesToDefaults();

 	
    /**
     * Writes the diagram to an image file.
     * @param path is the path for the output file.
     * @param format should be png or jpg.
     * @throws IOException
     */
    public void export(File path, String format) throws IOException{
        final int BORDER = 10;
		Rectangle2D.Float bounds = getBounds();
		int width = (int)bounds.width;
		int height = (int)bounds.height;
		width += 2*BORDER;
		height += 2*BORDER;
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(getBackgroundColour());
		graphics.fillRect(0,0,width,height);
		
		graphics.translate(BORDER,BORDER);
		draw(graphics,1.0f);
		ImageIO.write(image, format, path);

    }
	

}
