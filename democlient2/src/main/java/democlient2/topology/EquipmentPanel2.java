package democlient2.topology;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.miyake.demo.jsonobject.MyTesterJson;
import com.miyake.demo.jsonobject.TestItemList;

import democlient2.MyHttpClient;
import democlient2.MyJPanel;
import democlient2.RestClient;

public class EquipmentPanel2 extends MyJPanel {
	protected void onUpdate() {}
	
	public EquipmentPanel2(MyHttpClient http, Long equipment_id) {
		RestClient restClient = new RestClient(http);
		
		TestItemList testItemList = restClient.testitems(equipment_id);
		
		List<List<String>> list = testItemList.convertToTable();
		
		this.setLayout(new BorderLayout());
		
		List<String> title = Arrays.asList("Port Name", "Direction", "Test Item", "Assigned Tester", "Candidates");
		AbstractTableModel model = new AbstractTableModel() {

			@Override
			public String getColumnName(int column) {
				return title.get(column);
			}

			@Override
			public int getRowCount() {
				return list.size();
			}

			@Override
			public int getColumnCount() {
				return title.size();
				//return list.get(0).size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return list.get(rowIndex).get(columnIndex);
			}
			
		};
		
		
		JTable table = new JTable(model);
		table.setAutoCreateRowSorter(true);
		this.add(new JScrollPane(table), BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		this.add(panel, BorderLayout.NORTH);
		JComboBox<MyTesterJson> myTesters = new JComboBox<>();
		testItemList.getMyTesters().forEach(t -> {myTesters.addItem(t);});
		panel.add(myTesters);
		
		JButton addTester = new JButton("Add Tester");
		panel.add(addTester);
		addTester.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				list.clear();
			
				list.addAll( restClient.applyTester(equipment_id, ((MyTesterJson)myTesters.getSelectedItem()).getId()).convertToTable());
				model.fireTableDataChanged();
			}
		});
	}

	@Override
	protected void commit() {
		// TODO Auto-generated method stub
		
	}

}
