package democlient2.topology2;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.miyake.demo.entities.PortEntity;

public class PortTablePanel extends JPanel {

	public PortTablePanel(List<PortEntity> ports) {
		this.setLayout(new BorderLayout());

		List<String> titles = Arrays.asList("Port");
		AbstractTableModel model = new AbstractTableModel() {

			@Override
			public int getRowCount() {
				return ports.size();
			}

			@Override
			public int getColumnCount() {
				return titles.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				if (titles.get(columnIndex).equals("Port")) {
					return ports.get(rowIndex).getPort_name();
				}
				return null;
			}
			
		};
		
		JTable table = new JTable(model);
		this.add(new JScrollPane(table), BorderLayout.CENTER);
	}

}
