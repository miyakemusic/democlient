package democlient2;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

abstract public class OkCancelPanel extends JPanel {

	public OkCancelPanel() {
		this.setLayout(new FlowLayout());
		JButton ok = new JButton("OK");
		this.add(ok);
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOk();	
			}
		});
		
		JButton cancel = new JButton("Cancel");
		this.add(cancel);
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
	}

	protected abstract void onCancel();

	protected abstract void onOk();
}
