package democlient2.topology;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import com.miyake.demo.entities.EquipmentCategoryEntity;
import com.miyake.demo.entities.EquipmentEntity;
import com.miyake.demo.entities.MyTesterEntity;
import com.miyake.demo.entities.PortEntity;

import democlient2.MyHttpClient;
import democlient2.MyJFrame;
import democlient2.TableComboBox;

public class TopologyPanel extends JPanel {
	
	
	private TopologyTable table = null;
	private TopologyUi topology = null;
	
	private EquipmentEntity[] equipments;

	private EquipmentCategoryEntity[] options;

	private MyHttpClient http;

	private Long project_id;
	
	public TopologyPanel(MyHttpClient http, Long project_id) {
		this.http = http;
		this.project_id = project_id;
		
		this.setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		this.add(panel, BorderLayout.NORTH);
		JButton addEquipment = new JButton("Add Equipment");
		panel.add(addEquipment);
		
		try {
			options = http.getObject("EquipmentCategoryEntityS", EquipmentCategoryEntity[].class);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		addEquipment.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EquipmentEntity equipment = new EquipmentEntity();
				equipment.setCategory(1L);
				equipment.setName("---");
				equipment.setProject(project_id);
				try {
					http.post("EquipmentEntity", equipment);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//table.update(http.getObject("equipments?project=" + id, EquipmentEntity[].class));
				updateTables();
			}
		});
		JButton deleteEquipment = new JButton("Delete");
		panel.add(deleteEquipment);
		deleteEquipment.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				http.delete("EquipmentEntity?id=" + table.getSelectedId());
				updateTables();
			}
		});
		
		JButton publishButton  =new JButton("Publish");
		panel.add(publishButton);
		publishButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String ids = table.getSelectedIds().toString().replace("[", "").replace("]","");
				
				http.get("EquipmentEntity/publish?equipmentids=" + ids);
				
			}
		});
		
		JButton addPort = new JButton("Add Port");
		panel.add(addPort);
		addPort.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String value = JOptionPane.showInputDialog("Add Port", "8");
				if (value == null || value.isEmpty()) {
					return;
				}
				http.get("EquipmentEntity/addport?id=" + table.getSelectedId() + "&count=" + value);
				updateTables();
			}
		});
		
		JTabbedPane tabbedPane = new JTabbedPane();
		this.add(tabbedPane, BorderLayout.CENTER);
		try {
			equipments = http.getObject("EquipmentEntityS?parent=" + project_id, EquipmentEntity[].class);			
//			table = createTable(http, equipments);
//			tabbedPane.addTab("Table", table);
			tabbedPane.addTab("Topology", topology = createTopology(http, equipments));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void updateTables() {
		try {
//			
			equipments = http.getObject("EquipmentEntityS?parent=" + project_id, EquipmentEntity[].class);
			this.table.update();
			this.topology.update();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private TopologyTable createTable(MyHttpClient http, EquipmentEntity[] equipments) {
		return new TopologyTable() {
			@Override
			void onUpdate() {
				updateTables();
			}
		};
	}

	private TopologyUi createTopology(MyHttpClient http, EquipmentEntity[] equipments) {
		return new TopologyUi() {

			@Override
			void onUpdate() {
				updateTables();
			}
			
		};
	}

	abstract class TopologyUi extends JPanel {
		abstract void onUpdate();
		private Map<JButton, PortEntity> buttons = new HashMap<>();
		private Map<Long, PortEntity> buttons2 = new HashMap<>();
		private Map<PortEntity, JButton> buttons3 = new HashMap<>();
		
		private boolean dragging = false;
		
		private int start_x;
		private int start_y;
		private int end_x;
		private int end_y;
		private JButton currentButton;
		JCheckBox linkVisible;
		
		private MouseListener mouseListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					PortEntity portEntity = buttons.get(e.getSource());
					MyJFrame frame = new MyJFrame(portEntity.getPort_name(), new PortPanel(http, portEntity.getId()));
					frame.setModal(true);
					frame.setVisible(true);				
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					dragging = !dragging;
					
					JButton button = (JButton)e.getSource();
					if (dragging) {
						currentButton = button;
						start_x = currentButton.getLocation().x + currentButton.getWidth()/2;
						start_y = currentButton.getLocation().y + currentButton.getHeight() / 2;
					}
					else {
						if (button.equals(currentButton)) {
							return;
						}
						PortEntity from = buttons.get(currentButton);
						PortEntity to = buttons.get(button);
						from.setOpposite(to.getId());
						try {
							http.post("PortEntity", from);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						onUpdate();
					}
				}
			}
			
		};
		private MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (dragging) {
					JButton button = (JButton)e.getSource();
					end_x = button.getLocation().x + /*button.getWidth()/2 +*/ e.getPoint().x;
					end_y = button.getLocation().y + /*button.getHeight() / 2*/ + e.getPoint().y;
					
					TopologyUi.this.repaint();
				}
			}
		};
		private MouseMotionListener panelMouseListener = new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (dragging) {
					end_x = /*button.getWidth()/2 +*/ e.getPoint().x;
					end_y = /*button.getHeight() / 2*/ + e.getPoint().y;	
					TopologyUi.this.repaint();
				}
			}
			
		};
		
		public TopologyUi() {
			setLayout(null);
			linkVisible = new JCheckBox("Link Visible");
			//linkVisible.setEnabled(true);
			this.add(linkVisible);
			linkVisible.setBounds(0, 0, 200, 24);
			linkVisible.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					TopologyUi.this.repaint();
				}
			});
			
			update();
		}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			
			if (!linkVisible.isSelected()) {
				return;
			}
			Graphics2D g2 = (Graphics2D)g;

			if (dragging) {
				g2.drawLine(start_x, start_y, end_x, end_y);
			}
			
			for (Map.Entry<JButton, PortEntity> entry : buttons.entrySet()) {
				if (entry.getValue().getOpposite() != null) {
					PortEntity portEntity = buttons2.get(entry.getValue().getOpposite());
					if (portEntity == null) {
						continue;
					}
					JButton button2 = entry.getKey();
					JButton button = buttons3.get(portEntity);
					
					if (button.getY() > button2.getY()) {
						g2.drawLine(button2.getX() + button2.getWidth()/2, button2.getY() + button2.getHeight(), 
								button.getX() + button.getWidth()/2, button.getY());				
					}
					else {
						g2.drawLine(button2.getX() + button2.getWidth()/2, button2.getY(), 
								button.getX() + button.getWidth()/2, button.getY() + button.getHeight());					
					}

				}
			}
		}

		int count = 0;
		public void update() {
			this.removeAll();
			this.add(linkVisible);
			
			//buttons.forEach((b,a) -> {remove(b);});
			this.buttons.clear();
			this.buttons2.clear();
			this.buttons3.clear();
			
			int y = 80;
			int x = 30;
			int width = 150;
			int height = 40;

			this.addMouseMotionListener(panelMouseListener);
			
			OrderCalculator orderCalculator = new OrderCalculator(http, equipments);
			
			for (EquipmentEntity equipment : orderCalculator.getEquipments()) {
//				JCheckBox check = new JCheckBox("");
//				check.setBounds(10, y, 20, 20);
//				add(check);
				
				JButton button = new JButton("<html>" + equipment.getName() + "</html>");
				
				button.setBounds(x, y, width, height);
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						MyJFrame frame = new MyJFrame(button.getText(), new EquipmentPanel2(http, equipment.getId()) {

							@Override
							protected void onUpdate() {
								updateTables();
							}
							
							
						});
						frame.setModal(true);
						frame.setVisible(true);
						frame.setModal(true);
						if (frame.isOkClicked()) {
							updateTables();
						}
					}
				});
				add(button);
				
				PortEntity[] ports = orderCalculator.getPorts(equipment);//http.getObject("ports?equipment=" + equipment.getId(), PortEntity[].class);
				int button_width = 30;
				int button_height = 40;
				
				for (int i = 0; i < ports.length; i++) {
					PortEntity port = ports[i];
					JButton portButton = new JButton("<html>" + port.getPort_name() + "</html>");
					portButton.setFont(new Font("Arial", Font.PLAIN, 8));
					portButton.setMargin(new Insets(0, 0, 0, 0));
					portButton.setToolTipText(port.getPort_name());
					add(portButton);
					portButton.setBounds(x + width + i*button_width, y, button_width, button_height);
					
					portButton.addMouseListener(mouseListener);
					portButton.addMouseMotionListener(mouseMotionListener);
					
					buttons.put(portButton, port);
					buttons2.put(port.getId(), port);
					buttons3.put(port, portButton);
				}
				
				
				y += height + 10;
			}	
		}

	}
	
	abstract class TopologyTable extends JPanel {
		abstract void onUpdate();
		
		public Long getSelectedId() {
			return equipments[table.getSelectedRow()].getId();
		}
		
		public List<Long> getSelectedIds() {
			List<Long> ret = new ArrayList<>();
			for (int row : table.getSelectedRows()) {
				ret.add(equipments[table.convertRowIndexToModel(row)].getId());
			}
			return ret;
		}
		
		private static final String CATEGORY = "Category";
		private static final String NAME = "Name";
		private AbstractTableModel model;
		private JTable table = null;
		
		public TopologyTable() {
			
			List<String> title = Arrays.asList(NAME, CATEGORY);

			model = new AbstractTableModel() {

				@Override
				public String getColumnName(int column) {
					return title.get(column);
				}

				@Override
				public int getRowCount() {
					return equipments.length;
				}

				@Override
				public int getColumnCount() {
					return title.size();
				}

				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					EquipmentEntity equipment = equipments[rowIndex];
					
					if (title.get(columnIndex).equals(NAME)) {
						return equipment.getName();
					}
					else if (title.get(columnIndex).equals(CATEGORY)) {
						return equipment.getCategoryEntity().getCategory();
					}
					return "";
				}

				@Override
				public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
					EquipmentEntity equipment = equipments[rowIndex];
					if (title.get(columnIndex).equals(NAME)) {
						equipment.setName(aValue.toString());
					}
					else if (title.get(columnIndex).equals(CATEGORY)) {
						for (EquipmentCategoryEntity option : options) {
							if (option.toString().equals(aValue.toString())) {
								equipment.setCategory(option.getId());
								break;
							}
						}
						
					}
					try {
						http.post("EquipmentEntity", equipment);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					onUpdate();
				}

				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return true;
				}
				
			};
			
		    table = new JTable(model);

		    new TableComboBox<EquipmentCategoryEntity>(table, options, title.indexOf(CATEGORY));

			this.add(new JScrollPane(table));
		}

		public void update() {
			this.model.fireTableDataChanged();
		}
	}
}

