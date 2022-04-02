package democlient2.topology2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class DraggablePanel2 extends JPanel {
	public enum DragTarget {
		Move,
		Link
	}
	private int xoffset;
	private int yoffset;
	private int unitWidth;
	private int unitHeight;
	private int countx;
	private int county;
	private Point originalLinePoint;
	private Point currentLinePoint;
	
	private boolean dragging = false;
	private Map<Long, MovableButton> buttonMap= new HashMap<>();
	private Map<MovableButton, Point> offsetMap= new HashMap<>();
	private List<MovableButton> selectedButtons = new ArrayList<>();
	private int gridx = 1;
	private int gridy = 1;
	private DragTarget dragTarget = DragTarget.Move;
	
	private Point startPoint;
	private Point currentPoint;
	private MovableButton currentLinkTarget;
	private MovableButton currentLinkSource;
	private LinkPairContainer staticLinks = new LinkPairContainer();
	
	public DraggablePanel2(int xoffset, int yoffset, int unitWidth, int unitHeight) {
		this.xoffset = xoffset;
		this.yoffset = yoffset;
		this.unitWidth = unitWidth;
		this.unitHeight = unitHeight;
		
		this.setLayout(null);
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					dragging = true;
					originalLinePoint = e.getPoint();
					selectedButtons.clear();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					dragging = false;
					
					currentLinePoint = e.getPoint();
					int xmin = Math.min(originalLinePoint.x, currentLinePoint.x);
					int xmax = Math.max(originalLinePoint.x, currentLinePoint.x);
					int ymin = Math.min(originalLinePoint.y, currentLinePoint.y);
					int ymax = Math.max(originalLinePoint.y, currentLinePoint.y);
					onSelect(xmin, ymin, xmax, ymax);
					
					currentLinePoint = null;
					originalLinePoint = null;
					
					onSelecteComplete(selectedButtons);
					repaint();
				}
			}

			private void onSelect(int xmin, int ymin, int xmax, int ymax) {
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

			@Override
			public void mouseMoved(MouseEvent e) {
				if (dragging) {
					currentLinePoint = e.getPoint();
				}
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				currentLinePoint = e.getPoint();
				repaint();
			}
			
		});
	}
	
	protected void onSelecteComplete(List<MovableButton> selectedButtons2) {};

	public MovableButton createButton(String name, Long id) {
		String html = "<HTML>" + name + "</HTML>";
		MovableButton movableButton = new MovableButton(html, id).xoffset(this.xoffset).yoffset(this.yoffset);
		this.add(movableButton);
		movableButton.setMovable(dragTarget.equals(DragTarget.Move));
		this.buttonMap.put(id, movableButton);
		
		movableButton.addMovableButtonListener(new MovableButtonListener() {
			@Override
			public void onClick() {
				onButtonClick(id);
			}

			@Override
			public void onMoved(Point location) {
				onEntityPositionUpdate(id, (int)(location.getX() - xoffset), (int)(location.getY() - yoffset), movableButton.getWidth(), movableButton.getHeight());
				selectedButtons.forEach(b-> {
					b.adjust();
					b.setSelected(false);
					if (b.equals(movableButton)) {
						return;
					}
					onEntityPositionUpdate(b.getId(), (int)(b.getX() - xoffset), (int)(b.getY() - yoffset), movableButton.getWidth(), movableButton.getHeight());
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
		
		movableButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				startPoint = new Point();
				startPoint.setLocation(movableButton.getX() +  e.getX(), movableButton.getY() + e.getY());
				
				currentLinkSource = movableButton;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				startPoint = null;
				currentPoint = null;
				
				if (currentLinkSource != currentLinkTarget && dragTarget.equals(DragTarget.Link)) {
					onLinkSelected(currentLinkSource.getId(), currentLinkTarget.getId());
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				currentLinkTarget = movableButton;
			}
		});
		movableButton.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				currentPoint = new Point();
				currentPoint.setLocation(movableButton.getX() +  e.getX(), movableButton.getY() + e.getY());
				repaint();
			}
			
			
		});
		
		movableButton.gridWidth(gridx);
		movableButton.gridHeight(gridy);
		
		return movableButton;
	}

	public List<MovableButton> getSelectedButtons() {
		return selectedButtons;
	}

	protected abstract void onLinkSelected(Long id, Long id2);

	protected abstract void onButtonClick(Long id);

	protected abstract void onEntityPositionUpdate(Long id, int x, int y, int width, int height);

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int maxWidth = 0;
		int maxHeight = 0;
		for (MovableButton button : this.buttonMap.values()) {
			maxWidth = Math.max(button.getX(), maxWidth);
			maxHeight = Math.max(button.getY(), maxHeight);
		}
		countx = (maxWidth + unitWidth - xoffset) / this.gridx;
		county = (maxHeight + unitHeight - yoffset) / this.gridy;
		
		Graphics2D g2 = (Graphics2D)g;

		// draw rect
		g2.setColor(Color.white);
		g2.fillRect(xoffset, yoffset, gridx * countx, gridy * county);
		
		// draw grid
		if (gridx != 1 && gridy != 1) {
			g2.setColor(Color.LIGHT_GRAY);
			g2.setStroke(new BasicStroke(1));
			for (int i = 1; i < county; i++) {
				g2.drawLine(
						xoffset, 
						yoffset + i * this.gridy, 
						xoffset + this.gridx * countx, 
						yoffset + i * this.gridy);
			}
			for (int i = 1; i < countx; i++) {
				g2.drawLine(
						xoffset + i * this.gridx, 
						yoffset, 
						xoffset + i * this.gridx, 
						(yoffset + this.gridy * county));
			}
		}
		
		float[] dash1 = { 2f, 0f, 2f };
		BasicStroke bs1 = new BasicStroke(1, 
			        BasicStroke.CAP_BUTT, 
			        BasicStroke.JOIN_ROUND, 
			        1.0f, 
			        dash1,
			        2f);
		
		// Dragging line
