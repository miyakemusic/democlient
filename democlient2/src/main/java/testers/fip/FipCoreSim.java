package testers.fip;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import testers.MT1000APanel;
import testers.MyImageUtil;

public class FipCoreSim implements FipCore {
	private Set<FipCoreListener> listeners = new HashSet<>();
	private String sampleImage;
	private boolean stopRequest = false;
	private String portName = "";
	private String failImage;
	
	public FipCoreSim(String sampleImage, String failImage) {
		if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
			String arch = System.getProperty("os.arch");
			
			if (arch.equals("amd64")) {
				System.load("C:\\opencv\\build\\java\\x64\\opencv_java451.dll");
			}
			else if (arch.equals("x86")) {
				System.load("C:\\opencv\\build\\java\\x86\\opencv_java451.dll");
			}
		}
		this.sampleImage = sampleImage;
		this.failImage = failImage;
	}
	
	@Override
	public void stopTest() {
		this.stopRequest = true;
	}
	
	@Override
	public void runAsync() {
		new Thread() {
			@Override
			public void run() {
				try {
					runSync();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	private void runSync() throws IOException {
		String imageName = "";
		if (portName.equals(MT1000APanel.FIP)) {
			imageName = this.sampleImage;
		}
		else {
			imageName = this.failImage;
		}
		
    	File imageFile = new File( this.getClass().getResource(imageName).getFile() );
    	BufferedImage originalImage = ImageIO.read(imageFile);
    	
    	double num = 99;
    	double sigma = 20;

		while(true) {
			if (stopRequest) {
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
	        
	        this.listeners.forEach(l -> {
	        	l.onUpdate(mob.toArray());
	        });
	        
	        if (num <= 0) {
	        	break;
	        }

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.listeners.forEach(l -> {
			l.onComplete();
		});
	}
	
	@Override
	public void addFipCoreListener(FipCoreListener listener) {
		this.listeners.add(listener);
	}

	public void setSelectedPort(String name) {
		this.portName = name;
	}
}
