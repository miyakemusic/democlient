package testers;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public abstract class PortPanel extends JPanel {
	
	private BufferedImage portImage;
	protected int offset_x;
	protected int offset_y;
	private List<Port> ports = new ArrayList<>();
	private Port currentPort = null;
	

	abstract protected void onSelect(String name);
	
	public PortPanel(List<Port> ports) {
		this.ports = ports;
		this.setPreferredSize(new Dimension(1000, 100));
		this.setLayout(null);
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("x=" + e.getX() + ", y=" + e.getY());
				for (Port port : ports) {
					if (( (port.getX()-port.getWidth()/2) < e.getX()) &&  (e.getX() < (port.getX() + port.getWidth() - port.getWidth()/2))) {
						currentPort = port;
						repaint();
						onSelect(currentPort.getName());
					}
				}
			}
			
		});
		try {
			portImage = ImageIO.read(new File( portImage() ));
			this.setPreferredSize(new Dimension(portImage.getWidth(), portImage.getHeight()));
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
	}

	abstract protected String portImage();

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g);
		g2d.drawImage(portImage, null, 0, 0);
		
		g2d.setColor(Color.RED);
	
		for (Port port : ports) {
			if (port == this.currentPort) {
				AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
				g2d.setComposite(ac);
				g2d.fillOval(port.getX() - port.getWidth()/2, port.getY() - port.getWidth()/2, port.getWidth(), port.getWidth());
			}
			else {
				g2d.drawOval(port.getX() - port.getWidth()/2, port.getY() - port.getWidth()/2, port.getWidth(), port.getWidth());
			}		
		}
		
		if (this.currentPort != null) {
			BasicStroke bs = new BasicStroke(5);
			g2d.setStroke(bs);
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
			g2d.setComposite(ac);
			g2d.setColor(Color.yellow);
			g2d.drawLine(this.getWidth()/2, 0, this.currentPort.getX(), this.currentPort.getY());
		}
	}
	
	
}
