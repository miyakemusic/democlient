package testers.uilib;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.miyake.demo.entities.PropertyEntity;
import com.miyake.demo.entities.PropertyOptionEntity;
import com.miyake.demo.entities.PropertyType;

import testers.UiInterface;

public class UiFactory {

	private UiInterface uiInterface;// = new PropertyCache();
	
	public UiFactory(UiInterface uiInterface) {
		this.uiInterface = uiInterface;
	}

	public MyPanel create(Long id) {
		PropertyEntity property = uiInterface.property(id);
		
		if (property.getType().compareTo(PropertyType.List) == 0) {
			return new MyComboBox(id, uiInterface);
		}
		else if (property.getType().compareTo(PropertyType.Action) == 0) {
			
		}
		else if (property.getType().compareTo(PropertyType.Boolean) == 0) {
			
		}
		else if (property.getType().compareTo(PropertyType.Numeric) == 0) {
			return createNumricTextBox(id, uiInterface);
		}

		return null;
	}


	private MyPanel createNumricTextBox(Long id, UiInterface uiInterface) {
		PropertyEntity property = uiInterface.property(id);
		
		MyPanel panel = new MyPanel();
		panel.setSize(new Dimension(100, 24));
		panel.add(new JLabel(property.getName()));
		JTextField textField = new JTextField();
		textField.setPreferredSize(new Dimension(50, 20));
		panel.add(textField);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					uiInterface.setCurrentNumeric(property.getId(), Double.valueOf(textField.getText()));
				}
			}
		});
		textField.setText(uiInterface.formattedNumeric(property.getId()).toString());
		uiInterface.addValueListener(id, new ValueListener() {
			@Override
			public void onChange(Long id, Object value) {
				textField.setText(value.toString());
			}

			@Override
			public void onEnableChange(Long id, boolean enabled) {
				textField.setEnabled(enabled);
			}
			
		});
		return panel;
	}



	public MyPanel createToggleButton(Long id) {
		return new MyToggleButton(id, this.uiInterface);
	}

	public MyPanel createLabel(Long id) {
		return new MyLabel(id, this.uiInterface);
	}

}



class MyLabel extends MyPanel {

	public MyLabel(Long id, UiInterface uiInterface) {
		PropertyEntity property = uiInterface.property(id);
		this.setLayout(new FlowLayout());
		this.add(new JLabel(property.getName()));
		this.add(new JLabel(" : "));
		JLabel label = new JLabel();
		this.add(label);
		
		uiInterface.addValueListener(id, new ValueListener() {
			@Override
			public void onChange(Long id, Object value) {
				updatePresentation(property, label, uiInterface);
			}

			@Override
			public void onEnableChange(Long id, boolean enabled) {
				label.setEnabled(enabled);
			}
		});
		
		updatePresentation(property, label, uiInterface);	
	}

	protected void updatePresentation(PropertyEntity property, JLabel label, UiInterface uiInterface) {
		String text = "";
		if (property.getType().equals(PropertyType.Numeric)) {
			text = uiInterface.formattedNumeric(property.getId()) + " " + property.getUnitEntity().getUnit();
		}
		else if (property.getType().equals(PropertyType.List)) {
			Long optionid = uiInterface.currentOptionId(property.getId());
			text = property.findOption(optionid).getName();
		}

		label.setText(text);
	}	
}

class MyComboBox extends MyPanel {
	private boolean consuming = false;
	public MyComboBox(Long id, UiInterface uiInterface) {
		PropertyEntity property = uiInterface.property(id);
		
		JPanel panel = this;
		panel.setSize(new Dimension(100, 24));
		panel.add(new JLabel(property.getName()));
		JComboBox<PropertyOptionEntity> combo = new JComboBox<>();
		for (PropertyOptionEntity option : property.getOption_list()) {
			combo.addItem(option);
			if (uiInterface.currentOptionId(property.getId()) == option.getId()) {
				combo.setSelectedItem(option);
			}
		}
		
		combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!consuming) {
					PropertyOptionEntity option = (PropertyOptionEntity)combo.getSelectedItem();
					uiInterface.setCurrentOptionId(property.getId(), option.getId()); 
				}
			}
		});
		panel.add(combo);
		
		
		uiInterface.addValueListener(id, new ValueListener() {
			@Override
			public void onChange(Long id, Object value) {
				consuming = true;
				combo.setSelectedItem(property.findOption((Long)value));
				consuming = false;
			}

			@Override
			public void onEnableChange(Long id, boolean enabled) {
				combo.setEnabled(enabled);
			}
		});
	}
	
}
class MyToggleButton extends MyPanel {

	public MyToggleButton(Long id, UiInterface uiInterface) {
		PropertyEntity property = uiInterface.property(id);
		//String html = "<html>" + property.getName() + "<br><font color=\"red\">" + property.getOptions().get(0).getName() + "</font></html>";
		Long optionid = uiInterface.currentOptionId(id);
//		PropertyOptionEntity option = property.findOption(optionid);
		JButton button = new JButton();
		this.setLayout(new BorderLayout());
		this.add(button, BorderLayout.CENTER);
		
		updateLabel(property, uiInterface, button);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				uiInterface.setCurrentOptionId(id, property.nextOption(uiInterface.currentOptionId(id)));
				updateLabel(property, uiInterface, button);
			}
		});
		
		uiInterface.addValueListener(id, new ValueListener() {
			@Override
			public void onChange(Long id, Object value) {
				updateLabel(property, uiInterface, button);
			}

			@Override
			public void onEnableChange(Long id, boolean enabled) {
				MyToggleButton.this.setEnabled(enabled);
			}
		});
	}

	private void updateLabel(PropertyEntity property, UiInterface uiInterface, JButton button) {
		PropertyOptionEntity nextOption =  property.findOption(property.nextOption(uiInterface.currentOptionId(property.getId())));
//		PropertyOptionEntity nextOption =  property.findOption(values.optionId(id));
		String html = "<html>" + nextOption.getName() + "</html>";
		button.setText(html);
	}
	
}
