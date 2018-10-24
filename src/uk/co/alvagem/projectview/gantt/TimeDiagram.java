/*
 * TimeDiagram.java
 * Project: EATool
 * Created on 26-Oct-2006
 *
 */
package uk.co.alvagem.projectview.gantt;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uk.co.alvagem.projectview.model.Task;

/**
 * TimeDiagram is a diagram that displays time series.
 * 
 * @author rbp28668
 */
public class TimeDiagram extends Diagram {

	private Task root;
    private List<TimeBar> bars = new LinkedList<TimeBar>();	
    private float timeAxis = SCALE_DAY;

    private transient float zoom = 1.0f;
    private transient Date startDate;
    private transient Date finishDate;
	private transient Font font = null;
	private transient boolean layoutRequired = true;

	// preferred sizes of the 2 panes (same height, different widths) - set during layout
    private transient int widthDiagram = 100;
    private transient int widthCaptions = 100;
    private transient int viewHeight = 100;

    
    public final static float SCALE_DAY = 1.0f;
    public final static float SCALE_WEEK = 1.0f / 7.0f;
    public final static float SCALE_MONTH = 1.0f / (365.25f/12.0f);
    public final static float SCALE_YEAR = 1.0f / 365.25f;
    
    private static final long DAYS_TO_MILLIS = 1000l * 60l * 60l * 24l;

    public static final int SHOW_YEARS = 1;
    public static final int SHOW_HALFS = 2;
    public static final int SHOW_QUARTERS = 4;
    public static final int SHOW_MONTHS = 8;
    public static final int SHOW_WEEKS = 16;
    public static final int SHOW_DAYOFMONTH = 32;
    public static final int SHOW_DAYOFWEEK = 64;
    
    private int desiredCaptions = 
         SHOW_YEARS + 
         SHOW_HALFS + 
         SHOW_QUARTERS + 
         SHOW_MONTHS + 
         SHOW_WEEKS + 
         SHOW_DAYOFMONTH +
         SHOW_DAYOFWEEK;

    // Depends on scaling which can actually be shown.
    private int actualCaptions = 0;
    private int captionsHeight = 0;
    
    
    private static final CaptionMarker CAP_YEARS = new CaptionMarker();
    private static final CaptionMarker CAP_HALFS = new CaptionMarker();
    private static final CaptionMarker CAP_QUARTERS = new CaptionMarker();
    private static final CaptionMarker CAP_MONTHS = new CaptionMarker();
    private static final CaptionMarker CAP_WEEKS = new CaptionMarker();
    private static final CaptionMarker CAP_DAYOFMONTH = new CaptionMarker();
    private static final CaptionMarker CAP_DAYOFWEEK = new CaptionMarker();
    
    private static final CaptionMarker captions[] = new CaptionMarker[]{
            CAP_YEARS,
            CAP_HALFS,
            CAP_QUARTERS,
            CAP_MONTHS,
            CAP_WEEKS,
            CAP_DAYOFMONTH,
            CAP_DAYOFWEEK
    };
    
