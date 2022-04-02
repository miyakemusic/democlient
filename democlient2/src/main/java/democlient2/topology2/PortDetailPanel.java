package democlient2.topology2;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import com.miyake.demo.entities.PortDirectionEntity;
import com.miyake.demo.entities.PortEntity;
import com.miyake.demo.entities.PortEntitySimple;
import com.miyake.demo.entities.PortTestEntity;
import com.miyake.demo.entities.PortTestTemplateEntity;
import com.miyake.demo.entities.TestItemEntity;

import democlient2.MyJFrame;
import democlient2.MyJPanel;
import democlient2.MyTextEditBox;
import democlient2.RestClient;
import democlient2.TestItemPanel;
import democlient2.TestItemPanel.Mode;

public class PortDetailPanel extends MyJPanel {

	private static final String PASS_FAIL = "Pass/Fail";
	private static final String DIRECTION = "Direction";
	private static final String TEST_RESULT = "Test Result";
	private static final String CRITERIA = "Criteria";
	private static final String TEST = "Test";
	private static final String CATEGORY = "Category";
	private MyTextEditBox nameEditor;
	private PortEntitySimple port;
	private RestClient restClient;
	private PortTestEntity[] portTests;
	
	public PortDetailPanel(PortEntitySimple port2, RestClient restClient) {
		this.port = port2;
		this.restClient = restClient;
		retreivePortTests();
		
		this.setLayout(new BorderLayout());
		
		PortDirectionEntity[] directions = restClient.directions();
		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
		this.add(tablePanel, BorderLayout.CENTER);
		
		JPanel toolBar = new JPanel();
		toolBar.setLayout(new FlowLayout());
		toolBar.add(nameEditor = new MyTextEditBox("Port Name", port2.getPort_name()));
		this.add(toolBar, BorderLayout.NORTH);
		
		createTables(restClient, port2.getId(), directions, tablePanel);

		JButton regAsTemplate = new JButton("Register as Template");
		toolBar.add(regAsTemplate);
		regAsTemplate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String value = JOptionPane.showInputDialog("Template Name", "");
				if (value == null || value.isEmpty()) {
					return;
				}
				restClient.registerPortTestTemplate(value, port.getId());
			}
		});
		
		JButton loadTemplateButton = new JButton("Load Template");
		toolBar.add(loadTemplateButton);
		loadTemplateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (loadTemplate(port2, restClient)) {
					createTables(restClient, port2.getId(), directions, tablePanel);
				}
			}
		});
	}

	private void retreivePortTests() {
		this.portTests = restClient.portTests(this.port.getId());
	}
	
	private void createTables(RestClient restClient, Long portid, PortDirectionEntity[] directions, JPanel tablePanel) {
		tablePanel.removeAll();
		for (PortDirectionEntity direction : directions) {
			tablePanel.add(createTablePanel(portid, direction, portTests));
		}
		this.updateUI();
	}
	
	private JPanel createTablePanel(Long portid, PortDirectionEntity direction, PortTestEntity[] portTests) {
		List<PortTestEntity> list = new ArrayList<>();
		for (PortTestEntity portTest : portTests) {
			if (portTest.getDirection().equals(direction.getId())) {
				list.add(portTest);
			}
		}
		createList(direction, list);
		
		List<String> title = Arrays.asList(CATEGORY, TEST, CRITERIA, TEST_RESULT, PASS_FAIL);
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
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				PortTestEntity portTest = list.get(rowIndex);
				if (title.get(columnIndex).equals(TEST)) {
					return portTest.getTest_itemEntity().getTest_item();
				}
				else if (title.get(columnIndex).equals(CATEGORY)) {
					return portTest.getTest_itemEntity().getCategoryEntity().getCategory();
				}
				else if (title.get(columnIndex).equals(DIRECTION)) {
					return portTest.getDirectionEntity().getName();
				}
				else if (title.get(columnIndex).equals(TEST_RESULT)) {
					return portTest.getResult();
				}
				else if (title.get(columnIndex).equals(CRITERIA)) {
					return portTest.getCriteria();
				}
				else if (title.get(columnIndex).equals(PASS_FAIL)) {
					return portTest.getPassfail().name();
				}
				return null;
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				PortTestEntity portTest = list.get(rowIndex);
				if (title.get(columnIndex).equals(TEST)) {

				}
				else if (title.get(columnIndex).equals(CATEGORY)) {

				}
				else if (title.get(columnIndex).equals(DIRECTION)) {

				}
				else if (title.get(columnIndex).equals(TEST_RESULT)) {
					portTest.setResult(aValue.toString());
				}
				else if (title.get(columnIndex).equals(CRITERIA)) {
					portTest.setCriteria(aValue.toString());
				}
				try {
					restClient.post(portTest);
					retreivePortTests();
					createList(direction, list);
					this.fireTableDataChanged();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return true;
			}
			
			
		};
		JTable table = new JTable(model);
