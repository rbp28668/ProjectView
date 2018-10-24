/**
 * 
 */
package uk.co.alvagem.projectview.swingui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;

import uk.co.alvagem.projectview.model.Calendar;
import uk.co.alvagem.projectview.model.CalendarItem;
import uk.co.alvagem.projectview.model.DayCalendarItem;
import uk.co.alvagem.projectview.model.DayOfWeekCalendarItem;
import uk.co.alvagem.swingui.BasicDialog;
import uk.co.alvagem.swingui.ButtonBox;
import uk.co.alvagem.swingui.GridPanel;

/**
 * @author bruce.porteous
 *
 */
public class CalendarEditor extends BasicDialog {

	private static final long serialVersionUID = 1L;
	private CalendarPanel cp;
	private ItemPanel items;
	
	/**
	 * @param parent
	 * @param title
	 */
	public CalendarEditor(Component parent, Calendar calendar, List<Calendar> calendars) {
		super(parent, "Edit Calendar");
		setLayout(new BorderLayout());

        List<Calendar> possibleParents = new LinkedList<Calendar>();
        possibleParents.addAll(calendars);
        possibleParents.remove(calendar);
		
		cp = new CalendarPanel(calendar,possibleParents);
		add(cp,BorderLayout.NORTH);
		
		items = new ItemPanel(calendar);
		add(items,BorderLayout.CENTER);
		
		add(getOKCancelPanel(), BorderLayout.EAST);
		
		pack();
	}

   @Override
    protected boolean validateInput() {
        return cp.validateInput() && items.validateInput();
    }

	/* (non-Javadoc)
	 * @see uk.co.alvagem.swingui.BasicDialog#onOK()
	 */
	@Override
	protected void onOK() {
	    cp.onOK();
	    items.onOK();
	}

	private static class CalendarPanel extends GridPanel {
		
	    /**   */
        private static final long serialVersionUID = 1L;

        private JTextField name;
        private JTextField workingDay;
		private JComboBox parents;
		private Calendar calendar;
		
		private static Object NONE  = new Object() {
            public String toString(){
                return "--NONE--";
            }
        };

		CalendarPanel(Calendar calendar, List<Calendar> possibleParents){
			this.calendar = calendar;
			
			name = new JTextField(calendar.getName());
			name.setColumns(30);
			addItem("Name", name, "Name of this calendar");
			
			workingDay = new JTextField(Float.toString(calendar.getWorkingDayLength()));
			name.setColumns(15);
			addItem("Working Day", workingDay,"Length of working day in hours");
			
			Object[] items = new Object[possibleParents.size() + 1];
			items[0] = NONE;
			int idx = 1;
			for(Calendar c : possibleParents){
			    items[idx++] = c;
			}
            parents = new JComboBox(items);
            if(calendar.getParent() == null){
                parents.setSelectedItem(NONE);
            } else {
                parents.setSelectedItem(calendar.getParent());
            }
            addItem("Parent Calendar", parents, "Select a base calendar or " + NONE.toString() + " if this calendar has no parent");
		}
		
	    boolean validateInput() {
	        if(name.getText().trim().equals("")){
                JOptionPane.showMessageDialog(this,"Please enter a name","ProjectView", JOptionPane.WARNING_MESSAGE);
                name.requestFocusInWindow();
                return false;
	        }
	        
	        if(workingDay.getText().trim().equals("")){
                JOptionPane.showMessageDialog(this,"Please enter working day length","ProjectView", JOptionPane.WARNING_MESSAGE);
                workingDay.requestFocusInWindow();
                return false;
	        }
	        
	        try{
	        	Float.parseFloat(workingDay.getText());
	        } catch (NumberFormatException nfx) {
                JOptionPane.showMessageDialog(this,"Please enter working day length as a number","ProjectView", JOptionPane.WARNING_MESSAGE);
                workingDay.requestFocusInWindow();
                return false;
	        }
	        
	        
	        return true;
	    }

	    void onOK() {
	        calendar.setName(name.getText().trim());
	        calendar.setWorkingDayLength(Float.parseFloat(workingDay.getText()));
	        Object selected = parents.getSelectedItem();
	        if(selected != NONE){
	            calendar.setParent((Calendar)selected);
	        }
	    }
		
	}
	
	
	private class ItemPanel extends JPanel {
		
