/*
 * RubberBand.java
 * Created on 13-Jun-2004
 * By Bruce.Porteous
 *
 */
package uk.co.alvagem.projectview.gantt;

import java.awt.Color;
import java.awt.Graphics;


/**
 * RubberBand provides rubber-band drawing of a line for 
 * dragging connections between symbols.
 * @author Bruce.Porteous
 *
 */
public class RubberBand{
	private int lastX = 0;
	private int lastY = 0;
	private int lastXStart = 0;
	private int lastYStart = 0;
	private boolean bandDrawn = false;
	
	public boolean isDrawn() {
		return bandDrawn;
	}
	
	public void drawBand(Graphics g, int x0, int y0, int x1, int y1){
		g.setXORMode(Color.white);
			
		if(bandDrawn){
			g.drawLine(lastXStart, lastYStart, lastX, lastY);
		}
		g.drawLine(x0,y0,x1,y1);
		bandDrawn = true;
		
		lastXStart = x0;
		lastYStart = y0;
		lastX = x1;
		lastY = y1;
			
		g.setPaintMode();
	}
	
	public void clearBand(Graphics g){
		if(bandDrawn){
			g.setXORMode(Color.white);
			g.drawLine(lastXStart, lastYStart, lastX, lastY);
			g.setPaintMode();
			bandDrawn = false;
		}
	}
}