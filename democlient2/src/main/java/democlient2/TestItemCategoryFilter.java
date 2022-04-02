package democlient2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import com.miyake.demo.entities.TestItemCategoryEntity;

public class TestItemCategoryFilter {

	public JComboBox<TestItemCategoryEntity> create(MyHttpClient http, JTable table, AbstractTableModel model, int column) {
		TableRowSorter<AbstractTableModel> sorter = new TableRowSorter<AbstractTableModel>(model);
		table.setRowSorter(sorter);
		try {
			TestItemCategoryEntity[] categories = http.getObject("TestItemCategoryEntityS", TestItemCategoryEntity[].class);
			JComboBox<TestItemCategoryEntity> comboBox = new JComboBox<>();
			TestItemCategoryEntity nullE = new TestItemCategoryEntity();
			nullE.setCategory("");
			comboBox.addItem(nullE);
			for (TestItemCategoryEntity category : categories) {
				comboBox.addItem(category);
			}
			//panel.add(comboBox);
			
			comboBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
		        	RowFilter<AbstractTableModel, Object>  filter = RowFilter.regexFilter(comboBox.getSelectedItem().toString(), column);
		        	sorter.setRowFilter(filter);
				}
			});
			return comboBox;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}