		/** */
        private static final long serialVersionUID = 1L;
        private JButton btnEdit;
		private JButton btnNewDay;
		private JButton btnNewDayOfWeek;
		private JButton btnDelete;
		private JList lstItems;
		private Calendar calendar;
		
		ItemPanel(Calendar calendar){
		
			this.calendar = calendar;
			
            setBorder(new TitledBorder("Calendar Items"));
            setLayout(new BorderLayout());
            
            btnEdit = new JButton("Edit");
            btnNewDay = new JButton("New Date");
            btnNewDayOfWeek = new JButton("New Day of Week");
            btnDelete = new JButton("Delete");
            
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
            
            ButtonBox box = new ButtonBox();
            box.add(btnEdit);
            box.add(btnNewDay);
            box.add(btnNewDayOfWeek);
            box.add(btnDelete);
            
            // TODO - figure out some way of comparing CalendarItems for a sensible list order.
            //List<CalendarItem> items = new LinkedList<CalendarItem>();
            //items.addAll(calendar.getItems());
            
            DefaultListModel lm = new DefaultListModel();
            for(CalendarItem item : calendar.getItems()){
                CalendarItem copy = item.clone();
                lm.addElement(copy);
            }
            
            lstItems = new JList(lm);
            JScrollPane scroll = new JScrollPane(lstItems);
            add(scroll,BorderLayout.CENTER);
            add(box, BorderLayout.EAST);
            
            // Edit Property
            btnEdit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    CalendarItem item = (CalendarItem)lstItems.getSelectedValue();
                    if(item instanceof DayOfWeekCalendarItem){
                        DayOfWeekCalendarItemEditor editor = 
                            new DayOfWeekCalendarItemEditor(CalendarEditor.this,(DayOfWeekCalendarItem)item);
                    } else if (item instanceof DayCalendarItem) {
                        DayCalendarItemEditor editor = 
                            new DayCalendarItemEditor(CalendarEditor.this,(DayCalendarItem)item);
                    }
                }
            });

            // New Day Property
            btnNewDay.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                	DayCalendarItem item = new DayCalendarItem();
                    DayCalendarItemEditor editor = new DayCalendarItemEditor(CalendarEditor.this,item);
                    if(editor.wasEdited()){
                    	DefaultListModel model = (DefaultListModel)lstItems.getModel();
                        model.addElement(item);
                    }
                }
            });

            // New Day Property
            btnNewDayOfWeek.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    DayOfWeekCalendarItem item = new DayOfWeekCalendarItem();
                    DayOfWeekCalendarItemEditor editor = new DayOfWeekCalendarItemEditor(CalendarEditor.this,item);
                    editor.setVisible(true);
                    if(editor.wasEdited()) {
                        DefaultListModel model = (DefaultListModel)lstItems.getModel();
                        model.addElement(item);
                    }
                }
            });
            
            // Delete Property
            btnDelete.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    CalendarItem item = (CalendarItem)lstItems.getSelectedValue();
                    if(JOptionPane.showConfirmDialog(null,"Delete " + item.toString() + "?","ProjectView",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                        int idx = lstItems.getSelectedIndex();
                        DefaultListModel listModel = (DefaultListModel)lstItems.getModel(); 
                        listModel.remove(idx);    
                        ItemPanel.this.calendar.getItems().remove(item);
                    }
                }
            });
            
            

            lstItems.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
                public void valueChanged(ListSelectionEvent evt) {
                    if(!evt.getValueIsAdjusting()) {
                        btnEdit.setEnabled(true);
                        btnDelete.setEnabled(true);
                    }
                }

            });
			
		}
		
        boolean validateInput() {
            return true; // empty list is fine - calendar is just a placeholder.
        }

        void onOK() {
            DefaultListModel listModel = (DefaultListModel)lstItems.getModel(); 
            
            Set<CalendarItem> items = calendar.getItems();
            Set<CalendarItem> toDelete = new HashSet<CalendarItem>();
            toDelete.addAll(items);
            
            for(int i=0; i<listModel.size(); ++i){
                CalendarItem item = (CalendarItem)listModel.get(i);
                if(items.contains(item)){
                    toDelete.remove(item);
                    items.remove(item);  // remove old item
                }
                items.add(item);     // add or replace by new.
            }
            items.removeAll(toDelete);
        }
		
	}

	
	
}
