package democlient2;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class MyJFrame extends JDialog {
	private boolean okClicked = false;
	
	public boolean isOkClicked() {
		return okClicked;
	}

	public MyJFrame(String title, MyJPanel panel) {
		JDialog frame = this;
		this.setTitle(title);
		frame.setSize(new Dimension(800, 600));
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		JPanel control = new JPanel();
		control.setLayout(new FlowLayout());
		this.getContentPane().add(control, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		control.add(ok);
		JButton cancel = new JButton("Cancel");
		control.add(cancel);
		
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				okClicked = true;
				setVisible(false);
				panel.commit();
			}
		});		
	}

	public Component modal() {
		this.setModal(true);
		return this;
	}

}
