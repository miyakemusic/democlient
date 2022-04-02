package democlient2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import com.miyake.demo.entities.TestItemEntity;
import com.miyake.demo.entities.TesterCapabilityEntity;
import com.miyake.demo.entities.TesterEntity;

public class TesterConfigPanel extends MyJPanel {

	public static void showFrame(MyHttpClient http, TesterEntity entity) {
		JFrame frame = new JFrame();
		frame.setSize(new Dimension(800, 600));
		frame.getContentPane().setLayout(new BorderLayout());
		
		frame.getContentPane().add(new TesterConfigPanel(http, entity), BorderLayout.CENTER);
		frame.setVisible(true);
		
	}
	private TestItemEntity items[];
	private Map<Long, Boolean> enabled = new HashMap<>();
	
	public Map<Long, Boolean> getEnabled() {
		return enabled;
	}

	public TestItemEntity[] getItems() {
		return items;
	}

	public TesterConfigPanel(MyHttpClient http, TesterEntity entity) {
		this.setPreferredSize(new Dimension(1000, 600));
		List<String> title = Arrays.asList("Select", "Item");
		
		try {
			items = http.getObject("TestItemEntityS", TestItemEntity[].class);
			for (TestItemEntity item : items) {
				enabled.put(item.getId(), false);
				for (TesterCapabilityEntity e : entity.getTestItems()) {
					if (item.getId().equals(e.getTestItem())) {
						enabled.put(item.getId(), true);
						break;
					}
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		AbstractTableModel model = new AbstractTableModel() {

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return true;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (title.get(columnIndex).equals("Select")) {
					return Boolean.class;
				}
				return String.class;
			}

			@Override
			public String getColumnName(int column) {
				return title.get(column);
			}

			@Override
			public int getRowCount() {
				return items.length;
			}

			@Override
			public int getColumnCount() {
				return title.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				TestItemEntity item = items[rowIndex];
				
				if (title.get(columnIndex).equals("Select")) {
					return enabled.get(item.getId());
				}
				else if (title.get(columnIndex).equals("Item")) {
					return "[" + item.getCategoryEntity().getCategory() + "] " + item.getTest_item();
				}
				return "";
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				TestItemEntity item = items[rowIndex];
				if (title.get(columnIndex).equals("Select")) {
					enabled.put(item.getId(), (Boolean)aValue);
				}
			}
			
		};

		
				
		JTable table = new JTable(model);
		table.setAutoCreateRowSorter(true);
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		this.setBorder(new TitledBorder("Tester Configuration"));
		this.setLayout(new BorderLayout());
		this.add(panel, BorderLayout.NORTH);
		this.add(new JScrollPane(table), BorderLayout.CENTER);

		panel.add(new TestItemCategoryFilter().create(http, table, model, title.indexOf("Item")));

		this.doLayout();
	}

	@Override
	protected void commit() {
		// TODO Auto-generated method stub
		
	}

}

