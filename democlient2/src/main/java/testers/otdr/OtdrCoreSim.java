package testers.otdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import testers.MT1000AFrame;

abstract public class OtdrCoreSim implements OtdrCore {
	public static void main(String[] arg) {
	//	OtdrCore otdr = new OtdrCore(Wavelength.WL1310);
	//	otdr.single();
	}

	private Set<OtdrCoreListener> listeners = new HashSet<>();
	private List<Event> events = new ArrayList<>();
	private double[] x = null;
	private double[] y = null;

//	private OtdrProperty otdrProperty;
	private boolean stopRequest;
	private double noiseAmp = 1.0;
	private double resolution;
	private int averageTimes;
	private double distanceRange;
	private double pulseWidth;
	private Map<Double, Double> attMap = new HashMap<>();
	private OtdrCalculator otdrCalculator;
	private double pulse;
	public OtdrCoreSim() {	
		attMap.put(1310.0, 0.35);
		attMap.put(1550.0, 0.20);
		attMap.put(1625.0, 0.30);
		attMap.put(1650.0, 0.40);
		
		events.add(new Event(1000, 1.2, 0));
		events.add(new Event(2500, 10, 5));
		events.add(new Event(5500, 3.2, 4));
		events.add(new Event(5500, 2, 10));
		events.add(new Event(25500, 60, 10));
		
		this.otdrCalculator = this.createCalculator();
	}
	
	@Override
	public void runTestAsync(double wavelength, double range2, double pulse, boolean autoAverage, int times) {
		if (autoAverage) {
			this.averageTimes = 5;
		}
		else {
			this.averageTimes = times;
		}
		if (range2 <= 0.0) {
			this.distanceRange = 50*1000;
		}
		else {
			this.distanceRange = range2;
		}
		if (pulse <= 0.0) {
			this.pulseWidth = 20;
		}
		else {
			this.pulse = pulse;
		}
		new Thread() {
			@Override
			public void run() {
				stopRequest = false;
				noiseAmp = 1.0;
				for (int i = 0; i < averageTimes; i++) {
					if (stopRequest) {
						break;
					}
					singleSync(wavelength);
					
					noiseAmp = 1.0/Math.sqrt((i+1));
					onUpdate(wavelength, i, x(), y());
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				OtdrResult result = otdrCalculator.calculate(x(), y());
				onComplete(wavelength, result);
			}
		}.start();
	}

	@Override
	public void stopTest() {
		this.stopRequest= true;
	}
	
	@Override
	public void addListener(OtdrCoreListener otdrCoreListener) {
		listeners.add(otdrCoreListener);
	}

	protected void onComplete(double wavelength, OtdrResult result) {
		this.listeners.forEach(l -> {
			l.onComplete(wavelength, result);
		});
	}

	protected void onUpdate(double wavelength, int averaged, double[] x2, double[] y2) {
		this.listeners.forEach(l -> {
			l.onUpdate(wavelength, averaged, x2, y2);
		});
	}

	
	private void singleSync(double wavelength) {
		boolean connected = false;
		if (wavelength == 1650.0) {
			if (this.portName.equals(MT1000AFrame.SM1650)) {
				connected = true;
			}
		}
		else {
			if (this.portName.equals(MT1000AFrame.SM1310_1550)) {
				connected = true;
			}
		}
		Iterator<Event> it = events.iterator();
		
		double lossDB = 0.0;
		double loss = 1.0;
		
		double att = attMap.get(wavelength); // dB/km
		double alpha = calcAlpha(att);
//		List<String> data = new ArrayList<>();
		
		Event event = it.next();
		double reflection = 0;
		double prev_y = 0;
		
		resolution = this.distanceRange / 5000;
		
		int size = (int)(this.distanceRange / resolution);
		x = new double[size];
		y = new double[size];
		
		int index = 0;
		for (double xx = 0.0; xx < this.distanceRange; xx += resolution) {
			x[index] = xx;
			if (event != null && event.pos < xx) {
				lossDB += event.loss;
				loss = toLin(lossDB);
				reflection = event.reflection;
				if (it.hasNext()) {
					event = it.next();
				}
				else {
					event = null;
				}
			}
			double yy = Math.pow(alpha, xx/1000.0) / loss;
			if (!connected) {
				yy = 0.0;
			}
			if (reflection > 0) {
				yy = prev_y * toLin(reflection);
			}
			
			yy = noise(yy);
			y[index] = toDB(yy);
			reflection = 0;
			prev_y = yy;
			index++;
		}
	}
	protected double calcAlpha(double att) {
		double alpha = toLin(-att);
		return alpha;
	}
	private Random random = new Random();
	private String portName = MT1000AFrame.SM1310_1550;
	private double noise(double y) {
		return y + random.nextGaussian() * 1e-4 * noiseAmp;
	}
	private double toDB(double val) {
		if (val < 0.0) {
			return -60;
		}
		double ret = 10 * Math.log10(val);
		if (ret < -60) {
			ret = -60;
		}
		return ret;
	}
	private double toLin(double att) {
		double alpha = Math.pow(10, att/10);
		return alpha;
	}

	public double[] x() {
		return x;
	}
	
	
	public double[] y() {
		return y;
	}
//	public List<Event> getEvents() {
//		return events;
//	}
	
//	public double totalLength() {
//		return this.events.get(events.size() - 1).pos;
//	}
//	public double orl() {
//		double orl = 0.0;
//		for (Event event : events) {
//			orl += event.reflection;
//		}
//		return orl;
//	}
//	public double totalLoss() {
//		Event lastEvent = events.get(events.size()-1);
//		int index = posToIndex(lastEvent.pos);
//		
//		return y[0] - y[index-1];
//	}
	private int posToIndex(double pos) {
		int points = (int)(pos / this.resolution);
		return points;
	}

	public void setSelectedPort(String name) {
		this.portName = name;
	}

}
