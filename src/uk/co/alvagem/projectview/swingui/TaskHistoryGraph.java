/**
 * 
 */
package uk.co.alvagem.projectview.swingui;

import javax.swing.JInternalFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.projectview.model.TaskHistory;

/**
 * @author bruce.porteous
 *
 */
public class TaskHistoryGraph extends JInternalFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TaskHistoryGraph(Task task){
        
		TimeSeriesCollection dataset = new TimeSeriesCollection();
        
        TimeSeries actualWork = new TimeSeries("Actual Work", FixedMillisecond.class);
        TimeSeries fractionComplete = new TimeSeries("Fraction Complete", FixedMillisecond.class);
        TimeSeries elapsedTime = new TimeSeries("Elapsed Time", FixedMillisecond.class);
        TimeSeries estimatedEffort = new TimeSeries("Estimated Effort", FixedMillisecond.class);
        
        for(TaskHistory history : task.getHistory()){
        	FixedMillisecond when = new FixedMillisecond(history.getTimePoint().getTime());
        	actualWork.addOrUpdate(when,history.getActualWork());
        	fractionComplete.addOrUpdate(when,history.getFractionComplete());
        	elapsedTime.addOrUpdate(when,history.getElapsedTime());
        	estimatedEffort.addOrUpdate(when, history.getEstimatedEffort());
        }
        
        
        dataset.addSeries(actualWork);
        dataset.addSeries(fractionComplete);
        dataset.addSeries(elapsedTime);
        dataset.addSeries(estimatedEffort);
        
        JFreeChart chart = ChartFactory.createTimeSeriesChart(task.getName(), "Date", "Days", dataset, true, false, false);
 
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
