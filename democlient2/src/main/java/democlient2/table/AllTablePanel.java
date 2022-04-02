package democlient2.table;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.miyake.demo.entities.EquipmentEntity;
import com.miyake.demo.entities.PortEntity;
import com.miyake.demo.entities.PortTestEntity;
import com.miyake.demo.jsonobject.TestPlan2Element;

import democlient2.MyJPanel;
import democlient2.RestClient;
import testers.MyWebSocketCallback;

class MyRow {
	public String equipment = "";
	public String port = "";
	public String test = "";
	public String direction = "";
	public String result = "";
	public String passFail = "";
	public String criteria;
	public MyRow(String equipment, String port, String direction, String test, String criteria, String result, String passfail) {
		this.equipment = equipment;
		this.port = port;
		this.test = test;
		this.direction = direction;
		this.result = result;
		this.passFail = passfail;
		this.criteria = criteria;
	}

	public MyRow(String comment) {
		this.equipment = comment;
	}
	

	
}
public class AllTablePanel extends MyJPanel {
	private static final String CRITERIA = "Criteria";
	private static final String PASS_FAIL = "Pass/Fail";
	private static final String RESULT = "Result";
	private static final String TEST = "Test";
	private static final String DIRECTION = "Direction";
	private static final String PORT = "Port";
	private static final String EQUIPMENT = "Equipment";
	private List<MyRow> rows;
	private List<EquipmentEntity> equipments;
	
	public AllTablePanel(Long projectid, RestClient restClient) {
		this.setLayout(new BorderLayout());
		rows = updateTable(projectid, restClient);
		List<String> title = Arrays.asList(EQUIPMENT, PORT, DIRECTION, TEST, CRITERIA, RESULT, PASS_FAIL);
		AbstractTableModel model = new AbstractTableModel() {

			@Override
			public String getColumnName(int column) {
				return title.get(column);
			}

			@Override
			public int getRowCount() {
				return rows.size();
			}

			@Override
			public int getColumnCount() {
				return title.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				MyRow row = rows.get(rowIndex);
				if (title.get(columnIndex).equals(EQUIPMENT)) {
					return " " + row.equipment;
				}
				else if (title.get(columnIndex).equals(PORT)) {
					return row.port;
				}
				else if (title.get(columnIndex).equals(TEST)) {
					return row.test;
				}
				else if (title.get(columnIndex).equals(DIRECTION)) {
					return row.direction;
				}
				else if (title.get(columnIndex).equals(RESULT)) {
					return row.result;
				}
				else if (title.get(columnIndex).equals(PASS_FAIL)) {
					return row.passFail;
				}
				else if (title.get(columnIndex).equals(CRITERIA)) {
					return row.criteria;
				}				
				
				return "";
			}
			
		};
		
		JTable table = new JTable(model);
		this.add(new JScrollPane(table), BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		this.add(panel, BorderLayout.NORTH);
		JButton updateButton = new JButton("Update");
		panel.add(updateButton);
		updateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rows = updateTable(projectid, restClient);
				model.fireTableDataChanged();
			}
		});
		
		restClient.webSocket("ws://localhost:8080/ws", new MyWebSocketCallback() {

			@Override
			public void onResultUpdate(TestPlan2Element testPlan2Element) {
				rows = replaceResult(testPlan2Element);
				model.fireTableDataChanged();
			}

			@Override
			public void onRequestTest(TestPlan2Element object) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	protected List<MyRow> replaceResult(TestPlan2Element testPlan2Element) {
		for (EquipmentEntity equipment : equipments) {
			if (equipment.getId().equals(testPlan2Element.getEquipment())) {
				for (PortEntity port : equipment.getPorts()) {
					for (PortTestEntity portTest  : port.getPortTests()) {
						if (portTest.getId().equals(testPlan2Element.getPorttest())) {
							portTest.setResult(testPlan2Element.getResult());
							portTest.setPassfail(testPlan2Element.getPassFail());
							break;
						}
					}
				}
			}
		}
		return generateRows();
	}

	private List<MyRow> updateTable(Long projectid, RestClient restClient) {
		equipments = new ArrayList<>(Arrays.asList(restClient.equipments(projectid)));
		List<MyRow> rows = generateRows();
		return rows;
	}

	private List<MyRow> generateRows() {
		List<MyRow> rows = new ArrayList<>();
		for (EquipmentEntity equipment : equipments) {
			rows.add(new MyRow("********** " + equipment.getName() + " **********"));
			for (PortEntity port : equipment.getPorts()) {
				rows.add(new MyRow("=" + port.getPort_name() + "="));
				for (PortTestEntity test : port.getPortTests()) {
					rows.add(new MyRow(equipment.getName(), port.getPort_name(), test.getDirectionEntity().getName(), 
							test.getTest_itemEntity().getTest_item(), test.getCriteria(), test.getResult(), test.getPassfail().name()));
					
				}
			}
		}
		return rows;
	}

	@Override
	protected void commit() {
		// TODO Auto-generated method stub
		
	}

}
