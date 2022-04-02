package democlient2;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import democlient2.topology2.TopologyPanel3;

public class TopologyFrame extends JFrame {


	public static void main(String[] args) {
//		MyHttpClient http = new MyHttpClient("http://localhost:8080");	
//		new TopologyFrame(http).setVisible(true);
	}

	public TopologyFrame(String title, MyHttpClient http, Long id) {
		this.setTitle(title);
		this.setSize(new Dimension(1000, 800));
		this.getContentPane().setLayout(new BorderLayout());
	
		this.getContentPane().add(new TopologyPanel3(new RestClient(http), id), BorderLayout.CENTER);
//		this.getContentPane().add(new TopologyPanel(http, id), BorderLayout.CENTER);
	}
}
