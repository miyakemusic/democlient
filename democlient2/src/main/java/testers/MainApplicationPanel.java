package testers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
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

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.miyake.demo.jsonobject.TestPlan2Element;
import com.miyake.demo.jsonobject.TestResult;

import democlient2.MyHttpClient;
import democlient2.RestClient;
import testers.fip.FipCoreSim;
import testers.fip.FipPane;
import testers.fip.SingleFipTester;
import testers.olts.OltsCoreSim;
import testers.olts.OltsPane;
import testers.otdr.OtdrCalculator;
import testers.otdr.OtdrCalculatorSim;
import testers.otdr.OtdrCoreSim;
import testers.otdr.OtdrPane;

public abstract class MainApplicationPanel extends JPanel {
	private MyMessageBox messageBox = null;
	private String lastRequestedPortName = "";
	private List<Port> ports = new ArrayList<>();
	private Map<Integer, ITester> testItemMap = new HashMap<>();
	
	abstract protected String portImage();
		
	public static void main(String[] arg) throws IOException {
		RestClient restClient = new RestClient(new MyHttpClient("http://localhost:8080"));
		restClient.signin("MT9085.6D01234567@miyake.com", "marijuana");
		
		OtdrCoreSim otdrCore = new OtdrCoreSim() {
			@Override
			public OtdrCalculator createCalculator() {
				return new OtdrCalculatorSim();
			}
		};
		OltsCoreSim oltsCore = new OltsCoreSim();
		FipCoreSim fipCore = new FipCoreSim("Microscope-SM.jpg", "Microscope-SM_fail.jpg");
		MainApplicationPanel panel = new MainApplicationPanel(restClient) {
			@Override
			protected String portImage() {
				return this.getClass().getResource("portimage2.bmp").getFile();
			}

			@Override
			protected void configurePort(List<Port> ports) {
				ports.add(new Port(MT1000APanel.FIP, 978, 27));
				ports.add(new Port(MT1000APanel.OPM, 308, 29));
				ports.add(new Port(MT1000APanel.SM1650, 535, 39));
				ports.add(new Port(MT1000APanel.SM1310_1550, 670, 39));
			}
		}.
				application("OTDR", new OtdrPane(otdrCore)).
				application("OLTS", new OltsPane(oltsCore)).
				application("FIP", new FipPane((fipCore)));
		
		JFrame frame = new JFrame();
		frame.setSize(new Dimension(1000, 800));
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		PortPanel portPanel = new PortPanel(panel.ports) {
			@Override
			protected void onSelect(String name) {
				otdrCore.setSelectedPort(name);
				oltsCore.setSelectedPort(name);
			}

			@Override
			protected String portImage() {
				return panel.portImage();
			}
		};
		frame.getContentPane().add(portPanel, BorderLayout.NORTH);
		frame.setVisible(true);
	}
	
	public List<Port> getPorts() {
		return ports;
	}