    static {
        /***************************************/
        CAP_YEARS.name="Year";
        CAP_YEARS.calendarField = Calendar.YEAR;
        CAP_YEARS.fieldsToUse = new int[]{
                Calendar.YEAR
        };
        CAP_YEARS.mask = SHOW_YEARS;
        CAP_YEARS.sampleCaption = "2007";
        CAP_YEARS.serialConvertion = DAYS_TO_MILLIS * 365;

        /***************************************/
        CAP_HALFS.name = "Half";
        CAP_HALFS.calendarField = Calendar.MONTH;
        CAP_HALFS.fieldsToUse = new int[]{
                Calendar.YEAR,
                Calendar.MONTH
        };
        CAP_HALFS.segment = 6;
        CAP_HALFS.captions = new String[]{"H1","H2"};
        CAP_HALFS.mask = SHOW_HALFS;
        CAP_HALFS.sampleCaption = "H2";
        CAP_HALFS.serialConvertion = DAYS_TO_MILLIS * 365 / 2; 
        
        /***************************************/
        CAP_QUARTERS.name = "Quarter";
        CAP_QUARTERS.calendarField = Calendar.MONTH;
        CAP_QUARTERS.fieldsToUse = new int[]{
                Calendar.YEAR,
                Calendar.MONTH
        };
        CAP_QUARTERS.segment = 3;
        CAP_QUARTERS.captions = new String[]{"Q1","Q2","Q3","Q4"};
        CAP_QUARTERS.mask = SHOW_QUARTERS;
        CAP_QUARTERS.sampleCaption = "Q4";
        CAP_QUARTERS.serialConvertion = DAYS_TO_MILLIS * 365 / 4;

        /***************************************/
        CAP_MONTHS.name = "Month";
        CAP_MONTHS.calendarField = Calendar.MONTH;
        CAP_MONTHS.fieldsToUse = new int[]{
                Calendar.YEAR,
                Calendar.MONTH
        };
        CAP_MONTHS.captions = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        CAP_MONTHS.mask = SHOW_MONTHS;
        CAP_MONTHS.sampleCaption="XXX";
        CAP_MONTHS.serialConvertion = DAYS_TO_MILLIS * 365 / 12; 
        
        /***************************************/
        CAP_WEEKS.name = "Week of year";
        CAP_WEEKS.calendarField = Calendar.WEEK_OF_YEAR;
        CAP_WEEKS.fieldsToUse = new int[]{
                Calendar.YEAR,
                Calendar.MONTH
        };
        CAP_WEEKS.mask = SHOW_WEEKS;
        CAP_WEEKS.sampleCaption = "88";
        CAP_WEEKS.serialConvertion = DAYS_TO_MILLIS * 365 / 7; 
        
        /***************************************/
        CAP_DAYOFMONTH.name = "Day of Month";
        CAP_DAYOFMONTH.calendarField = Calendar.DAY_OF_MONTH;
        CAP_DAYOFMONTH.fieldsToUse = new int[]{
                Calendar.YEAR,
                Calendar.MONTH,
                Calendar.DAY_OF_MONTH
        };
        CAP_DAYOFMONTH.mask = SHOW_DAYOFMONTH;
        CAP_DAYOFMONTH.sampleCaption = "88";
        CAP_DAYOFMONTH.serialConvertion = DAYS_TO_MILLIS;

        /***************************************/
        CAP_DAYOFWEEK.name = "Day of Week";
        CAP_DAYOFWEEK.calendarField = Calendar.DAY_OF_WEEK;
        CAP_DAYOFWEEK.fieldsToUse = new int[]{
                Calendar.YEAR,
                Calendar.MONTH,
                Calendar.DAY_OF_MONTH
        };
        CAP_DAYOFWEEK.captions = new String[]{"S","M","T","W","Th","F","Sa",};
        CAP_DAYOFWEEK.mask = SHOW_DAYOFWEEK;
        CAP_DAYOFWEEK.sampleCaption="XX";
        CAP_DAYOFWEEK.serialConvertion = DAYS_TO_MILLIS;
    }
    
    /**
     * @param root
     */
    public TimeDiagram(Task root) {
    	this.root = root;
		font = new Font("SansSerif", Font.PLAIN,10);
		init(root);
		startDate = new Date(root.getStartDate().getTime());
		finishDate = new Date(root.getFinishDate().getTime());
    }

    private void init(Task root){
    	if(root.isActive()){
    		TimeBar bar = new TimeBar(root);
    		bars.add(bar);
    		for(Task sub : root.getSubTasks()){
    			init(sub);
    		}
    	}
    }
    /**
     * Gets the display name for the diagram.
     * @return
     */
    public String getName(){
    	return root.getName();
    }
    
 	/* (non-Javadoc)
 	 * @see alvahouse.eatool.gui.graphical.Diagram#draw(java.awt.Graphics2D, float)
 	 */
 	public void draw(Graphics2D g, float zoom) {
	    if(layoutRequired){
	        layout(g,zoom);
	    }
	    
		for(TimeBar bar : bars){
	        bar.draw(g,zoom);
	    }
	}


