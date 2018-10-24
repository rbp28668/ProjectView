/**
 * 
 */
package uk.co.alvagem.projectview.swingui;

import javax.swing.JInternalFrame;

import org.apache.commons.math.MathException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.projectview.model.TaskHistory;

/**
 * Graphs the uncertainty of when a task completes based on its estimated effort.
 * Only makes sense on leaf tasks.
 * @author bruce.porteous
 *
 */
public class UncertaintyGraph extends JInternalFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UncertaintyGraph(Task task) throws MathException{
        
		XYSeries series = new XYSeries("Time");
        
		for(float p = 0.05f; p <0.99f; p += 0.05f){
			float days = task.getUncertaintyType().getEstimatedEffort(task.getEstimatedEffort(), 
					task.getEstimateSpread(),p);
			series.add(p,days);
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        
        JFreeChart chart = ChartFactory.createXYLineChart(task.getName(), "Probability", "Days", dataset, PlotOrientation.VERTICAL, true, false, false);
 
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
