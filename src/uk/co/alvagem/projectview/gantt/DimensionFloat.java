/*
 * DimensionFloat.java
 * Project: EATool
 * Created on 21-Feb-2006
 *
 */
package uk.co.alvagem.projectview.gantt;

/**
 * DimensionFloat is an analogue for Dimension2D but is a concrete class
 * that stores its dimension as float.
 * 
 * @author rbp28668
 */
public class DimensionFloat {

    private float width;
    private float height;
    
    /**
     * 
     */
    public DimensionFloat() {
        this(0.0f,0.0f);
    }
    
    public DimensionFloat(float width, float height){
        this.width = width;
        this.height = height;
    }

    public void set(float width, float height){
        this.width = width;
        this.height = height;
    }
    
    /**
     * @return Returns the height.
     */
    public float getHeight() {
        return height;
    }
    /**
     * @param height The height to set.
     */
    public void setHeight(float height) {
        this.height = height;
    }
    /**
     * @return Returns the width.
     */
    public float getWidth() {
        return width;
    }
    /**
     * @param width The width to set.
     */
    public void setWidth(float width) {
        this.width = width;
    }
}
