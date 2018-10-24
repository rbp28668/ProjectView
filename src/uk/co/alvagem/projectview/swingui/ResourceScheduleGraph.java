/**
 * 
 */
package uk.co.alvagem.projectview.swingui;

import java.util.Date;
import java.util.Map;

import javax.swing.JInternalFrame;

import org.apache.commons.math.MathException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import uk.co.alvagem.projectview.core.schedule.DateUtils;
import uk.co.alvagem.projectview.core.schedule.ResourceSchedule;
import uk.co.alvagem.projectview.model.Resource;

/**
 * Graphs the uncertainty of when a task completes based on its estimated effort.
 * Only makes sense on leaf tasks.
 * @author bruce.porteous
 *
 */
public class ResourceScheduleGraph extends JInternalFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResourceScheduleGraph(String taskName, Map<Resource,ResourceSchedule> resources) throws MathException{
        
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		
		for(Resource resource : resources.keySet()){
			
			TimeSeries series = new TimeSeries(resource.getName(), Day.class);
			ResourceSchedule schedule = resources.get(resource);
			Date start = schedule.getEarliestDay();
			Date finish = schedule.gatLatestDay();
			finish.setTime(finish.getTime() + DateUtils.TICKS_PER_DAY);
			
			for( Date now = new Date(start.getTime()); now.before(finish); now.setTime(now.getTime() + DateUtils.TICKS_PER_DAY)){
				float hours = schedule.getHoursUsedOn(now);
				series.add(new Day(now),hours);
			}
			dataset.addSeries(series);
		}
        
        JFreeChart chart = ChartFactory.createTimeSeriesChart(taskName, "Date", "Hours", dataset,  true, true, false);
 
        ChartPanel panel = new ChartPanel(chart);
        
        add(panel);

        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        
        setLocation(100,100);
        setSize(800, 600);

        pack();

	}
}
