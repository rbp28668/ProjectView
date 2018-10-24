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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import uk.co.alvagem.projectview.model.Task;

/**
 * Graphs the uncertainty of when a task completes based on its estimated effort.
 * Only makes sense on leaf tasks.
 * @author bruce.porteous
 *
 */
public class MonteCarloSchedulingGraph extends JInternalFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MonteCarloSchedulingGraph(Task task, float[] estimates, float[] effort) throws MathException{
        assert(estimates.length == effort.length);
        
		XYSeries elapsedSeries = new XYSeries("Elapsed");
		XYSeries effortSeries = new XYSeries("Effort");
        
		for(int i=0; i<estimates.length; ++i){
			float p = (float)i / (float)estimates.length;
			elapsedSeries.add(p,estimates[i]);
			effortSeries.add(p,effort[i]);
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(elapsedSeries);
        dataset.addSeries(effortSeries);
        
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
