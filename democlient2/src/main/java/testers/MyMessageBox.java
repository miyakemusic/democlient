package testers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.SecondaryLoop;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MyMessageBox extends JFrame {

	protected boolean okCliked;

	private Object sync = new Object();
	private SecondaryLoop loop;
	
	public MyMessageBox(Container parent, String message) {
		
//		this.setLocationRelativeTo(parent);
		this.setAlwaysOnTop(true);
//		this.setSize(new Dimension(700, 400));
//		this.setLocationRelativeTo(parent);//(parent.getX() + 200, parent.getY() + 250);
//		this.setLocation(parent.getX() , parent.getY()+100);
		this.getContentPane().setLayout(new BorderLayout());
		this.setBackground(Color.LIGHT_GRAY);
		JEditorPane editorPane = new JEditorPane("text/html", message);
		editorPane.setEditable(false);
		editorPane.setAutoscrolls(true);
		this.getContentPane().add(editorPane, BorderLayout.CENTER);
		this.setUndecorated(true);
		getRootPane().setBorder(
		        BorderFactory.createMatteBorder(4, 4, 4, 4, Color.black)
		);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		JButton ok = new JButton("OK");
		panel.add(ok);
		
		JButton cancel = new JButton("Cancel");
		panel.add(cancel);
		
		ActionListener close = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				loop.exit();
			}
		};
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				okCliked = true;
			}
		});
		ok.addActionListener(close);
		cancel.addActionListener(close);
		
		this.add(panel, BorderLayout.SOUTH);
		
		
	}

	public boolean isOkCliked() {
		return okCliked;
	}

	public void modal() {
		this.setVisible(true);
		loop = Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();
		loop.enter();
	}

	public void cancel() {
		this.setVisible(false);
		this.okCliked = false;
	}
	
}