//		if (this.dragTarget.equals(DragTarget.Move)) {
			g2.setColor(Color.black);
			g2.setStroke(bs1);
			if (dragging && originalLinePoint != null && currentLinePoint != null) {
				g2.drawLine(originalLinePoint.x, originalLinePoint.y, currentLinePoint.x, originalLinePoint.y);
				g2.drawLine(currentLinePoint.x, originalLinePoint.y, currentLinePoint.x, currentLinePoint.y);
				g2.drawLine(currentLinePoint.x, currentLinePoint.y, originalLinePoint.x, currentLinePoint.y);
				g2.drawLine(originalLinePoint.x, currentLinePoint.y, originalLinePoint.x, originalLinePoint.y);
			}
//		}
		
		// Dragging rect
		if (dragTarget.equals(DragTarget.Link) && (startPoint != null) && (currentPoint != null)) {
			int x1 = (int)startPoint.getX();
			int y1 = (int)startPoint.getY();
			int x2 = (int)currentPoint.getX();
			int y2 = (int)currentPoint.getY();
			System.out.println(x1 + "," + y1 + "," + x2 + "," + y2);
			g2.drawLine(x1, y1, x2, y2);
		}
		
		// Static Link
		g2.setColor(Color.blue);
		g2.setStroke(new BasicStroke(2));
		for (LinkPair linkPair : this.staticLinks.links) {
			Long id1 = linkPair.from;
			Long id2 = linkPair.to;
			JButton b1 = this.buttonMap.get(id1);
			JButton b2 = this.buttonMap.get(id2);
			
			if (b1 != null && b2 != null) {
				int x1 = 0;
				int x2 = 0;
				int y1 = 0;
				int y2 = 0;
				
				x1 = b1.getX() + b1.getWidth() / 2;
				x2 = b2.getX() + b2.getWidth() / 2;
				y1 = b1.getY() + b1.getHeight() / 2;
				y2 = b2.getY() + b2.getHeight() / 2;

				g2.drawLine(x1, y1, x2, y2);
			}
			else {
				System.out.println();
			}
		}
	}	
	
	@Override
	public void updateUI() {
		super.updateUI();
		if (buttonMap == null) {
			return;
		}
		int width = 0;
		int height = 0;
		for (MovableButton mb : buttonMap.values()) {		
			width = Math.max(mb.getX() + mb.getWidth(), width);
			height = Math.max(mb.getY() + mb.getHeight(), height);
		}
		this.setPreferredSize(new Dimension(width, height));
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
	
	public DraggablePanel2 grid(int x, int y) {
		this.gridx = x;
		this.gridy = y;
		return this;
	}

	
	public DragTarget getDragTarget() {
		return dragTarget;
	}

	public DraggablePanel2 dragTarget(DragTarget dragTarget) {
		this.dragTarget = dragTarget;
		this.buttonMap.values().forEach(b -> b.setMovable(dragTarget.equals(DragTarget.Move)));
		return this;
	}

	public JLabel createLabel(String name, int x, int y) {
		JLabel label = new JLabel(name);
		label.setBounds(x, y, 200, 24);
		this.add(label);
		return label;
	}

	public void addLinkPair(Long id, Long id2) {
		this.staticLinks.add(id, id2);
		this.repaint();
	}

	public void clearPair() {
		this.staticLinks.clear();
	}

	public void clear() {
		this.buttonMap.clear();
		this.removeAll();
		this.clearPair();
	}

	public void alignVertical() {
		Collections.sort(this.selectedButtons, new Comparator<MovableButton>() {
			@Override
			public int compare(MovableButton o1, MovableButton o2) {
				return o1.getY() - o2.getY();
			}
		});
		
		int y = selectedButtons.get(0).getBounds().y;
		for (MovableButton button : this.selectedButtons) {
			Rectangle rect = button.getBounds();
			rect.y = y;
			button.setBounds(rect);
			onEntityPositionUpdate(button.getId(), (int)(button.getX() - xoffset), (int)(button.getY() - yoffset), button.getWidth(), button.getHeight());
			
			y = rect.y + rect.height;
		}

	}

	public List<Long> getSelectedIds() {
		List<Long> ret = new ArrayList<>();
		for (MovableButton b : this.selectedButtons) {
			ret.add(b.getId());
		}
		return ret;
	}
}

class LinkPairContainer {
	List<LinkPair> links = new ArrayList<>();
	public void add(Long id1, Long id2) {
		LinkPair linkPair = new LinkPair(id1, id2);
		if (!links.contains(linkPair)) {
			links.add(linkPair);
		}
	}
	public void clear() {
		this.links.clear();
	}
}
class LinkPair {
	public LinkPair(Long id1, Long id2) {
		from = Math.min(id1, id2);
		to = Math.max(id1, id2);
	}
	Long from;
	Long to;
	@Override
	public boolean equals(Object obj) {
		LinkPair pair = (LinkPair)obj;
		if (from == pair.from && to == pair.to) {
			return true;
		}
		return false;
		
	}
}