 	/**
 	 * @param g
 	 * @param zoom
 	 */
 	public void drawCaptions(Graphics2D g, float zoom) {
	    if(layoutRequired){
	        layout(g,zoom);
	    }
	    
	    Font localFont = deriveFont(zoom);
		for(TimeBar bar : bars){
			bar.drawCaption(g, localFont, zoom);
	    }
	}

    /**
     * @param g
     * @param zoom
     */
    private void layoutTimeScale(Graphics2D g, float zoom) {

        assert(startDate != null);
        assert(finishDate != null);
        
		Calendar start = Calendar.getInstance();
	    start.set(Calendar.YEAR,2000);
	    
		Font localFont = deriveFont(zoom);
		FontMetrics fontMetrics = g.getFontMetrics(localFont);
		
		int unitWidth = fontMetrics.charWidth('m'); 
		int unitHeight = fontMetrics.getHeight();
		
		int possibleCaptions = 0;
		
		Calendar leftCal = Calendar.getInstance();
		Calendar rightCal = Calendar.getInstance();

		for(int i=0; i<captions.length; ++i){
		    captions[i].getStart(start,leftCal);
		    captions[i].getFinish(start, rightCal);
		    
		    long leftTime = leftCal.getTimeInMillis();
		    long rightTime = rightCal.getTimeInMillis();
		    
		    int leftPos = timeToPos(leftTime, unitWidth);
		    int rightPos = timeToPos(rightTime, unitWidth);
		    
		    String caption = captions[i].sampleCaption;
		    int width = fontMetrics.stringWidth(caption);
		    
		    if(width < (rightPos - leftPos)){
		        possibleCaptions |= captions[i].mask;
		    }
		}
		
		actualCaptions = possibleCaptions & desiredCaptions;
		captionsHeight = unitHeight * (1+Integer.bitCount(actualCaptions));
        
    }

    /**
     * @param g
     * @param bounds
     * @param zoom2
     */
    public void drawTimeScale(Graphics2D g, Rectangle bounds, float zoom) {

	    if(layoutRequired){
	        layout(g,zoom);
	    }
        
        assert(startDate != null);
        assert(finishDate != null);

		Font localFont = deriveFont(zoom);
		g.setFont(localFont);

		FontMetrics fontMetrics = g.getFontMetrics(localFont);
		int unitWidth = fontMetrics.charWidth('m'); 
		int unitHeight = fontMetrics.getHeight();
		
		float y = unitHeight;

		int left = bounds.x;
		int right = bounds.x + bounds.width;
		long leftTime = posToTime(left, unitWidth);
		long rightTime = posToTime(right, unitWidth);

		Calendar leftCal = Calendar.getInstance();
		Calendar rightCal = Calendar.getInstance();
		Calendar time = Calendar.getInstance();
		
		for(int i=0; i<captions.length; ++i){
		    
		    if( (captions[i].mask & actualCaptions) == 0){
		        continue;
		    }
		
		    long leftSerial = leftTime / captions[i].serialConvertion;
		    long rightSerial = rightTime / captions[i].serialConvertion;
			
			if(leftSerial == rightSerial){ // segment open at both ends.
				leftCal.setTimeInMillis(leftTime);
			    String caption = captions[i].getCaption(leftCal);
			    int width = fontMetrics.stringWidth(caption);

			    int pos = (left + right - width) / 2;
			    //System.out.println("L: " + left + " R: " + right + " P: " + pos);
			    g.drawString(caption,pos,(int)y);
			} else {

			    leftCal.setTimeInMillis(leftTime);
			    captions[i].getStart(leftCal, time);

			    leftCal.setTime(time.getTime());
			    leftCal.add(captions[i].calendarField, captions[i].segment);
			    
		        int pos = timeToPos(time.getTimeInMillis(), unitWidth);
		        while(time.getTimeInMillis() <= rightTime){
       
			        //System.out.println("  At " + time.get(captions[i].calendarField));
			        int iy = (int)y;
			        g.drawLine(pos,iy - unitHeight,pos,bounds.y + bounds.height);

				    String text = captions[i].getCaption(time);
				    int width = fontMetrics.stringWidth(text);

				    time.add(captions[i].calendarField, captions[i].segment);
			        int next = timeToPos(time.getTimeInMillis(), unitWidth);

				    int ix = (pos + next - width) / 2;
				    g.drawString(text,ix,iy);
			        
			        pos = next;
			    }
			}
			g.drawLine(bounds.x,(int)y, bounds.x+bounds.width, (int)y);
	        y += unitHeight;
	    }
     }
 	
