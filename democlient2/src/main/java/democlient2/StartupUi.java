package democlient2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.miyake.demo.entities.MyTesterEntity;
import com.miyake.demo.entities.ProjectEntitySimple;
import com.miyake.demo.entities.UserEntity;
import com.miyake.demo.entities.UserGroupEntity;

public class StartupUi extends JFrame {

	public static void main(String[] args) {
		System.out.println(ProjectEntitySimple[].class.getName());
		System.out.println(ProjectEntitySimple.class.getName());
		new StartupUi().setVisible(true);
	}

	private JTextField userName;
	private JPasswordField password;
	protected MyTesterEntity userEntity;
	protected JTextField group;

	public StartupUi() {
		RestClient restClient = new RestClient(new MyHttpClient("http://localhost:8080"));
		
//		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		this.setSize(new Dimension(400, 400));
		
		JPanel mainPanel = new JPanel();
		this.getContentPane().setLayout(new BorderLayout());
		
		mainPanel.setLayout(new FlowLayout());
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		JLabel status = new JLabel("Not Signed In");
		mainPanel.add(status);
		
		mainPanel.add(new UiElement("Username") {
			@Override
			protected JComponent arrange(JPanel panel) {
				userName = new JTextField("");
				userName.setText("miyakemusic@yahoo.co.jp");
				return userName;
			}
		});
		mainPanel.add(new UiElement("Password") {
			@Override
			protected JComponent arrange(JPanel panel) {
				password = new JPasswordField("");
				password.setText("marijuana");
				return password;
			}
		});
		mainPanel.add(new UiElement("Group") {
			@Override
			protected JComponent arrange(JPanel panel) {
				group = new JTextField("");
				group.setEditable(false);
				return group;
			}
		});

		JButton buttonSignIn = new JButton("Sign In");
		mainPanel.add(buttonSignIn);
		buttonSignIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					userEntity = restClient.signin(userName.getText(), password.getText());
					if (userEntity.getUsergroupEntity() != null) {
						group.setText(userEntity.getUsergroupEntity().getName());
					}
					else {
						group.setText(userEntity.getUsergroup().toString());
					}
					
					ProjectFrame<ProjectEntitySimple> projectFrame = new ProjectFrame<ProjectEntitySimple>(
							restClient.http(), ProjectEntitySimple.class) {
						@Override
						protected void onEdit(ProjectEntitySimple entity) {
							new TopologyFrame(entity.getName(), getHttp(), entity.getId()).setVisible(true);
						}
					};
					projectFrame.setTitle(userEntity.getUsergroupEntity().getName());
					buttonSignIn.setText("Sign out");
					userName.setEnabled(false);
					password.setEnabled(false);
					projectFrame.setVisible(true);
					setVisible(false);
					status.setText("Signed in as  " + userEntity.getName());
				} catch (IOException e1) {
					e1.printStackTrace();
					status.setText("Failed to sign in.");
				}

			}
		});

		JButton buttonGroup = new JButton("Create Group");
		mainPanel.add(buttonGroup);
		buttonGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String value = JOptionPane.showInputDialog("Group Name", "GROUP_NAME");
				if (value != null && !value.isBlank()) {
					UserGroupEntity entity = new UserGroupEntity();
					entity.setName(value);
					try {
						restClient.post(entity);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
	}
}
abstract class UiElement extends JPanel {
	abstract protected JComponent arrange(JPanel panel);
	public UiElement(String string) {
		JPanel panel = this;
		panel.setLayout(new FlowLayout());
		JLabel label = new JLabel(string);
		label.setPreferredSize(new Dimension(100, 20));
		panel.add(label);
		JComponent component = arrange(panel);
		component.setPreferredSize(new Dimension(200, 20));
		panel.add(component);
	}
}