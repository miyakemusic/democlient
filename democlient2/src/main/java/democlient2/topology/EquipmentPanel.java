package democlient2.topology;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.miyake.demo.entities.ConnectorEntity;
import com.miyake.demo.entities.PortEntity;
import com.miyake.demo.entities.PortTestEntity;

import democlient2.MyHttpClient;
import democlient2.MyJFrame;

public class EquipmentPanel extends JPanel {

	private static final String EDIT = "EDIT";
	private static final String CONNECTOR = "Connector";
	private static final String PORT_NAME = "Port Name";
	private static final String INSIDE_TEST_ITEMS = "Inside Test Items";
	private static final String OUTSIDE_TEST_ITEMS = "Outside Test Items";
	private static final String OPPOSITE = "Opposite Side";
	private static final String LENGTH = "Length";

	private PortEntity[] ports;
	private AbstractTableModel model;
	private MyHttpClient http;
	private Long equipment_id;
	protected void onUpdate() {}
	
	public EquipmentPanel(MyHttpClient http, Long equipment_id) {
		this.setLayout(new BorderLayout());
		this.http = http;
		this.equipment_id = equipment_id;
		try {			
			List<String> title = Arrays.asList(PORT_NAME, CONNECTOR, OPPOSITE, LENGTH, INSIDE_TEST_ITEMS, OUTSIDE_TEST_ITEMS, EDIT);
			model = new AbstractTableModel() {

				@Override
				public String getColumnName(int column) {
					return title.get(column);
				}

				@Override
				public int getRowCount() {
					return ports.length;
				}

				@Override
				public int getColumnCount() {
					return title.size();
				}

				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					PortEntity port = ports[rowIndex];
					if (title.get(columnIndex).equals(PORT_NAME)) {
						return port.getPort_name();
					}
					else if (title.get(columnIndex).equals(CONNECTOR)) {
						if (port.getConnector_typeEntity() != null) {
							return port.getConnector_typeEntity().getName();
						}
					}
					else if (title.get(columnIndex).equals(INSIDE_TEST_ITEMS)) {
						return createtText(port.getPortTests(), 0);
					}
					else if (title.get(columnIndex).equals(OUTSIDE_TEST_ITEMS)) {
						return createtText(port.getPortTests(), 1);
					}
					else if (title.get(columnIndex).equals(OPPOSITE)) {
						return port.getOpposite();
					}
					else if (title.get(columnIndex).equals(LENGTH)) {
						return port.getFiber_length();
					}
					return "";
				}

				private String createtText(List<PortTestEntity> portTests, int i) {
					String ret = "";
					for (PortTestEntity p : portTests) {
						if (p.getDirection() == i) {
							if (p.getTest_itemEntity() != null) {
								ret += p.getTest_itemEntity().toString();
							}
						}
					}
					return ret;
				}

				@Override
				public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
					PortEntity port = ports[rowIndex];
					if (title.get(columnIndex).equals(PORT_NAME)) {
						port.setPort_name(aValue.toString());
					}
					else if (title.get(columnIndex).equals(CONNECTOR)) {
						ConnectorEntity c = ((ConnectorEntity)aValue);
						port.setConnector_type(c.getId());
						
					}
					try {
						http.post("PortEntity", port);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					updateTable();
				}

				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return true;
				}
			};
			
			JTable table = new JTable(model);
			
			ConnectorEntity[] connectors = http.getObject("connectors", ConnectorEntity[].class);
			
    	    JComboBox<ConnectorEntity> combo = new JComboBox<>();
    	    for (ConnectorEntity connector : connectors) {
    	    	combo.addItem(connector);
    	    }
    	    
    	    TableColumn col = table.getColumnModel().getColumn(title.indexOf(CONNECTOR));
		    col.setCellEditor(new DefaultCellEditor(combo));
		    
		    ButtonCellRenderer renderer = new ButtonCellRenderer(table, title.indexOf(EDIT)) {

				@Override
				protected void onClick(int row) {
					PortEntity portEntity = ports[row];
					new MyJFrame(portEntity.getPort_name(), new PortPanel(http, portEntity.getId())).modal().setVisible(true);;
		
				}
		    	
		    };
	        TableColumn column0 = table.getColumnModel().getColumn(title.indexOf(EDIT));
	        column0.setCellEditor(renderer);
	        column0.setCellRenderer(renderer);
	        
			this.add(new JScrollPane(table));
			
			JPanel toolBar = new JPanel();
			toolBar.setLayout(new FlowLayout());
			JButton addButton = new JButton("Add");
			this.add(toolBar, BorderLayout.NORTH);
			toolBar.add(addButton);
			addButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					PortEntity port = new PortEntity();
					port.setEquipment(equipment_id);
					port.setPort_name("---");
					try {
						http.post("PortEntity", port);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					updateTable();
				}
			});
			
			JButton deleteButton = new JButton("Delete");
			toolBar.add(deleteButton);
			deleteButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					PortEntity delPort = ports[table.getSelectedRow()];
					http.delete("PortEntity?id=" + delPort.getId());
					updateTable();
				}
			});
			
			JButton copyButton = new JButton("Copy to others");
			toolBar.add(copyButton);
			copyButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					PortEntity copyPort = ports[table.getSelectedRow()];
					try {
						http.post("PortEntity/copy", copyPort);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					updateTable();
				}
			});			
			updateTable();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void updateTable() {
		try {
			ports = http.getObject("PortEntityS?equipment=" + equipment_id, PortEntity[].class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.fireTableDataChanged();
	}

}

abstract class ButtonCellRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

	private final JButton button;

	public ButtonCellRenderer(final JTable table, int column) {
		this.button = new JButton("Edit");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireEditingStopped(); 
			}
		});
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Point point = e.getPoint();
				if (table.columnAtPoint(point) == column) {
					fireEditingStopped();
					int row = table.rowAtPoint(point);
						onClick(row);
				}
			}
		});
	}

	abstract protected void onClick(int row);

	public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		return button;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		return button;
	}

	public Object getCellEditorValue() {
		return button.getText();
	}
}