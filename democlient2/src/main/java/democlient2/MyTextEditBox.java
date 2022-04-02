package democlient2;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MyTextEditBox extends JPanel {
	private JTextField textField;

	public MyTextEditBox(String title, String defaultText) {
		this.setLayout(new FlowLayout());
		this.add(new JLabel(title));
		textField = new JTextField();
		textField.setText(defaultText);
		textField.setPreferredSize(new Dimension(200, 24));
		this.add(textField);
	}

	public String getText() {
		return this.textField.getText();
	}
}