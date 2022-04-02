package testers.uilib;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

public class MyPanel extends JPanel {

	public MyPanel fontSize(int size) {
		for (int i = 0; i < this.getComponentCount(); i++) {
			Component c = this.getComponent(i);
			c.setFont(new Font(this.getFont().getFontName(), this.getFont().getStyle(), size));
		}
		return this;
	}

	public MyPanel boderWidth(int width) {
		this.setBorder(new EtchedBorder(width));
		return this;
	}
	
}