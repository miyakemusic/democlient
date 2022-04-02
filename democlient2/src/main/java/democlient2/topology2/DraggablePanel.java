package democlient2.topology2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Collection;

import javax.swing.JPanel;

public abstract class DraggablePanel extends JPanel {
	
	private int xoffset;
	private int yoffset;
	private int unitWidth;
	private int unitHeight;
	private int countx;
	private int county;
	private Point original;
	private Point current;
	
	private boolean dragging = false;
	private Collection<MovableButton> collection;
	public DraggablePanel(int xoffset, int yoffset, int unitWidth, int unitHeight, Collection<MovableButton> collection) {
		this.xoffset = xoffset;
		this.yoffset = yoffset;
		this.unitWidth = unitWidth;
		this.unitHeight = unitHeight;
		this.collection = collection;
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				dragging = true;
				original = e.getPoint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				dragging = false;
				repaint();
				current = e.getPoint();
				int xmin = Math.min(original.x, current.x);
				int xmax = Math.max(original.x, current.x);
				int ymin = Math.min(original.y, current.y);
				int ymax = Math.max(original.y, current.y);
				onSelect(xmin, ymin, xmax, ymax);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if (dragging) {
					current = e.getPoint();
				}
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				current = e.getPoint();
				repaint();
			}
			
		});
	}

	protected abstract void onSelect(int xmin, int ymin, int xmax, int ymax);

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int maxWidth = 0;
		int maxHeight = 0;
		for (MovableButton button : collection) {
			maxWidth = Math.max(button.getX(), maxWidth);
			maxHeight = Math.max(button.getY(), maxHeight);
		}
		countx = (maxWidth + unitWidth - xoffset) / unitWidth;
		county = (maxHeight + unitHeight - yoffset) / unitHeight;
		
		Graphics2D g2 = (Graphics2D)g;

		g2.setColor(Color.white);
		g2.fillRect(xoffset, yoffset, unitWidth * countx,  unitHeight * county);
		
		g2.setColor(Color.LIGHT_GRAY);
		g2.setStroke(new BasicStroke(1));
		for (int i = 1; i < county; i++) {
			g2.drawLine(
					xoffset, 
					yoffset + i * this.unitHeight, 
					xoffset + this.unitWidth * countx, 
					yoffset + i * this.unitHeight);
		}
		for (int i = 1; i < countx; i++) {
			g2.drawLine(
					xoffset + i * this.unitWidth, 
					yoffset, 
					xoffset + i * this.unitWidth, 
					yoffset + this.unitHeight * county);
		}
		
		float[] dash1 = { 2f, 0f, 2f };
		BasicStroke bs1 = new BasicStroke(1, 
			        BasicStroke.CAP_BUTT, 
			        BasicStroke.JOIN_ROUND, 
			        1.0f, 
			        dash1,
			        2f);
		
		g2.setColor(Color.black);
		g2.setStroke(bs1);
		
		if (dragging && original != null && current != null) {
			g2.drawLine(original.x, original.y, current.x, original.y);
			g2.drawLine(current.x, original.y, current.x, current.y);
			g2.drawLine(current.x, current.y, original.x, current.y);
			g2.drawLine(original.x, current.y, original.x, original.y);
		}
		
	}	
}
