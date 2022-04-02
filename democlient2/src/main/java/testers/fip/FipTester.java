package testers.fip;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.miyake.demo.jsonobject.TestResult;

import testers.MT1000AFrame;
import testers.MyImageUtil;
import testers.PortPanel;
import testers.uilib.AbstractTesterPane;

public abstract class FipTester extends AbstractTesterPane {

	private JLabel picture;
	private Object sync = new Object();
	private String portName;
	
	public FipTester() {
		this.setLayout(new BorderLayout());
		picture = new JLabel();
		this.add(picture, BorderLayout.CENTER);
		
		if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
			String arch = System.getProperty("os.arch");
			
			if (arch.equals("amd64")) {
				System.load("C:\\opencv\\build\\java\\x64\\opencv_java451.dll");
			}
			else if (arch.equals("x86")) {
				System.load("C:\\opencv\\build\\java\\x86\\opencv_java451.dll");
			}
		}
	}
	
	@Override
	protected List<TestResult> runTestOnWorkerThread(List<Long> testitems) {
		test();
		List<TestResult> testResults = new ArrayList<>();
		testitems.forEach(item -> testResults.add(new TestResult(item, String.valueOf((int)(Math.random()*4)))));
		return testResults;
	}

	
	private double num = 0;
	private double sigma = 20;
	private boolean stopFlag = false;
	private BufferedImage originalImage;
	
	protected void testAsync() {
		new Thread() {
			@Override
			public void run() {
				test();
			}
		}.start();
	}
	
	protected void test() {
		this.testerListener().requestPort(MT1000AFrame.FIP, this)
		;
        try {
        	File imageFile = new File( this.getClass().getResource(sampleImage()).getFile() );
			originalImage = ImageIO.read(imageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		num = 99;
		sigma = 20;

		while(true) {
			if (stopFlag) {
				break;
			}

			num -= 10;
			Mat mat = MyImageUtil.bufferedImageToMat(originalImage);
			Mat destination = new Mat(mat.rows(),mat.cols(),mat.type());
			if (num < 10) {
				num = 0;
				sigma = 1;
			}
	        Imgproc.GaussianBlur(mat, destination, new Size((int)num, (int)num), sigma);

	        MatOfByte mob=new MatOfByte();
	        Imgcodecs.imencode(".png", destination, mob);
	        
	        onUpdate(mob.toArray());
	        if (num <= 0) {
	        	break;
	        }

//	        if (!portName.equals(PortPanel.FIP)) {
//	        	break;
//	        }
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	abstract protected String sampleImage();
	
	protected void onUpdate(byte[] array) {
		InputStream is = new ByteArrayInputStream(array);

		try {
			BufferedImage bi = ImageIO.read(is);
			picture.setIcon(new ImageIcon(bi));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
