package democlient2;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.miyake.demo.entities.TestItemCategoryEntity;
import com.miyake.demo.entities.TestItemEntity;

public class TestItemPanel extends MyJPanel {

	public enum Mode {
		Editable,
		Readonly
	}
	private static final String CATEGORY = "Category";
	private static final String TEST_ITEM = "Test Item";

	private TestItemEntity[] items;
	private RestClient restClient;
	private AbstractTableModel model;
	private JTable table;
	public TestItemPanel(RestClient restClient, Mode mode) {
		this.restClient = restClient;
		
		TestItemCategoryEntity[] categories = restClient.test_item_categories();//
		
		
		List<String> title = new ArrayList<>();//Arrays.asList(TEST_ITEM, CATEGORY);
		title.add(TEST_ITEM);
		//if (mode.equals(Mode.Editable)) {
			title.add(CATEGORY);
		//}
		
		model = new AbstractTableModel() {

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
				if (title.get(columnIndex).equals(TEST_ITEM)) {
					return item.getTest_item();
				}
				else if (title.get(columnIndex).equals(CATEGORY)) {
					if (item.getCategoryEntity() == null) {
						return "";
					}
					return item.getCategoryEntity().getCategory();
				}
				return "";
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				TestItemEntity item = items[rowIndex];
				if (title.get(columnIndex).equals(TEST_ITEM)) {
					item.setTest_item(aValue.toString());
				}
				else if (title.get(columnIndex).equals(CATEGORY)) {
					TestItemCategoryEntity v = (TestItemCategoryEntity)aValue;
					item.setCategory(v.getId());
				}			
				try {
					restClient.post(item);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				updateTable();
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return mode.equals(Mode.Editable);
			}
			
			
		};
		updateTable();
		this.setLayout(new BorderLayout());

		table = new JTable(model);
		this.add(new JScrollPane(table), BorderLayout.CENTER);
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		JComboBox<TestItemCategoryEntity> combo;
		panel.add(combo = new TestItemCategoryFilter().create(restClient.http(), table, model, title.indexOf(CATEGORY)));
		this.add(panel, BorderLayout.NORTH);
		
		if (mode.equals(Mode.Editable)) {
			new TableComboBox<>(table, categories, title.indexOf(CATEGORY));
			
			JButton addItem = new JButton("Add");
			panel.add(addItem);
			addItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					TestItemEntity entity = new TestItemEntity();
					entity.setCategory(((TestItemCategoryEntity)combo.getSelectedItem()).getId());;
					entity.setTest_item("new item");
					try {
						restClient.post(entity);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					updateTable();
				}
			});
			
			JButton deleteButton = new JButton("Delete");
			panel.add(deleteButton);
			deleteButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					restClient.delete(items[table.convertRowIndexToModel(table.getSelectedRow())]);
					updateTable();
				}
			});
		}
	}

	public List<TestItemEntity> getSelectedItems() {
		List<TestItemEntity> ret = new ArrayList<>();
		for (int row : this.table.getSelectedRows()) {
			ret.add( items[table.convertRowIndexToModel(row)] );
		}

		return ret;
	}
	
	protected void updateTable() {
		items = restClient.test_items();//  .getObject("test_item", TestItemEntity[].class);
		model.fireTableDataChanged();
	}

	@Override
	protected void commit() {
		// TODO Auto-generated method stub
		
	}

}
