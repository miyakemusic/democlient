package democlient2.topology2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import com.miyake.demo.entities.EquipmentEntitySimple;
import com.miyake.demo.entities.EquipmentPresentationEntity;
import com.miyake.demo.entities.PortEntitySimple;
import com.miyake.demo.jsonobject.DiagramItemContainers;

import democlient2.MyJFrame;
import democlient2.RestClient;
import democlient2.topology2.DraggablePanel2.DragTarget;

public class TopologyPanel3 extends JPanel {
	private Map<Long, EquipmentPresentationEntity> equipmentPresentationMap = new HashMap<>();
	private Map<Long, EquipmentEntitySimple> equipmentMap= new HashMap<>();
//	private int height = 50;
//	private int width = 150;
	private int grid_width = 20;
	private int grid_height = 20;
	
	private int unit_width = grid_width * 5;
	private int unit_height = grid_height * 2;
	
	private int xoffset = 20;
	private int yoffset = 20;
	private RestClient restClient;
	
	public TopologyPanel3(RestClient restClient, Long projectid) {
		this.restClient = restClient;
		
		DraggablePanel2 panel = new DraggablePanel2(xoffset, yoffset, unit_width, unit_height) {
			@Override
			protected void onEntityPositionUpdate(Long id, int x, int y, int w, int h) {
				EquipmentPresentationEntity entity = equipmentPresentationMap.get(id);
				entity.setX(x);
				entity.setY(y);
				entity.setWidth(w);
				entity.setHeight(h);
				try {
					restClient.post(entity);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void onButtonClick(Long id) {
				boolean ret = showPortEditor(equipmentMap.get(id), restClient);
				if (ret) {
					createButtons(projectid, restClient, this);
				}
			}

			@Override
			protected void onLinkSelected(Long id, Long id2) {
				showLinkEditor(equipmentMap.get(id), equipmentPresentationMap.get(id), 
						equipmentMap.get(id2), equipmentPresentationMap.get(id2), restClient);				
			}

			@Override
			protected void onSelecteComplete(List<MovableButton> selectedButtons2) {
			}		
			
		}.grid(grid_width, grid_height).dragTarget(DragTarget.Move);
		
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(panel), BorderLayout.CENTER);
		panel.setLayout(null);
		
		createButtons(projectid, restClient, panel);

		JPanel toolBar = new JPanel();
		toolBar.setLayout(new FlowLayout());

		JButton linkButton = new JButton("Link");
		toolBar.add(linkButton);
		linkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean ret = showLinkEditor(panel.getSelectedButtons(), xoffset, yoffset, restClient);
				if (ret) {
					updateEquipmentMap(restClient, projectid);
					updateLinks(panel);
					
				}
			}
		});
		
		JButton addNew = new JButton("Add");
		toolBar.add(addNew);
		addNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				restClient.addNewEquipment(projectid);
				createButtons(projectid, restClient, panel);
			}
		});
		
		this.add(toolBar, BorderLayout.NORTH);
				
		this.updateUI();
	}

	private void createButtons(Long projectid, RestClient restClient, DraggablePanel2 panel) {
		panel.clear();
		EquipmentEntitySimple[] EquipmentEntity = updateEquipmentMap(restClient, projectid);
		for (EquipmentEntitySimple entity: EquipmentEntity) {
			EquipmentPresentationEntity presentationEntity = equipmentPresentationMap.get(entity.getId());
			final MovableButton movableButton = panel.createButton(entity.getName(), entity.getId());
			
			JPopupMenu popup = new JPopupMenu();
			JMenuItem editMenu = new JMenuItem("Edit");
			popup.add(editMenu);
			editMenu.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showPortEditor(equipmentMap.get(entity.getId()), restClient);
				}
			});
			JMenuItem copyMenu = new JMenuItem("Duplicate");
			popup.add(copyMenu);
			copyMenu.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					restClient.copyEquipment(entity.getId());
					createButtons(projectid, restClient, panel);
				}
			});
			
			JMenuItem copyMultiMenu = new JMenuItem("Multiple Copy");
			popup.add(copyMultiMenu);
			copyMultiMenu.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String value = JOptionPane.showInputDialog("Add Port", "8");
					if (value == null || value.isEmpty()) {
						return;
					}
					restClient.copyEquipment(entity.getId(), Integer.valueOf(value));
					createButtons(projectid, restClient, panel);
				}
			});
			
			JMenuItem deleteMenu = new JMenuItem("Delete");
			popup.add(deleteMenu);
			deleteMenu.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (panel.getSelectedIds().size() == 0) {
						restClient.deleteEquipment(entity.getId());
					}
					else {
						restClient.deleteEquipments(panel.getSelectedIds());
					}
					createButtons(projectid, restClient, panel);
				}
			});
			
			JMenuItem alignMenu = new JMenuItem("Align");
			popup.add(alignMenu);
			alignMenu.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					panel.alignVertical();
					//createButtons(projectid, restClient, panel);
				}
			});
			
			JMenuItem createTestScenarioMenu = new JMenuItem("Create Test Scenario");
			popup.add(createTestScenarioMenu);
			createTestScenarioMenu.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String candidateName = "";
					if (panel.getSelectedIds().size() > 0) {
						for (Long id: panel.getSelectedIds()) {
							candidateName += TopologyPanel3.this.equipmentMap.get(id).getName() + "_";
						}
					}
					else {
						Long id = ((MovableButton)e.getSource()).getId();
						candidateName = TopologyPanel3.this.equipmentMap.get(id).getName();
					}
					candidateName = candidateName.substring(0, candidateName.length()-1);
					String value = JOptionPane.showInputDialog("Test Scenario Name", candidateName);
					if (value == null || value.isEmpty()) {
						return;
					}
					restClient.createTestCase(panel.getSelectedIds(), candidateName);
				}
			});
			
			movableButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON3) {
						popup.show(movableButton, e.getX(), e.getY());
						
					}
				}				
			});
			movableButton.setBounds(yoffset + presentationEntity.getX(), yoffset + presentationEntity.getY(), unit_width, unit_height);
		}
		updateLinks(panel);
		panel.updateUI();
	}

	private EquipmentEntitySimple[] updateEquipmentMap(RestClient restClient, Long projectid) {
		EquipmentEntitySimple[] equipmentEntities = restClient.equipmentsSimple(projectid);
		for (EquipmentEntitySimple e : equipmentEntities) {
			equipmentMap.put(e.getId(), e);
		}
		for (EquipmentPresentationEntity entity : restClient.equipmentPresentations(projectid)) {
			equipmentPresentationMap.put(entity.getEquipment(), entity);
		}		
		return equipmentEntities;
	}

	private void updateLinks(DraggablePanel2 panel) {
		// calc link
		Map<PortEntitySimple, EquipmentEntitySimple> portMap = new HashMap<>();
		Map<Long, PortEntitySimple> portMap2 = new HashMap<>();
		for (EquipmentEntitySimple e :  this.equipmentMap.values()) {
			PortEntitySimple[] ports = restClient.portsByEquipment(e.getId());
			for (PortEntitySimple p : ports) {
				portMap.put(p, e);
				portMap2.put(p.getId(), p);
			}
		}
		panel.clearPair();
		for (Map.Entry<PortEntitySimple, EquipmentEntitySimple> entry : portMap.entrySet()) {
			EquipmentEntitySimple equipment = entry.getValue();
			PortEntitySimple portEntity = entry.getKey();
			if (portEntity.getOpposite() != null) {
				PortEntitySimple oppositePort = portMap2.get(portEntity.getOpposite());
				EquipmentEntitySimple oppositeEquipment = portMap.get(oppositePort);
				if (equipment != null && oppositeEquipment != null) {
					panel.addLinkPair(equipment.getId(), oppositeEquipment.getId());
				}
			}
		}
	}
	
	protected boolean showLinkEditor(List<MovableButton> selectedButtons, int xoffset, int yoffset, RestClient restClient) {
		LinkEditor linkEditor = new LinkEditor(selectedButtons, equipmentMap, restClient) {
		};
		MyJFrame dlg = new MyJFrame(linkEditor.getTitle(), linkEditor);
		dlg.setSize(new Dimension(1000, 500));
		dlg.modal();
		dlg.setVisible(true);
		
		return dlg.isOkClicked();
	}

	protected void showLinkEditor(
			EquipmentEntitySimple equipmentEntity, EquipmentPresentationEntity equipmentPresentationEntity, 
			EquipmentEntitySimple equipmentEntity2, EquipmentPresentationEntity equipmentPresentationEntity2, 
			RestClient restClient) {

	}

	protected boolean showPortEditor(EquipmentEntitySimple equipment, RestClient restClient) {
		MyJFrame dlg = new MyJFrame("Equipment Editor", new PortListPanel2(equipment, restClient));
		dlg.modal();
		dlg.setVisible(true);
		return dlg.isOkClicked();
	}

}
