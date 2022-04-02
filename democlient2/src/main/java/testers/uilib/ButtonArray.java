package testers.uilib;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ButtonArray extends JPanel {

	public ButtonArray() {
		this.setPreferredSize(new Dimension(100, 100));
		this.setLayout(new GridLayout(10, 1));
	}

	public ButtonArray button(MyPanel button) {
		this.add(button);
		button.setPreferredSize(new Dimension(100, 50));
		return this;
	}
}
