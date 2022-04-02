package testers;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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

import com.miyake.demo.entities.TestScenarioEntity;
import com.miyake.demo.jsonobject.TestPlan2;
import com.miyake.demo.jsonobject.TestPlan2Element;
import democlient2.RestClient;

class EquipmentObj {
	public EquipmentObj(Long id, String name) {
		this.id = id;
		this.name = name;
	}
	public Long id;
	public String name;
	@Override
	public String toString() {
		return this.name;
	}
}
public class AutomationPane extends JPanel {
	private static final String PASS_FAIL = "PASS/FAIL";
	private static final String CRITERIA = "CRITERIA";
	private static final String RESULT = "RESULT";
	private static final String TEST_ITEM = "TEST ITEM";
	private static final String DIRECTION = "DIRECTION";
	private static final String PORT = "PORT";
	private static final String MY_TEST = "My Test";
	private static final String EQUIPMENT = "EQUIPMENT";
	private static final String ID = "ID";
	private TestPlan2 testPlan = new TestPlan2();

	public AutomationPane(RestClient restClient, Automator automator) {
		this.setLayout(new BorderLayout());
		
		JPanel equipmentPanel = new JPanel();
		equipmentPanel.setLayout(new FlowLayout());
		this.add(equipmentPanel, BorderLayout.SOUTH);
		
		List<String> title = Arrays.asList(EQUIPMENT, MY_TEST, PORT, DIRECTION, TEST_ITEM, CRITERIA, RESULT, PASS_FAIL);
		
		AbstractTableModel model = new AbstractTableModel() {
			@Override
			public String getColumnName(int column) {
				return title.get(column);
			}

			@Override
			public int getRowCount() {
				return testPlan.filteredElements().size();
			}

			@Override
			public int getColumnCount() {
				return title.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				TestPlan2Element element = testPlan.filteredElements().get(rowIndex);
				
				if (title.get(columnIndex).equals(ID)) {
					return element.getPorttest();
				}
				else if (title.get(columnIndex).equals(EQUIPMENT)) {
					return testPlan.presentation().equipment(element.getEquipment());
				}
				else if (title.get(columnIndex).equals(MY_TEST)) {
					return element.isMytest();
				}
				else if (title.get(columnIndex).equals(PORT)) {
					return testPlan.presentation().port(element.getPort());
				}
				else if (title.get(columnIndex).equals(DIRECTION)) {
					return element.getDirection();
				}
				else if (title.get(columnIndex).equals(TEST_ITEM)) {
					return testPlan.presentation().getTestItem().get(element.getTestitem());
				}
				else if (title.get(columnIndex).equals(RESULT)) {
					return element.getResult();
				}
				else if (title.get(columnIndex).equals(CRITERIA)) {
					return element.getCriteria();
				}			
				else if (title.get(columnIndex).equals(PASS_FAIL)) {
					return element.getPassFail().name();
				}			
				
				return "";
			}			
		};
				
		TestScenarioEntity[] testScenarios = restClient.testPlanList();
		
		JComboBox<TestScenarioEntity> equipmentCombo = new JComboBox<>();
		for (TestScenarioEntity testScenario : testScenarios) {
			equipmentCombo.addItem(testScenario);
		}

		equipmentCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testPlan = restClient.testPlan(((TestScenarioEntity)equipmentCombo.getSelectedItem()).getId());//.filterEquipment(((EquipmentObj)equipmentCombo.getSelectedItem()).id);
				model.fireTableDataChanged();
			}
		});
		
		JTable table = new JTable(model);
		this.add(new JScrollPane(table), BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.add(equipmentCombo);
		panel.setLayout(new FlowLayout());
		this.add(panel, BorderLayout.NORTH);
		
		JButton retreiveTestPlan = new JButton("Get");
		panel.add(retreiveTestPlan);
		retreiveTestPlan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testPlan = restClient.testPlan(((TestScenarioEntity)equipmentCombo.getSelectedItem()).getId());//.filterEquipment(((EquipmentObj)equipmentCombo.getSelectedItem()).id);
				model.fireTableDataChanged();
			}
		});
		
		JComboBox<String> sortCombo = new JComboBox<>();
		sortCombo.addItem("--- Sort ---");
		sortCombo.addItem("Port");
		sortCombo.addItem("Tester");
		sortCombo.addItem("Direction");
		sortCombo.addItem("Sort");
		sortCombo.addItem("Equipment");
		sortCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (sortCombo.getSelectedItem().toString().equals("Port")) {
					testPlan.sortByPort();
				}
				else if (sortCombo.getSelectedItem().toString().equals("Tester")) {
					testPlan.sortByTester();
				}
				else if (sortCombo.getSelectedItem().toString().equals("Direction")) {
					testPlan.sortByDirection();
				}
				else if (sortCombo.getSelectedItem().toString().equals("Equipment")) {
					testPlan.sortByEquipment();
				}
				model.fireTableDataChanged();
			}
		});
		panel.add(sortCombo);
		
//		JPanel functions = new JPanel();
//		functions.setLayout(new GridLayout(5,1));
//		this.add(functions, BorderLayout.EAST);
		JButton runAll = new JButton("Run All");
		panel.add(runAll);
		runAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				automator.runAll(testPlan);
			}
		});
		
		JButton runSingle = new JButton("Run Single");
		panel.add(runSingle);
		runSingle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				automator.runSingle(testPlan, table.getSelectedRow());
			}
		});
		
		JButton clearResult = new JButton("Clear Results");
		panel.add(clearResult);
		clearResult.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				restClient.clearResults(((TestScenarioEntity)equipmentCombo.getSelectedItem()).getId());
				model.fireTableDataChanged();
			}
		});
		
		
		restClient.webSocket("ws://localhost:8080/ws", new MyWebSocketCallback() {
			@Override
			public void onResultUpdate(TestPlan2Element testPlan2Element) {
				testPlan.mergeResult(testPlan2Element);
				model.fireTableDataChanged();
			}

			@Override
			public void onRequestTest(TestPlan2Element testPlan2Element) {
				int i = testPlan.findIndex(testPlan2Element);
				
//				int index = testPlan.getElements().indexOf(testPlan2Element);
				if (testPlan.getElements().get(i).isMytest()) {
					automator.runAllFrom(testPlan, i);
				}
				//boolean valid = automator.runIfValid(testPlan, index);
			}
		});
		equipmentPanel.updateUI();	
	}

}
