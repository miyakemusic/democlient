package democlient2.topology;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import com.miyake.demo.entities.MyTesterEntity;
import com.miyake.demo.entities.PortTestEntity;
import com.miyake.demo.entities.TestItemEntity;
import com.miyake.demo.entities.TesterCapabilityEntity;
import com.miyake.demo.entities.TesterEntity;

import democlient2.MyHttpClient;
import democlient2.MyJFrame;
import democlient2.MyJPanel;
import democlient2.RestClient;
import democlient2.TestItemPanel;
import democlient2.TestItemPanel.Mode;

public class PortPanel extends MyJPanel {

	private static final String CANDIDATES = "Candidates";
	private static final String CRITERIA = "Criteria";
	private static final String RESULT = "Result";
	private static final String PASS_FAIL = "Pass/Fail";
	private static final String ID = "ID";
	private List<PortTestEntity> inside = new ArrayList<>();
	private List<PortTestEntity> outside = new ArrayList<>();
	private List<AbstractTableModel> models = new ArrayList<>();
	private MyHttpClient http;
	private Long port_id;
	private JTable insideTable;
	private JTable outsideTable;
	private TesterEntity testers[] ;
	
	public PortPanel(MyHttpClient http, Long port_id) {
		try {
//			Map<Long, List<TesterEntity>> myTeters = http.getObject("mytesters/candidates?portid="+port_id, Map.class);
			
			testers = http.getObject("TesterEntityS", TesterEntity[].class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.setLayout(new BorderLayout());
		this.http = http;
		this.port_id = port_id;

		JPanel toolPanel = new JPanel();
		toolPanel.setLayout(new GridLayout(1, 2));
		this.add(toolPanel, BorderLayout.NORTH);
		toolPanel.setPreferredSize(new Dimension(800, 100));	

		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(4,1));
		this.add(mainPanel, BorderLayout.CENTER);
		
		mainPanel.add(new JScrollPane( insideTable = createTable("Inside", inside) ));
		mainPanel.add(createControl("Inside", this.inside, this.insideTable));
		
		mainPanel.add(new JScrollPane( outsideTable = createTable("Outside", outside) ));
		mainPanel.add(createControl("Outside", this.outside, this.outsideTable));
		
		//toolPanel.add(createControl("Inside", this.inside, this.insideTable));
		//toolPanel.add(createControl("Outside", this.outside, this.outsideTable));
//		new Thread() {
//
//			@Override
//			public void run() {
//				while (true) {
//					SwingUtilities.invokeLater(new Runnable() {
//						@Override
//						public void run() {
//							updateTable();
//						}
//						
//					});
//					try {
//						Thread.sleep(10000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}	
//			}
//			
//		}.start();
		
		updateTable();
	}

	private JPanel createControl(String side, List<PortTestEntity> list, JTable table) {
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(side));
		panel.setLayout(new FlowLayout());
		
		JButton testItemButton = new JButton("Add Test");
		panel.add(testItemButton);
		testItemButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TestItemPanel p = new TestItemPanel(new RestClient(http), Mode.Readonly);
				MyJFrame dlg = new MyJFrame("Test Item", p);
				dlg.setModal(true);
				dlg.setVisible(true);
				if (dlg.isOkClicked()) {
					p.getSelectedItems().forEach(t -> {
						PortTestEntity portTest = new PortTestEntity();
						portTest.setPort(port_id);
						portTest.setDirection(side.equals("Inside") ? 0L : 1L);
						portTest.setTestItem(t.getId());
						try {
							http.post("PortTestEntity", portTest);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					});
					updateTable();
				}
			}
		});
		
		JButton deleteInside = new JButton("Delete " + side);
		panel.add(deleteInside);
		deleteInside.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String ids = selectedIds(list, table);
				http.delete("PortTestEntity?id=" +  ids);
				updateTable();
			}
		});
		
		JButton updateButton = new JButton("Update");
		panel.add(updateButton);
		updateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateTable();
			}
		});
		
		JButton clearResultButton = new JButton("Clear Results");
		panel.add(clearResultButton);
		clearResultButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String ids = selectedIds(list, table);
				http.delete("PortTestEntity/result?id=" + ids);
				updateTable();
			}
		});
		return panel;
	}

	protected void updateTable() {
		this.inside.clear();
		this.outside.clear();
		try {
			PortTestEntity[] portTests = http.getObject("PortTestEntityS?parent=" + port_id, PortTestEntity[].class);
			for (PortTestEntity portTest : portTests) {
				
				String s = "";
				if (portTest.getTestItem() != null) {
					s = portTest.getTestItem().toString() + portTest.getTest_itemEntity().getTest_item();
				}
			
				if (portTest.getDirection() == 1L) {
					inside.add(portTest);
				}
				else {
					outside.add(portTest);
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (AbstractTableModel model : models) {
			model.fireTableDataChanged();
		}
	}

	private JTable createTable(String string, List<PortTestEntity> testItems) {
		List<String> title = Arrays.asList(ID, string, PASS_FAIL, RESULT, CRITERIA, CANDIDATES);
		
		AbstractTableModel model = new AbstractTableModel() {

			@Override
			public String getColumnName(int column) {
				return title.get(column);
			}

			@Override
			public int getRowCount() {
				return testItems.size();
			}

			@Override
			public int getColumnCount() {
				return title.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				PortTestEntity entity =  testItems.get(rowIndex);
				if (title.get(columnIndex).equals(string)) {
					return entity.getTest_itemEntity().toString();
				}
				else if (title.get(columnIndex).equals(ID)) {
					return entity.getId();
				}
				else if (title.get(columnIndex).equals(RESULT))  {
					return entity.getResult();
				}
				else if (title.get(columnIndex).equals(PASS_FAIL))  {
					if (entity.getResult() == null || entity.getResult().isEmpty()) {
						return "";
					}
					return "Pass";
				}
				else if (title.get(columnIndex).equals(CANDIDATES))  {
					String t = "";
					for (TesterEntity tester : testers) {
						for (TesterCapabilityEntity e :  tester.getTestItems()) {
							if (e.getTestItem() == entity.getTestItem()) {
								t += tester.getProduct_name() + "/";
								break;
							}
						}
					}
					if (!t.isEmpty()) {
						return t.substring(0, t.length()-1);
					}
//					if (entity.getTesterObj() == null) {
//						return "";
//					}
//					return findTester(entity.getTester()) + "@" +  entity.getTesterObj().getName();
				}
				return "";
			}
			
		};
		
		models.add(model);
		JTable table = new JTable(model);
		table.setAutoCreateRowSorter(true);
		return table;
		
	}

	protected String findTester(Long tester) {
		for (TesterEntity e : this.testers) {
			if (e.getId().equals(tester)) {
				return e.getProduct_name();
			}
		}
		return "";
	}

	private String selectedIds(List<PortTestEntity> list, JTable table) {
		 String ids= "";
		for (int row : table.getSelectedRows()) {
			ids += list.get(table.convertRowIndexToModel(row)).getId() + ",";
		}
		return ids.substring(0, ids.length()-1);
	}

	@Override
	protected void commit() {
		// TODO Auto-generated method stub
		
	}

}
