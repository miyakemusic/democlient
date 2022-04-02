package testers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

import com.miyake.demo.entities.MyTesterEntity;
import com.miyake.demo.entities.TesterEntity;
import com.miyake.demo.entities.UserEntity;
import democlient2.MyHttpClient;
import democlient2.RestClient;

public class TesterStartup extends JFrame {

	private RestClient restClient;
	private JLabel status;
	
	
	public static void main(String[] arg) {
		new TesterStartup().setVisible(true);;
	}
	
	public TesterStartup() {
		this.setSize(new Dimension(300, 200));
//		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		List<String> accounts = Arrays.asList(
				"01234567",
				"SN1234567",
				"MT9085_0001@anritsu.com",
				"MT9085C-053.6D01234567@miyake.com",
				"FIP500.000123411@anritsu.com",
				"MT9085.6D01234567@miyake.com",
				"MT9085.6D01234566@miyake.com",
				"G0382A.6D01234560@miyake.com"
				);
		
		
		this.getContentPane().setLayout(new FlowLayout());	
		
		MyHttpClient http = new MyHttpClient("http://localhost:8080");		
		RestClient restClient = new RestClient(http);
		
//		JComboBox<TesterEntity> testers = new JComboBox<>();
//		Arrays.asList(restClient.testers()).forEach(t -> testers.addItem(t));
	
		JComboBox<String> testers = new JComboBox<>();
		testers.addItem("OTDR");
		testers.addItem("FIP");
		JComboBox<String> accountCombo = new JComboBox<>();
		accounts.forEach(t -> {accountCombo.addItem(t);});

//		this.getContentPane().add(testers, BorderLayout.CENTER);
		this.getContentPane().add(accountCombo, BorderLayout.NORTH);
		
		JPasswordField password = new JPasswordField("marijuana");
		this.getContentPane().add(password, BorderLayout.SOUTH);
		
		JButton signin = new JButton("Sign In");
		this.getContentPane().add(signin);
		
		signin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					MyTesterEntity entity = restClient.signin(accountCombo.getSelectedItem().toString(), password.getText());
					setVisible(false);
					TesterDevFrame testerMain = null;//new MT1000A(restClient, entity);
//					if (accountCombo.getSelectedItem().toString().startsWith("FIP")) {
//						testerMain = new FIP500Frame(restClient, entity);
//					}
//					else if (accountCombo.getSelectedItem().toString().startsWith("MT9085")) {
//						testerMain = new MT1000AFrame(restClient, entity);
//					}
					if (entity.getTester() == 1) {
						testerMain = new MT1000AFrame(restClient, entity);
					}		
					else if (entity.getTester() == 7) {
						testerMain = new FIP500Frame(restClient, entity);
					}
					 			
					testerMain.setVisible(true);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
}