    /**
     * @param zoom
     * @return
     */
    private Font deriveFont(float zoom) {
        Font localFont = this.font;
		if(zoom != 1.0f){
			float scaleSize = font.getSize() * zoom;
			localFont = font.deriveFont(scaleSize);
		}
        return localFont;
    }

    /**
     * @param pos
     * @param unitWidth
     * @return
     */
    long posToTime(int pos, int unitWidth){
        double dp = (double)pos / timeAxis;
        dp /= unitWidth * 2;
        dp *= DAYS_TO_MILLIS;
        dp += startDate.getTime();
        return (long)dp;
        
    }
    
    /**
     * @param time
     * @param unitWidth
     * @return
     */
    int timeToPos(long time, int unitWidth){
        double pos = (double)(time - startDate.getTime()) / DAYS_TO_MILLIS;
        pos *= timeAxis;
        pos *= unitWidth * 2;
        return(int)pos;
    }
    
    /**
     * Sets the date range that delimit the start and finish of all the
     * time bars.  The 2 fields startDate and finishDate are set.  Note 
     * that if the diagram has no time ranges an arbitrary interval of a year
     * starting now is set.
     * @return true if there is a date range (false if no bars).
     */
    private boolean setDateRange(){
	    
        Date start = null;
	    Date finish = null;
	    boolean hasRange = false;
	    
	    Iterator iter = bars.iterator();
	    if(iter.hasNext()){
	        hasRange = true;
	        TimeBar bar = (TimeBar)iter.next();
	        start = bar.getStartDate();
	        finish = bar.getFinishDate();
	    
		    while(iter.hasNext()){
		        bar = (TimeBar)iter.next();
		        Date s = bar.getStartDate();
		        Date f = bar.getFinishDate();
		        if(s.before(start)){
		            start = s;
		        }
		        if(f.after(finish)){
		            finish = f;
		        }
		    }
	    } else {
	        start = new Date();
	        finish = new Date();
	        finish.setTime(start.getTime() + 365l * DAYS_TO_MILLIS );
	    }
	    
	    startDate = start;
	    finishDate = finish;
	    return hasRange;
    }
    
	/**
	 * Gets the bounds that will contain the diagram
	 * @param zoom is the current zoom factor.
	 * @return The bounds of the diagram.
	 */
	public Rectangle2D.Float getBounds(float zoom){
		
		Rectangle2D.Float bounds = new Rectangle2D.Float();

		for(TimeBar bar : bars){
	        bounds.add(bar.getBounds());
	    }

		return bounds;
	}

	/**
	 * Gets the bounds that will contain the diagram
	 * @param zoom is the current zoom factor.
	 * @return The bounds of the diagram.
	 */
	public Rectangle2D.Float getExtendedBounds(float zoom){
	    return getBounds(zoom);
	}
	
