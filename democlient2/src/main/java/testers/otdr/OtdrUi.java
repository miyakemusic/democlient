package testers.otdr;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.miyake.demo.entities.PropertyEntity;

import democlient2.MyHttpClient;
import democlient2.RestClient;
import testers.uilib.ButtonArray;
import testers.uilib.UiFactory;



public class OtdrUi extends JPanel {
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
		
		OtdrCalculator calculator = new OtdrCalculatorSim();
		frame.getContentPane().add(new OtdrUi(new OtdrModel(restClient, new OtdrCoreSim() {

			@Override
			public OtdrCalculator createCalculator() {
				return calculator;
			}
			
		})), BorderLayout.CENTER);
		
		frame.setVisible(true);
	}

	public OtdrUi(OtdrModel otdrModel) {
//		this.setSize(new Dimension(1000, 800));
		UiFactory uiFactory = new UiFactory(otdrModel);
		
		this.setLayout(new BorderLayout());
		MyChart chart = new MyChart();
		this.add(chart, BorderLayout.CENTER);
		
		JPanel toolBar = new JPanel();
		toolBar.setLayout(new FlowLayout());
		this.add(toolBar, BorderLayout.NORTH);
		toolBar.add(uiFactory.create(InstDef_OTDR.WAVELENGTH));
		toolBar.add(uiFactory.create(InstDef_OTDR.DISTANCE_RANGE));
		toolBar.add(uiFactory.create(InstDef_OTDR.PULSE_WIDTH));
		toolBar.add(uiFactory.create(InstDef_OTDR.AVERAGE_TYPE));
		toolBar.add(uiFactory.create(InstDef_OTDR.AVERAGE_TIMES));
		
		add(new ButtonArray().button(uiFactory.createToggleButton(InstDef_OTDR.TEST)).button(uiFactory.createToggleButton(InstDef_OTDR.AVERAGE_TYPE)), BorderLayout.EAST);
		
		JPanel resultBar = new JPanel();
		resultBar.setLayout(new FlowLayout());;
		this.add(resultBar, BorderLayout.SOUTH);
		resultBar.add(uiFactory.createLabel(InstDef_OTDR.CURRENT_AVERAGE));
		resultBar.add(uiFactory.createLabel(InstDef_OTDR.STATUS));
		resultBar.add(uiFactory.createLabel(InstDef_OTDR.FIBER_LENGTH));
		resultBar.add(uiFactory.createLabel(InstDef_OTDR.TOTAL_LOSS));
		
		otdrModel.addOtdrListener(new OtdrModelListener() {
			@Override
			public void onComplete(double wavelength) {
			}

			@Override
			public void onTestUpdate(double wavelength, int average, double[] x2, double[] y2) {
				chart.add(String.valueOf(wavelength) + "nm", x2, y2);
			}
		});
	}
}
class MyChart extends JPanel {
	private XYSeriesCollection dataset = new XYSeriesCollection();
	private JFreeChart chart;
	private Map<String, XYSeries> seriesMap = new HashMap<>();
	private Set<String> linkedHashSet = new LinkedHashSet<>();
	
	public MyChart() {
		dataset = new XYSeriesCollection();
		
		chart = ChartFactory.createXYLineChart(
			        "",
			        "X-Axis",
			        "Y-Axis",
			        dataset,
			        PlotOrientation.VERTICAL,
			        true, true, false);
		ChartPanel panel = new ChartPanel(chart);
		
		this.setLayout(new BorderLayout());
		this.add(panel, BorderLayout.CENTER);
	}
	public void add(String title, double[] x, double[] y) {
		linkedHashSet.add(title);
		XYSeries series = new XYSeries(title);
		for (int i = 0; i < x.length; i++) {
			series.add(x[i], y[i]);
		}
		this.seriesMap.put(title, series);

		this.dataset.removeAllSeries();
		
		for (String t : linkedHashSet) {
			this.dataset.addSeries(this.seriesMap.get(t));
		}
	}
	public void clear() {
		this.dataset.removeAllSeries();
	}
}