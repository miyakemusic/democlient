package testers;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.miyake.demo.entities.MyTesterEntity;
import com.miyake.demo.entities.UserEntity;
import com.miyake.demo.jsonobject.TestPlan2Element;
import com.miyake.demo.jsonobject.TestResult;
import democlient2.RestClient;


public abstract class TesterDevFrame extends JFrame {	
	private BufferedImage bkimage;

	public TesterDevFrame(RestClient restClient, MyTesterEntity entity) {
		this.getContentPane().setLayout(new BorderLayout());

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle(entity.getName()); 

		try {
			bkimage = ImageIO.read(new File( backgroundImage()));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		JPanel devBackgroundPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				super.paintComponent(g);
				g2d.drawImage(bkimage, null, 0, 0);
			}
		};
//		
		devBackgroundPanel.setPreferredSize(new Dimension(bkimage.getWidth(), bkimage.getHeight()));
		this.getContentPane().add(devBackgroundPanel, BorderLayout.CENTER);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				getContentPane().add(createPortPanel(), BorderLayout.NORTH);
				Component mainApp = createAppPanel(restClient);
				mainApp.setBounds(offsetX(), offsetY(), mainWidth(), mainHeight());
				devBackgroundPanel.add(mainApp);
				pack();
			}
		});		

		devBackgroundPanel.setLayout(null);				
		pack();
		
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				restClient.signout();
			}
			
		});
	}

	protected abstract Component createPortPanel();

	protected abstract Component createAppPanel(RestClient restClient2);

	abstract protected String backgroundImage();

	abstract protected void configurePort(List<Port> ports);
	
//	public TesterDevFrame application(String name, ITester tester) {
//		tester.setListener(testerListener);
//		testItemMap.put(tab.getTabCount(), tester);
//		this.tab.add(name, tester.panel(this.restClient));
//		
//		return this;
//	}

	abstract protected String portImage();


	abstract protected int mainHeight();


	abstract protected int mainWidth();


	abstract protected int offsetY();


	abstract protected int offsetX();


//	private MyMessageBox createMessageBox(String html) {
//		if (this.messageBox != null) {
//			this.messageBox.cancel();
//		}
//		MyMessageBox messageBox = new MyMessageBox(tab, html);
//		int width = (int)(tab.getWidth()*0.9);
//		int height = (int)(tab.getHeight() * 0.8);
//		messageBox.setSize(new Dimension( width, height));
//		messageBox.setLocation(tab.getX() + this.getX() + (int)(this.getWidth()*0.05), tab.getY() + this.getY() + portPanel.getHeight() + 50);
//		return messageBox;
//	}
}

