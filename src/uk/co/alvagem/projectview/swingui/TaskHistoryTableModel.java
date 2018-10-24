/**
 * 
 */
package uk.co.alvagem.projectview.swingui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import uk.co.alvagem.projectview.model.Task;
import uk.co.alvagem.projectview.model.TaskHistory;

/**
 * Provides a table model to represent a task history as a table.
 * @author bruce.porteous
 *
 */
public class TaskHistoryTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String[] HEADERS = {
		"Time Point",
		"Fraction Complete",
		"Estimated Effort",
		"Estimate Spread",
		"Uncertainty Type",
		"Elapsed Time",
		"Effort Driven",
		"Actual Work",
		"Start Date",
		"Finish Date",
		"Active"
	};
   
	/** Copy of data */
	private Vector<String[]> rows = new Vector<String[]>();
	
	private static final DateFormat FMT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


	public TaskHistoryTableModel(Task task){
		for(TaskHistory history : task.getHistory()){
			String[] row = new String[HEADERS.length];
			
			row[0] = FMT.format(history.getTimePoint());
			row[1] = Float.toString(history.getFractionComplete());
			row[2] = Float.toString(history.getEstimatedEffort());
			row[3] = Float.toString(history.getEstimateSpread());
			row[4] = history.getUncertaintyType().toString();
			row[5] = Float.toString(history.getElapsedTime());
			row[6] = history.isEffortDriven() ? "yes" : "no";
			row[7] = Float.toString(history.getActualWork());
			row[8] = FMT.format(history.getStartDate());
			row[8] = FMT.format(history.getFinishDate());
			row[10] = history.isActive() ? "yes" : "no";
			rows.add(row);
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int arg0) {
		return HEADERS[arg0];
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return HEADERS.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return rows.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		String[] row = rows.elementAt(rowIndex);
		return row[columnIndex];
	}
	
	
}
