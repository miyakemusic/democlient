package testers.fip;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import democlient2.MyHttpClient;
import democlient2.RestClient;
import testers.uilib.ButtonArray;
import testers.uilib.UiFactory;

public class FipUi extends JPanel {
	public static void main(String[] arg) {
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
		frame.getContentPane().add(new FipUi(new FipModel(restClient, new FipCoreSim("Microscope-SM.jpg", "Microscope-SM_fail.jpg"))), BorderLayout.CENTER);
		
		frame.setVisible(true);
		
	}
	public FipUi(FipModel fipModel) {
		UiFactory uiFactory = new UiFactory(fipModel);
		
		this.setLayout(new BorderLayout());
		this.add(new ButtonArray().button(uiFactory.createToggleButton(InstDef_FIP.TEST)), BorderLayout.EAST);
		JLabel picture = new JLabel();
		this.add(picture, BorderLayout.CENTER);
		
		fipModel.addFipModelListener(new FipModelListener() {
			@Override
			public void onUpdate(byte[] array) {
				InputStream is = new ByteArrayInputStream(array);
				try {
					BufferedImage bi = ImageIO.read(is);
					picture.setIcon(new ImageIcon(bi));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
