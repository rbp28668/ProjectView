/*
 * TimeBar.java
 * Project: EATool
 * Created on 02-Jan-2007
 *
 */
package uk.co.alvagem.projectview.gantt;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.alvagem.projectview.model.Task;

/**
 * TimeBar
 * 
 * @author rbp28668
 */
public class TimeBar{
    private Task task;
    private boolean isSummary;
    private float x;
    private float y;
	private DimensionFloat size = new DimensionFloat(50,20);
    private transient boolean isSelected = false;
    
    /**
     * 
     */
    public TimeBar(Task task) {
        super();
        this.task = task;
        this.isSummary = !task.getSubTasks().isEmpty();
    }
    
	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.GraphicalObject#setSize(float, float)
	 */
	public void setSize(float width, float height){
		size.set(width,height);
	}

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#setPosition(float, float)
     */
    public void setPosition(float px, float py) {
        x = px;
        y = py;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#getX()
     */
    public float getX() {
        return x;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#getY()
     */
    public float getY() {
        return y;
    }
    
    /**
     * @return
     */
    public Date getStartDate(){
        return task.getStartDate();
    }
    
    /**
     * @return
     */
    public Date getFinishDate(){
        return task.getFinishDate();
    }

    /**
     * @return
     */
    public Task getTask(){
    	return task;
    }

    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#getBounds()
     */
    public Float getBounds() {
        Rectangle2D.Float bounds = new Rectangle2D.Float();
        bounds.height = size.getHeight();
        bounds.width = size.getWidth();
        bounds.y = y - bounds.height/2;
        bounds.x = x - bounds.width/2;
        return bounds;
    }

 
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#draw(java.awt.Graphics2D, float)
     */
    public void draw(Graphics2D g, float scale) {
        
    	float width = size.getWidth();
        float height = size.getHeight();
        float px = x - width / 2;
        float py = y - height/2;
        
        //System.out.println("Draw bar " + x + ',' + y + ',' + width + ',' + height);
         
        int x0 = (int)px;
        int y0 = (int)py;
        int w = (int)(width);
        int h = (int)(height);

        if(isSummary){
            g.setColor(Color.BLACK);
            h /= 2;
        } else {
        	g.setColor(Color.BLUE);
//        	DateFormat df = SimpleDateFormat.getDateTimeInstance();
//        	System.out.println(task.getName() + width + ":" + df.format(task.getStartDate()) + ":" + df.format(task.getFinishDate()));
        }
        g.fillRect(x0,y0,w,h);
        
//        if(true || isSelected){
//            g.setColor(Color.black);
//            g.drawRect(x0,y0,w,h);
//            
//        }
    }

    /**
     * @param g
     * @param scale
     */
    public void drawCaption(Graphics2D g, Font font, float scale) {
        float height = size.getHeight();
        
        float px = 0;
        float py = y + height/2;
        
        g.setColor(Color.black);
        g.setFont(font);
        g.drawString(getCaption(),px,py);
    }

    /**
     * Gets the caption that will be used to describe the attached property.
     * @return the caption String.
     */
    public String getCaption() {
    	return task.getName();
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#sizeWith(java.awt.Graphics2D)
     */
    public void sizeWith(Graphics2D g) {
        // Leave as a NOP as Time Diagram sizes the bars
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#drawCollateral(java.awt.Graphics2D, float)
     */
    public void drawCollateral(Graphics2D g, float scale) {
        // NOP - no handles.
        
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#getBounds(float)
     */
    public Float getBounds(float zoom) {
        Rectangle2D.Float zoomedBounds = new Rectangle2D.Float();

        zoomedBounds.height = zoom * size.getHeight();
        zoomedBounds.width = zoom * size.getWidth();
        zoomedBounds.y = zoom * (y - zoomedBounds.height/2);
        zoomedBounds.x = zoom * (x - zoomedBounds.width/2);

        return zoomedBounds;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#getExtendedBounds(float)
     */
    public Float getExtendedBounds(float zoom) {
        return getBounds(zoom);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#hitTest(int, int, float)
     */
    public boolean hitTest(int mx, int my, float zoom) {
        Rectangle2D.Float bounds = getBounds(zoom);
        return bounds.contains(mx,my);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#onSelect(int, int, float)
     */
    public void onSelect(int mx, int my, float zoom) {
        isSelected = true;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#clearSelect()
     */
    public void clearSelect() {
        isSelected = false;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#onDrag(int, int, float)
     */
    public void onDrag(int mx, int my, float zoom) {
        // NOP
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.GraphicalObject#isSelected()
     */
    public boolean isSelected() {
        return isSelected;
    }


}
