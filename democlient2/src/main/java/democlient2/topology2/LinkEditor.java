package democlient2.topology2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.miyake.demo.entities.EquipmentEntity;
import com.miyake.demo.entities.EquipmentEntitySimple;
import com.miyake.demo.entities.PortEntity;
import com.miyake.demo.entities.PortEntitySimple;
import com.miyake.demo.entities.PortPresentationEntity;

import democlient2.MyJPanel;
import democlient2.RestClient;
import democlient2.topology2.DraggablePanel2.DragTarget;

class LinkEquipment {
	private List<JButton> buttons = new ArrayList<>();
	private String title;
	public void add(JButton button) {
		this.buttons.add(button);
	}
	public LinkEquipment() {}
	public int getX() {
		int min = Integer.MAX_VALUE;
		for (JButton button : buttons) {
			min = Math.min(min, button.getX());
		}
		return min;
	}
	public int getY() {
		int min = Integer.MAX_VALUE;
		for (JButton button : buttons) {
			min = Math.min(min, button.getY());
		}
		return min;
	}
	public int getWidth() {
		int max = 0;
		for (JButton button : buttons) {
			max = Math.max(max, button.getX() + button.getWidth());
		}
		return max - getX();
	}
	public int getHeight() {
		int max = 0;
		for (JButton button : buttons) {
			max = Math.max(max, button.getY() + button.getHeight());
		}
		return max - getY();
	}
	public void setTitle(String name) {
		this.title = name;
	}
	public String getTitle() {
		return this.title;
	}
	
}
public class LinkEditor extends MyJPanel {

	Map<Long, PortPresentationEntity> presentationPortMap = new HashMap<>();
	private Map<Long, LinkEquipment> linkEquipments = new HashMap<>();
	private Map<Point, Point> linkLines = new HashMap<>();
	
	private String title = "";
	private RestClient restClient;

	public LinkEditor(List<MovableButton> equipmentButtons, Map<Long, EquipmentEntitySimple> equipmentMap, RestClient restClient) {	
		
		int unitWidth = 120;
		int unitHeight = 24;
		int canvasLeft = 20;
		int canvasTop = 20;
		double ratio = 1;
		this.restClient = restClient;
		
		this.setLayout(new BorderLayout());
		DraggablePanel2 panel = new DraggablePanel2(canvasLeft, canvasTop, unitWidth, unitHeight) {
			@Override
			protected void onLinkSelected(Long id, Long id2) {
				restClient.linkPort(id, id2);

				Long project = equipmentMap.values().iterator().next().getProject();
				
				EquipmentEntitySimple[] equipments = restClient.equipmentsSimple(project);
				for (EquipmentEntitySimple e: equipments) {
					equipmentMap.put(e.getId(), e);
				}
				updateLink(equipmentMap, this);
			}

			@Override
			protected void onButtonClick(Long id) {
			}

			@Override
			protected void onEntityPositionUpdate(Long id, int x, int y, int width, int height) {
			}

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				Graphics2D g2 = (Graphics2D)g;
				linkEquipments.values().forEach(link -> {
					g2.drawString(link.getTitle(), link.getX(), link.getY());
					g2.drawRect(link.getX(), link.getY(), link.getWidth(), link.getHeight());
				});
			}
		}.dragTarget(DragTarget.Link);
		
		panel.setPreferredSize(new Dimension(2000, 2000));
		this.add(new JScrollPane(panel), BorderLayout.CENTER);
		
		int xoffset = Integer.MAX_VALUE;
		int yoffset = Integer.MAX_VALUE;
		
		Map<Integer, Map<Integer, EquipmentEntitySimple>> equipmentPosMatrix = new HashMap<>();
		
		for (MovableButton equipmentButton : equipmentButtons) {
			xoffset = Math.min(equipmentButton.getX(), xoffset);
			yoffset = Math.min(equipmentButton.getY(), yoffset);
			
			title += equipmentButton.getText() + " - ";
		}
		
		for (MovableButton equipmentButton : equipmentButtons) {
			PortPresentationEntity[] pp = restClient.portPresentation(equipmentButton.getId());
			
			int xPortMax = 0;
			int yPortMax = 0;
			for (PortPresentationEntity p : pp) {
				presentationPortMap.put(p.getPort(), p);
				xPortMax = Math.max(xPortMax, (int)(p.getX() * ratio));
				yPortMax = Math.max(yPortMax, p.getY());
			}
			
			LinkEquipment linkEquipment = new LinkEquipment();
//			linkEquipment.width = xPortMax + unitWidth;
//			linkEquipment.height = yPortMax + unitHeight;
			this.linkEquipments.put(equipmentButton.getId(), linkEquipment);
			
			int x = (equipmentButton.getX() - xoffset) / equipmentButton.getWidth();
			int y = (equipmentButton.getY() - xoffset) / equipmentButton.getHeight();
			EquipmentEntitySimple e = equipmentMap.get(equipmentButton.getId());
			if (!equipmentPosMatrix.containsKey(x)) {
				equipmentPosMatrix.put(x, new HashMap<Integer, EquipmentEntitySimple>());
			}
			equipmentPosMatrix.get(x).put(y, e);
		}

