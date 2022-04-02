package democlient2;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.miyake.demo.entities.MyTesterEntity;
import com.miyake.demo.entities.TesterEntity;

public class MyTesterPanel extends JPanel {

	private static final String ID = "ID";
	private static final String NAME = "Name";
	private static final String TESTER = "Tester";
	private AbstractTableModel model;
	private MyTesterEntity[] myTesters;
	private Map<Long, TesterEntity> map = new HashMap<>();
	
	public MyTesterPanel(RestClient restClient) {
		this.setLayout(new BorderLayout());
		
		myTesters = restClient.mytesters();
		
		JComboBox<TesterEntity> combo = new JComboBox<>();
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		this.add(panel, BorderLayout.NORTH);
		panel.add(combo);
		for (TesterEntity e : restClient.testers()) {
			combo.addItem(e);
			map.put(e.getId(), e);
		}
		JButton add = new JButton("Add");
		panel.add(add);
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String value = JOptionPane.showInputDialog("Input Tester Name", "Tester Name");
				if (value == null || value.isEmpty()) {
					return;
				}
				MyTesterEntity te = new MyTesterEntity();
				te.setName(value);
				te.setTester(((TesterEntity)combo.getSelectedItem()).getId());
				try {
					restClient.post(te);
					updateData(restClient);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JButton remove = new JButton("Remove");
		panel.add(remove);

		
		List<String> title = Arrays.asList(ID, TESTER, NAME);
		model = new AbstractTableModel() {

			@Override
			public String getColumnName(int column) {
				return title.get(column);
			}

			@Override
			public int getRowCount() {
				return myTesters.length;
			}

			@Override
			public int getColumnCount() {
				return title.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				MyTesterEntity e = myTesters[rowIndex];
				if (title.get(columnIndex).equals(TESTER)) {
					if (e.getTester() == null) {
						return "";
					}
					return map.get(e.getTester());
				}
				else if (title.get(columnIndex).equals(NAME)) {
					return e.getName();
				}
				else if (title.get(columnIndex).equals(ID)) {
					return e.getId();
				}
				return null;
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				MyTesterEntity e = myTesters[rowIndex];
				if (title.get(columnIndex).equals(NAME)) {
					e.setName(aValue.toString());
					try {
						restClient.post(e);
						updateData(restClient);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if (title.get(columnIndex).equals(NAME)) {
					return true;
				}
				return false;
			}
			
		};
		JTable table = new JTable(model);
		this.add(new JScrollPane(table), BorderLayout.CENTER);
		
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = table.convertRowIndexToModel(table.getSelectedRow());

				restClient.delete(myTesters[index]);
				updateData(restClient);
			}
		});
	}

	protected void updateData(RestClient restClient) {
		myTesters = restClient.mytesters();
		model.fireTableDataChanged();
	}

}