	/**
	 * Sets the positions of all the time-bars and captions.
	 * @param g
	 * @param zoom
	 */
	private void layout(Graphics2D g, float zoom){
	    
	    setDateRange();
        layoutTimeScale(g,zoom);

	        
		Font localFont = deriveFont(zoom);
	    FontMetrics fontMetrics = g.getFontMetrics(localFont);
	    g.setFont(localFont);
		
		int unitWidth = fontMetrics.charWidth('m'); 
		int unitHeight = fontMetrics.getHeight();
		
		float y = captionsHeight;
    	widthCaptions = 0;
		
		for(TimeBar bar : bars){
	        Date s = bar.getStartDate();
	        Date f = bar.getFinishDate();

	        int barStart = timeToPos(s.getTime(),unitWidth);
	        int barEnd = timeToPos(f.getTime(),unitWidth); 
	        
	        bar.setPosition((barStart + barEnd)/2 , y);
	        bar.setSize(barEnd - barStart, unitHeight);

	        String caption = bar.getCaption();
	        int width = fontMetrics.stringWidth(caption);
	        if(width > widthCaptions){
	            widthCaptions = width;
	        }
	        
	        y += unitHeight * 1.5f;
		}
		    
		Rectangle2D.Float bounds  = getExtendedBounds(zoom);
    	widthDiagram = 1 + (int)(bounds.x + bounds.width);
    	viewHeight = 1 + (int)(bounds.y + bounds.height);

	    layoutRequired = false;
	}

	/**
	 * Gets the preferred size to display the diagram.
	 * @return Dimension with the preferred size.
	 */
	Dimension getDiagramSize(){
	    return new Dimension(widthDiagram, viewHeight);
	}
	
	/**
	 * Gets the preferred size to display the captions.
	 * @return Dimension with the preferred size.
	 */
	Dimension getCaptionsSize(){
	    return new Dimension(widthCaptions,viewHeight);
	}

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.Diagram#reset()
     */
    public void reset() {
        bars.clear();
    }


    /**
     * Ensures that the diagram will be laid out again next time it is drawn.
     */
    public void forceLayout() {
        layoutRequired = true;
    }

    /**
     * Get the collection of TimeBar.
     * @return collection.
     */
    public Collection<TimeBar> getBars() {
        return Collections.unmodifiableCollection(bars);
    }

    /**
     * @param scale
     */
    public void setTimeAxis(float scale) {
        timeAxis = scale;
        layoutRequired = true;
    }
    


    private static class CaptionMarker{
        
        /** name for diagnostic or debug */
        String name="";
        
        /** for non-numeric values index into this array - leave null for numeric display */
        String captions[] = null;
        
        /** Sample caption for deciding whether this caption can be displayed 
         * - should be as wide as the largest caption.
         */
        String sampleCaption = "XXX";
        
        /** Which Calendar fields should be used when copying */
        int fieldsToUse[] = null;
        
        /** Which Calendar field should be used to get the caption value */
        int calendarField;
        
        /** multiple of calendar field to use - e.g. 3 for quarters */
        int segment = 1;
        
        /** Bitmask value that corresponds to this marker */
        int mask = 0;
        
        /** Serial - converts a time into a serial number to see if 2 units are the same */
        long serialConvertion = DAYS_TO_MILLIS;
        
        
        /**
         * Gets the caption corresponding to the given Calendar
         * @param c is the Calendar we want the caption of the time for.
         * @return String caption.
         */
        String getCaption(Calendar c){
            String caption;
            int val = c.get(calendarField);
            if(captions == null){
                caption = Integer.toString(val);
            } else {
                val -= c.getMinimum(calendarField);
                caption = captions[val/segment];
            }
            return caption;
        }
        
        /**
         * Gets the start time of the unit period that the given time is in.
         * @param left is the time to be converted.
         * @param result is the converted time.
         * @return the converted time (==result).
         */
        Calendar getStart(Calendar left, Calendar result){
            result.clear();
            for(int i=0; i<fieldsToUse.length; ++i){
                int val = left.get(fieldsToUse[i]);
                val -= (val % segment);
                result.set(fieldsToUse[i],val);
            }
            return result;
        }
        
        /**
         * Gets the finish time of the unit period that the given time is in.
         * @param right is the time to be converted.
         * @param result is the converted time.
         * @return the converted time (==result).
         */
        Calendar getFinish(Calendar right, Calendar result){
            getStart(right, result);
            result.add(calendarField,segment);
            return result;
        }
    }



	/* (non-Javadoc)
	 * @see uk.co.alvagem.projectview.gantt.Diagram#resetPropertiesToDefaults()
	 */
	@Override
	public void resetPropertiesToDefaults() {
		// TODO Auto-generated method stub
		
	}

 }
