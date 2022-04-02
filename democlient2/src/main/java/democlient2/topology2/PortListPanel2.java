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
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import com.miyake.demo.entities.EquipmentEntity;
import com.miyake.demo.entities.EquipmentEntitySimple;
import com.miyake.demo.entities.PortEntity;
import com.miyake.demo.entities.PortEntitySimple;
import com.miyake.demo.entities.PortPresentationEntity;
import com.miyake.demo.entities.PortTestTemplateEntity;

import democlient2.MyJFrame;
import democlient2.MyJPanel;
import democlient2.MyTextEditBox;
import democlient2.RestClient;



public class PortListPanel2 extends MyJPanel {

	private Map<Long, PortPresentationEntity> portPresentationMap = new HashMap<>();
	private Map<Long, PortEntitySimple> portMap = new HashMap<>();
	private MyTextEditBox nameEditor;
	private RestClient restClient;
	//private EquipmentEntity equipment;
	
	private int grid_width = 20;
	private int grid_height = 20;
	
	private int unit_height = grid_height * 1;
	private int unit_width = grid_width * 5;
	
	private int xoffset = 20;
	private int yoffset = 20;
	private Long equipmentId;
	
	public PortListPanel2(EquipmentEntitySimple equipment, RestClient restClient) {
		this.equipmentId = equipment.getId();
		this.restClient = restClient;
//		this.equipment = equipment;
		
		this.setLayout(new BorderLayout());
		
		JPanel toolBar = new JPanel();
		toolBar.setLayout(new FlowLayout());
		this.add(toolBar, BorderLayout.NORTH);
		toolBar.add(nameEditor = new MyTextEditBox("Equipment Name", equipment.getName()));
		
		JButton alignButton = new JButton("Align");
		toolBar.add(alignButton);
		
		JButton addButton = new JButton("Add Port");
		toolBar.add(addButton);
		
		DraggablePanel2 panel = new DraggablePanel2(xoffset, yoffset, unit_width, unit_height) {
			@Override
			protected void onEntityPositionUpdate(Long id, int x, int y, int w, int h) {
				PortPresentationEntity entity = portPresentationMap.get(id);
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
				boolean ret = showPortDetail(portMap.get(id), restClient);
				if (ret) {
					createButtons(restClient, this);		
				}
			}

			@Override
			protected void onLinkSelected(Long id, Long id2) {

			}
		}.grid(grid_width, grid_height);
				
		alignButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.alignVertical();
			}
		});
		
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				restClient.createPort(equipment.getId());
				createButtons(restClient, panel);
			}
		});
		
		this.add(new JScrollPane(panel), BorderLayout.CENTER);
		panel.setLayout(null);
		
//		panel.setPreferredSize(new Dimension(2000, 2000));
		createButtons(restClient, panel);

		panel.updateUI();
	}

	private void createButtons(/*EquipmentEntity equipment, */RestClient restClient, DraggablePanel2 panel) {
		panel.clear();
		portPresentationMap.clear();
		portMap.clear();
		PortPresentationEntity[] presentations = restClient.portPresentation(this.equipmentId);
		for (PortPresentationEntity entity : presentations) {
			portPresentationMap.put(entity.getPort(), entity);
		}
		
		for (PortEntitySimple entity: restClient.portsSimple(this.equipmentId)) {
			portMap.put(entity.getId(), entity);
			PortPresentationEntity presentationEntity = portPresentationMap.get(entity.getId());
			MovableButton movableButton = panel.createButton(entity.getPort_name(), entity.getId());
			movableButton.setBounds(yoffset + presentationEntity.getX(), yoffset + presentationEntity.getY(), unit_width, unit_height);
			
			JPopupMenu popup = new JPopupMenu();
			JMenuItem editMenu = new JMenuItem("Edit");
			popup.add(editMenu);
			editMenu.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

				}
			});
			JMenuItem copyMenu = new JMenuItem("Copy");
			popup.add(copyMenu);
			copyMenu.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					restClient.copyPort(entity.getId());
					createButtons(restClient, panel);
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
					restClient.copyPorts(entity.getId(), Integer.valueOf(value));
					createButtons(restClient, panel);
				}
			});
			

			
			JMenuItem deleteMenu = new JMenuItem("Delete");
			popup.add(deleteMenu);
			deleteMenu.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (panel.getSelectedIds().size() == 0) {
						restClient.deletePort(entity.getId());
					}
					else {
						restClient.deletePorts(panel.getSelectedIds());
					}
					createButtons(restClient, panel);
				}
			});
			
			JMenu template = new JMenu("Apply Template");
			popup.add(template);
			for (PortTestTemplateEntity t : restClient.getPortTestTemplates()) {
				JMenuItem menuItem = new JMenuItem(t.getName());
				menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						restClient.applyPortTemplate(panel.getSelectedIds(), t.getId());
					}
				});
				template.add(menuItem);
			}
			
			movableButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON3) {
						popup.show(movableButton, e.getX(), e.getY());
						
					}
				}				
			});
		}
	}

	protected boolean showPortDetail(PortEntitySimple port, RestClient restClient) {
		MyJFrame dlg = new MyJFrame("Port Editor", new PortDetailPanel(port, restClient));
		dlg.modal();
		dlg.setVisible(true);
		return dlg.isOkClicked();
	}

	@Override
	protected void commit() {
		this.restClient.renameEquipmentName(equipmentId, nameEditor.getText());
	}

}
