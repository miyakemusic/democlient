package democlient2.topology2;

import java.awt.Color;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.LineBorder;

public class MovableButton extends JButton {

	private boolean moving = false;
	private boolean moved = false;
	private Point original;
	private Point offset;
	private Set<MovableButtonListener> clickListeners = new HashSet<>();
//	private boolean grid = false;
	private int xoffset = 0;
	private int yoffset = 0;
	private Long id;
//	private Color borderColor = Color.lightGray;
	private int gridWidth = 1;
	private int gridHeight = 1;
	private boolean movable = true;
//	private JPopupMenu popup = new JPopupMenu();
//	private JMenu menu = new JMenu();
	public MovableButton(String name, Long id) {
		super(name);
		
		this.setToolTipText(name);
		this.id = id;
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					moved = false;
					moving = true;
					
					if (movable) {	
						original = MovableButton.this.getLocation();
						offset = e.getPoint();
						clickListeners.forEach(l -> {
							l.onMoveStart(MovableButton.this.getLocation());
						});
					}
				}
				else if (e.getButton() == MouseEvent.BUTTON3) {
//					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			
			@Override
			public void mouseReleased(MouseEvent e) {	
				if (e.getButton() == MouseEvent.BUTTON1) {
					moving = false;
					if (!moved) {
						clickListeners.forEach(l -> {
							l.onClick();
						});
					}
					else if (movable){
						adjust();
						clickListeners.forEach(l -> {
							l.onMoved(MovableButton.this.getLocation());
						});
					}
				}
//				System.out.println("released");
			}
		});
		
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				moved = true;
				
				if (moving && movable) {
					int x = (int)(original.getX() + e.getX() - offset.getX());
					int y = (int)(original.getY() + e.getY() - offset.getY());
//					System.out.println("x,y=" + x + "," + y);
					MovableButton.this.setLocation(new Point( x, y));
					
					original = MovableButton.this.getLocation();
					
					clickListeners.forEach(l -> {
						l.onMoving(MovableButton.this.getLocation());
					});
				}
			}
		});
	}
	public void addMovableButtonListener(MovableButtonListener actionListener) {
		clickListeners.add(actionListener);
	}
	
	public MovableButton xoffset(int xoffset) {
		this.xoffset = xoffset;
		return this;
	}
	
	public MovableButton yoffset(int yoffset) {
		this.yoffset = yoffset;
		return this;
	}
	
	public MovableButton gridWidth(int width) {
		this.gridWidth = width;
		return this;
	}
	
	public MovableButton gridHeight(int height) {
		this.gridHeight = height;
		return this;
	}
	
	@Override
	public void setSelected(boolean b) {
		if (b) {
			//this.setBackground(Color.green);
			this.setBorder(new LineBorder(Color.black, 2));
		}
		else {
			//this.setBackground(Color.LIGHT_GRAY);
			this.setBorder(new LineBorder(Color.lightGray, 1));
		}
	}

	public void adjust() {
		double x = this.getLocation().getX();
		double y = this.getLocation().getY();
		
		x = gridWidth * (int)( x / gridWidth) + xoffset % gridWidth;
		y = gridHeight * (int)( y / gridHeight) + yoffset % gridHeight;
		setLocation((int)x, (int)y);
	}
	public void setDuplicated(boolean b) {
		if (b) {
			this.setForeground(Color.red);
		}
		else {
			this.setForeground(Color.black);
		}
	}
	public Long getId() {
		return id;
	}
	public void setMovable(boolean movable) {
		this.movable = movable;
	}
//	public void addPopupMenu(String string, ActionListener actionListener) {
//		JMenuItem menu = new JMenuItem(string);
//		this.popup.add(menu);
//	}

	
}
