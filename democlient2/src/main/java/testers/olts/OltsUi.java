package testers.olts;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import democlient2.MyHttpClient;
import democlient2.RestClient;
import testers.uilib.ButtonArray;
import testers.uilib.UiFactory;

public class OltsUi extends JPanel {

	public static void main(String[] args) {
		MyHttpClient http = new MyHttpClient("http://localhost:8080");		
		RestClient restClient = new RestClient(http);
		try {
			restClient.signin("miyakemusic@yahoo.co.jp", "marijuana");
		} catch (IOException e) {
			e.printStackTrace();
		}
		JFrame frame = new JFrame();
		frame.setSize(new Dimension(800, 600));
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new OltsUi(new OltsModel(restClient, new OltsCoreSim())), BorderLayout.CENTER);
		
		frame.setVisible(true);
	}

	
	public OltsUi(OltsModel oltsModel) {
		this.setLayout(new BorderLayout());
		UiFactory uiFactory = new UiFactory(oltsModel);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(10,1));
		this.add(panel, BorderLayout.CENTER);
		panel.add(uiFactory.createLabel(InstDef_OLTS.TRANSMIT_POWER).fontSize(30).boderWidth(1));
		panel.add(uiFactory.createLabel(InstDef_OLTS.RECEIVE_POWER).fontSize(30).boderWidth(1));
		panel.add(uiFactory.createLabel(InstDef_OLTS.LOSS).fontSize(30).boderWidth(1));
		
		this.add(new ButtonArray().button(uiFactory.createToggleButton(InstDef_OLTS.TEST)), BorderLayout.EAST);
	}

}
