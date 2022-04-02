package testers;

import java.awt.Component;
import java.util.List;

import com.miyake.demo.entities.MyTesterEntity;
import com.miyake.demo.entities.UserEntity;

import democlient2.MyHttpClient;
import democlient2.RestClient;
import testers.fip.FipCoreSim;
import testers.olts.OltsCoreSim;
import testers.otdr.OtdrCalculator;
import testers.otdr.OtdrCalculatorSim;
import testers.otdr.OtdrCoreSim;

public class MT1000AFrame extends TesterDevFrame {
	public static final String SM1310_1550 = "1310/1550";
	public static final String SM1650 = "1650";
	public static final String OPM = "OPM";
	public static final String FIP = "FIP";
	private OtdrCoreSim otdrCore;
	private OltsCoreSim oltsCore;
	private FipCoreSim fipCore;
	private MT1000APanel mt1000aPanel;

	public MT1000AFrame(RestClient restClient, MyTesterEntity entity) {
		super(restClient, entity);
//
//		
//		application("OTDR", new OtdrPane(new OtdrCoreSim() {
//			@Override
//			public OtdrCalculator createCalculator() {
//				return new OtdrCalculatorSim();
//			}
//		})).
//		application("OLTS", new OltsPane(new OltsCoreSim())).
//		application("FIP", new SingleFipTester());
		
		
		
		otdrCore = new OtdrCoreSim() {
			@Override
			public OtdrCalculator createCalculator() {
				return new OtdrCalculatorSim();
			}
		};
		oltsCore = new OltsCoreSim();
		fipCore = new FipCoreSim("Microscope-SM.jpg", "Microscope-SM_fail.jpg");
		mt1000aPanel = new MT1000APanel(restClient, otdrCore, oltsCore, fipCore);
	}

	@Override
	protected void configurePort(List<Port> ports) {
		ports.add(new Port(MT1000AFrame.FIP, 978, 27));
		ports.add(new Port(MT1000AFrame.OPM, 308, 29));
		ports.add(new Port(MT1000AFrame.SM1650, 535, 39));
		ports.add(new Port(MT1000AFrame.SM1310_1550, 670, 39));
	}

	@Override
	protected String portImage() {
		return this.getClass().getResource("portimage2.bmp").getFile();
	}

	@Override
	protected String backgroundImage() {
		return this.getClass().getResource("mt1000A.bmp").getFile();
	}

	@Override
	protected int mainHeight() {
		return 458;
	}
	@Override
	protected int mainWidth() {
		return 802;
	}

	@Override
	protected int offsetY() {
		return 104;
	}

	@Override
	protected int offsetX() {
		return 130;
	}

	@Override
	protected Component createAppPanel(RestClient restClient) {
		return mt1000aPanel;
	}

	@Override
	protected Component createPortPanel() {
		PortPanel portPanel = new PortPanel(mt1000aPanel.getPorts()) {
			@Override
			protected void onSelect(String name) {
				otdrCore.setSelectedPort(name);
				oltsCore.setSelectedPort(name);
				fipCore.setSelectedPort(name);
			}

			@Override
			protected String portImage() {
				return this.getClass().getResource("portimage2.bmp").getFile();
			}
		};
		return portPanel;
	}
}