	private TesterListener testerListener = new TesterListener() {
		@Override
		public boolean requestPort(String portName, ITester tester) {
			if (portName.equals(lastRequestedPortName)) {
				return true;
			}
			
			Port port = null;
			for (Port p : ports) {
				if (p.getName().equals(portName)) {
					port = p;
					break;
				}
			}
			try {
	        	File imageFile = new File(portImage());
				BufferedImage bimage = ImageIO.read(imageFile);
				Mat mat = MyImageUtil.bufferedImageToMat(bimage);
				int x = (int)((double)port.getX() * 1.0);
				int y = (int)((double)port.getY() * 1.0);
				Imgproc.circle (
					mat,                 //Matrix obj of the image
					new Point( x,  y),    //Center of the circle
					20,                    //Radius
					new Scalar(0, 0, 255),  //Scalar object for color
					5                      //Thickness of the circle
				);			

				Files.deleteIfExists(Paths.get("mod_sidepanel.png"));
		        Imgcodecs.imwrite("mod_sidepanel.png", mat); 
		        
				lastRequestedPortName = portName;
				String html = "<html><body><font size=\"10\">Connect fiber to the connector</font><br>" + 
						"<img width=\"680\" height=\"60\" src=\"" + "file:" + "mod_sidepanel.png" + "\" alt=\"Red dot\" /></body></html>";
					messageBox = createMessageBox(html);
					messageBox.setVisible(true);
					messageBox.modal();
					return messageBox.okCliked;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			return true;
		}
		
	};
	private JTabbedPane tab;
	private RestClient restClient;
	public MainApplicationPanel(RestClient restClient) {
		this.restClient = restClient;
		this.setLayout(new BorderLayout());
		tab = new JTabbedPane();
		this.add(tab, BorderLayout.CENTER);
		
		Automator automator = new Automator() {
			@Override
			protected boolean onMessage(String message) {
				messageBox = createMessageBox(message);
				 messageBox.setVisible(true);
				 messageBox.modal();
				 return messageBox.isOkCliked();
			}

			@Override
			protected void onChangeApplication(Long testitem) {
				for (Map.Entry<Integer, ITester> entry : testItemMap.entrySet()) {
					if (entry.getValue().testitems().contains(testitem)) {
						tab.setSelectedIndex(entry.getKey());
						break;
					}
				}
			}

			@Override
			protected List<TestResult> onTest(List<Long> testItems) {
				ITester tester = findTester(testItems.get(0));
//				for (Map.Entry<Integer, ITester> entry : testItemMap.entrySet()) {
//					if (entry.getValue().testitems().contains(testitem)) {
//						tester = entry.getValue();
//						
//						for (Long follow : follows) {
//							if (entry.getValue().testitems().contains(follow)) {
//								ids.add(follow);
//							}
//							else {
//								break;
//							}
//						}
//						break;
//					}
//				}
				return tester.handleSync(testItems);
			}

			@Override
			protected void onComplete() {
				tab.setSelectedIndex(0);
			}

			@Override
			protected void onResult(TestPlan2Element ee) {
				try {
					restClient.post(ee);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected boolean onRequestOtherTester(String message, TestPlan2Element e) {
				tab.setSelectedIndex(0);
				restClient.requestOtherTester(e);
				return onMessage(message);
			}

			@Override
			protected boolean onTestIfValid(TestPlan2Element object) {
				ITester tester = findTester(object.getTestitem());
				if (tester != null) {
					messageBox = createMessageBox("It is your turn.");
					messageBox.setVisible(true);
					messageBox.modal();
					if (messageBox.isOkCliked()) {
						return true;
					}
				}
				return false;
			}
			
			private ITester findTester(Long testitem) {
				ITester tester = null;
				for (Map.Entry<Integer, ITester> entry : testItemMap.entrySet()) {
					if (entry.getValue().testitems().contains(testitem)) {
						tester = entry.getValue();
						return tester;
					}
				}
				return null;
			}

			@Override
			protected void onUpdate(List<TestPlan2Element> testGroup) {
				restClient.uploadResult(testGroup);
			}
		};
		
		tab.addTab("Automation", new AutomationPane(restClient, automator));
		tab.setFont(new Font("Arial", Font.PLAIN, 20));
		
		configurePort(this.ports);
	}
	
	protected abstract void configurePort(List<Port> ports2);

	public MainApplicationPanel application(String name, ITester tester) {
		tester.setListener(testerListener);
		testItemMap.put(tab.getTabCount(), tester);
		this.tab.add(name, tester.panel(this.restClient));
		return this;
	}
	
	private MyMessageBox createMessageBox(String html) {
		if (this.messageBox != null) {
			this.messageBox.cancel();
		}
		MyMessageBox messageBox = new MyMessageBox(tab, html);

		int width = (int)(tab.getWidth()*0.9);
		int height = (int)(tab.getHeight() * 0.8);
		messageBox.setSize(new Dimension( width, height));
		int x = (int)tab.getLocationOnScreen().getX() + (int)(tab.getWidth() * 0.05);
		int y = (int)tab.getLocationOnScreen().getY() + (int)(tab.getHeight()* 0.1);
		messageBox.setLocation(x, y);
		return messageBox;
	}
}