		List<Integer> xList = new ArrayList<Integer>(equipmentPosMatrix.keySet());
		Collections.sort(xList);

		int currentDrawButtonOffsetX = 0;
		
		int panelHeight = 0;
		
		Map<Integer, List<JButton>> buttonLayer = new LinkedHashMap<>();
		
		int xLayer = 0;
		for (Integer xTmp : xList) { // for each X
			Map<Integer, EquipmentEntitySimple> equipmentEntities = equipmentPosMatrix.get(xTmp);
			List<Integer> yList = new ArrayList<Integer>(equipmentEntities.keySet());
			Collections.sort(yList);

			List<JButton> layerButtonList = new ArrayList<>();
			buttonLayer.put(xLayer++, layerButtonList);
			
			int currentYsMaxX = 0;

			int equipemntYstart = 0;
			for (Integer yTmp : yList) { // for each Y
				EquipmentEntitySimple equipmentEntity = equipmentEntities.get(yTmp);
				//panel.createLabel(equipmentEntity.getName(), currentDrawButtonOffsetX, equipemntYstart);
				int currentYMax = 0;

				LinkEquipment link =  this.linkEquipments.get(equipmentEntity.getId());
				link.setTitle(equipmentEntity.getName());
				
				for (PortEntitySimple portEntity : restClient.portsByEquipment(equipmentEntity.getId())) {
					PortPresentationEntity portPresentation = presentationPortMap.get(portEntity.getId());

					int x = (int)(portPresentation.getX() * ratio) + currentDrawButtonOffsetX + canvasLeft;
					int y = portPresentation.getY() + equipemntYstart + canvasTop;
					
					currentYsMaxX = Math.max(currentYsMaxX, x);
					currentYMax = Math.max(currentYMax, y);
					
					JButton button = panel.createButton(portEntity.getPort_name(), portEntity.getId());
					button.setBounds(x, y, unitWidth, unitHeight);
					
					link.add(button);
					layerButtonList.add(button);
				}
				

//				link.x = currentDrawButtonOffsetX + canvasLeft;
//				link.y = equipemntYstart + canvasTop;
				equipemntYstart = currentYMax + unitHeight * 2;
				
				panelHeight = Math.max(panelHeight, equipemntYstart);
			}
			currentDrawButtonOffsetX = currentYsMaxX + unitWidth * 2;
		}

		// re-arrange buttons
		arrangeButtons(buttonLayer);
				
		panel.setPreferredSize(new Dimension(currentDrawButtonOffsetX, panelHeight));
		updateLink(equipmentMap, panel);
	}

	private void arrangeButtons(Map<Integer, List<JButton>> buttonLayer) {
		int maxY = 0;
		for (List<JButton> buttons : buttonLayer.values()) {
			for (JButton b : buttons) {
				maxY = Math.max(b.getY() + b.getHeight(), maxY);
			}
		}
		
		for (Integer layer : buttonLayer.keySet()) {
			List<JButton> buttons = buttonLayer.get(layer);
			int maxColY = 0;
			for (JButton button : buttons) {
				maxColY = Math.max(maxColY, button.getY() + button.getHeight());
			}
			int offset = (maxY - maxColY) / 2;
			for (JButton button : buttons) {
				Rectangle rect = button.getBounds();
				rect.y = rect.y + offset;
				button.setBounds(rect);
			}
		}
	}

	private void updateLink(Map<Long, EquipmentEntitySimple> equipmentMap, DraggablePanel2 panel) {
		panel.clearPair();
		for (Map.Entry<Long, EquipmentEntitySimple> entry : equipmentMap.entrySet()) {
			EquipmentEntitySimple equipment = entry.getValue();
			
			for (PortEntitySimple portEntity : restClient.portsByEquipment(equipment.getId())) {
				if (portEntity.getOpposite() != null) {
					panel.addLinkPair(portEntity.getId(), portEntity.getOpposite());
				}
			}
		}
	}

	public String getTitle() {
		return this.title;
	}

	@Override
	protected void commit() {
		// TODO Auto-generated method stub
		
	}

}
