package democlient2;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

public class TableComboBox<T> {
	private Map<String, Integer> map = new HashMap<>();
	public TableComboBox(JTable table, T[] items, int columnIndex) {
	    JComboBox<T> combo = new JComboBox<>();
	    
	    int i = 0;
	    for (T item : items) {
	    	combo.addItem(item);
	    	map.put(item.toString(), i++);
	    }
	    TableColumn col = table.getColumnModel().getColumn(columnIndex);
	    col.setCellEditor(new DefaultCellEditor(combo));
	    
	    table.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {

			}
	    	
	    });
	    table.addMouseListener(new MouseAdapter() {
	    	
			@Override
			public void mouseEntered(MouseEvent e) {
				if (table.getSelectedColumn() == columnIndex) {
					String s = (String)table.getValueAt(table.getSelectedRow(), table.getSelectedColumn());
					int index = map.get(s);
//					System.out.println(index);
					combo.setSelectedIndex(index);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

	    });
	}

}
