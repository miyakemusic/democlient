package democlient2.topology2;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.miyake.demo.entities.EquipmentEntity;
import com.miyake.demo.entities.PortEntity;
import com.miyake.demo.entities.PortPresentationEntity;

import democlient2.RestClient;

public class PortListPanel extends JPanel {

	private Map<Long, PortPresentationEntity> portPresentationMap = new HashMap<>();
	private Map<Long, MovableButton> buttonMap= new HashMap<>();
	private Map<MovableButton, Point> offsetMap= new HashMap<>();
	private List<MovableButton> selectedButtons = new ArrayList<>();
	
	public PortListPanel(EquipmentEntity equipment, RestClient restClient) {
		int height = 25;
		int width = 150;
		
		int xoffset = 20;
		int yoffset = 20;
		
		DraggablePanel panel = new DraggablePanel(xoffset, yoffset, width, height, buttonMap.values()) {
			@Override
			protected void onSelect(int xmin, int ymin, int xmax, int ymax) {
				selectedButtons.clear();
				buttonMap.forEach((id, button) -> {
					if (
							(button.getX() > xmin) && 
							(button.getY() > ymin) && 
							(xmax > (button.getX() + button.getWidth())) && 
							(ymax > (button.getY() + button.getHeight()))
					) {
						button.setSelected(true);
						selectedButtons.add(button);
					}
					else {
						button.setSelected(false);
					}
				});
			}
		};
		
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(panel), BorderLayout.CENTER);
		panel.setLayout(null);
		
		PortPresentationEntity[] presentations = restClient.portPresentation(equipment.getId());
		for (PortPresentationEntity entity : presentations) {
			portPresentationMap.put(entity.getPort(), entity);
		}
		
		for (PortEntity entity: equipment.getPorts()) {
			MovableButton movableButton = new MovableButton(entity.getPort_name(), entity.getId()).xoffset(xoffset).yoffset(yoffset);
			panel.add(movableButton);
			
			PortPresentationEntity presentationEntity = portPresentationMap.get(entity.getId());//findPosition(presentation, port.getId());

			buttonMap.put(entity.getId(), movableButton);
			
			movableButton.setBounds(yoffset + presentationEntity.getX(), yoffset + presentationEntity.getY(), width, height);
			movableButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				}
			});
			
			movableButton.addMovableButtonListener(new MovableButtonListener() {
				@Override
				public void onClick() {
				}

				@Override
				public void onMoved(Point location) {
					PortPresentationEntity pe2 = portPresentationMap.get(entity.getId());
					pe2.setX((int)(location.getX() - xoffset));
					pe2.setY((int)(location.getY() - yoffset));
					try {
						restClient.post(pe2);
					} catch (IOException e) {
						e.printStackTrace();
					}
					selectedButtons.forEach(b-> {
						if (b.equals(movableButton)) {
							return;
						}
						b.adjust();
						b.setSelected(false);
						PortPresentationEntity pe3 = portPresentationMap.get(b.getId());
						pe3.setX(b.getX() - xoffset);
						pe3.setY(b.getY() - yoffset);
						try {
							restClient.post(pe3);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
					selectedButtons.clear();
					
					checkDuplicated();
				}

				@Override
				public void onMoving(Point location) {
					selectedButtons.forEach(b -> {
						if (movableButton == b) {
							return;
						}
						Point offset = offsetMap.get(b);
						b.setLocation(movableButton.getX() + (int)offset.getX(), movableButton.getY() + (int)offset.getY());
					});
				}

				@Override
				public void onMoveStart(Point location) {
					selectedButtons.forEach(b-> {
						Point offset = new Point();
						offset.setLocation(b.getX() - location.getX(), b.getY() - location.getY());
						offsetMap.put(b, offset);
					});
				}
			});
		}
		
		this.checkDuplicated();
		this.updateUI();
	}

	protected void checkDuplicated() {
		for (MovableButton mb : buttonMap.values()) {
			mb.setDuplicated(false);
		}
		for (MovableButton mb : buttonMap.values()) {
			for (MovableButton mb2 : buttonMap.values()) {
				if (mb.equals(mb2)) {
					continue;
				}
				if ((mb.getLocation().getX() == mb2.getX()) && (mb.getLocation().getY() == mb2.getY())) {
					mb.setDuplicated(true);
					mb2.setDuplicated(true);
				}
			}
		}
		repaint();
	}

	private PortPresentationEntity findPosition(PortPresentationEntity[] positions, Long id) {
		for (PortPresentationEntity ui : positions) {
			if (ui.getPort().equals(id)) {
				return ui;
			}
		}
		PortPresentationEntity ret = new PortPresentationEntity();
		ret.setPort(id);
		return ret;
	}
}