//		model.fireTableDataChanged();
		table.setAutoCreateRowSorter(true);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(direction.getName()));
		panel.setLayout(new BorderLayout());
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		
		JPanel toolBar = new JPanel();
		panel.add(toolBar, BorderLayout.NORTH);
		toolBar.setLayout(new FlowLayout());
		JButton addButton = new JButton("Add");
		toolBar.add(addButton);
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TestItemPanel panel = new TestItemPanel(restClient, Mode.Readonly);
				MyJFrame frame = new MyJFrame("Test Item", panel);
				frame.modal();
				frame.setVisible(true);
				if( frame.isOkClicked()) {
					List<PortTestEntity> portTests = new ArrayList<>();
					for (TestItemEntity testItem : panel.getSelectedItems()) {
						PortTestEntity portTestEntity = new PortTestEntity();
						portTestEntity.setDirection(direction.getId());
						portTestEntity.setPort(portid);
						portTestEntity.setTestItem(testItem.getId());
						
						portTests.add(portTestEntity);
					}
					try {
						restClient.post(portTests.toArray(new PortTestEntity[portTests.size()]));
						retreivePortTests();
						createList(direction, list);
						model.fireTableDataChanged();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		JButton deleteButton = new JButton("Delete");
		toolBar.add(deleteButton);
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doDelete(direction, list, model, table);
			}
		});
		
		JPopupMenu popup = new JPopupMenu();
		JMenuItem deleteMenu = new JMenuItem("Delete");
		popup.add(deleteMenu);
		deleteMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doDelete(direction, list, model, table);
			}
		});
				
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		return panel;
	}

	private void createList(PortDirectionEntity direction, List<PortTestEntity> list) {
		list.clear();
		for (PortTestEntity portTest : this.portTests) {
			if (portTest.getDirection().equals(direction.getId())) {
				list.add(portTest);
			}			
		}
	}

//	protected void add(JTable table, List<PortTestEntity> list, RestClient restClient2) {
//		for (int row : table.getSelectedRows()) {
//			int index = table.convertRowIndexToModel(row);
//			PortTestEntity portTestEntity = list.get(index);
//			try {
//				restClient.post(portTestEntity);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		this.port.getPortTests().clear();
//		restClient.port(this.port.getId()).getPortTests().forEach(p -> port.getPortTests().add(p));
//	}
	
	protected void delete(JTable table, List<PortTestEntity> list, RestClient restClient2) {
		for (int row : table.getSelectedRows()) {
			int index = table.convertRowIndexToModel(row);
			PortTestEntity portTestEntity = list.get(index);
			restClient.delete(portTestEntity);
		}
		this.retreivePortTests();
	}


	@Override
	protected void commit() {
		this.port.setPort_name(this.nameEditor.getText());
		try {
			this.restClient.post(this.port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void doDelete(PortDirectionEntity direction, List<PortTestEntity> list, AbstractTableModel model,
			JTable table) {
		delete(table, list, restClient);
		createList(direction, list);
		model.fireTableDataChanged();
	}

	private boolean loadTemplate(PortEntitySimple port2, RestClient restClient) {
		PortTestTemplateEntity[] ret = restClient.getPortTestTemplates();
		JComboBox<PortTestTemplateEntity> combo = new JComboBox<>();
		for (PortTestTemplateEntity t : ret) {
			combo.addItem(t);
		}
		
		MyJPanel panel = new MyJPanel() {
			@Override
			protected void commit() {
				restClient.applyPortTemplate(port2.getId(), ((PortTestTemplateEntity)combo.getSelectedItem()).getId());
				retreivePortTests();
			}
		};
		panel.setLayout(new FlowLayout());
		panel.add(combo);
		MyJFrame frame = new MyJFrame("Load Template", panel);
		frame.modal().setVisible(true);
		
		return frame.isOkClicked();
	}

}
