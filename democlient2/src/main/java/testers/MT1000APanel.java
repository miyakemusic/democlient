package testers;

import java.util.List;

import democlient2.RestClient;
import testers.fip.FipCore;
import testers.fip.FipPane;
import testers.olts.OltsCore;
import testers.olts.OltsPane;
import testers.otdr.OtdrCore;
import testers.otdr.OtdrPane;

public class MT1000APanel extends MainApplicationPanel {
	public static final String SM1310_1550 = "1310/1550";
	public static final String SM1650 = "1650";
	public static final String OPM = "OPM";
	public static final String FIP = "FIP";
	
	public static void main(String[] arg) {
		
	}
	public MT1000APanel(RestClient restClient, OtdrCore otdrCore, OltsCore oltsCore, FipCore fipCore) {
		super(restClient);
		
		application("OTDR", new OtdrPane(otdrCore)).
		application("OLTS", new OltsPane(oltsCore)).
		application("FIP", new FipPane((fipCore)));
	}

	@Override
	protected String portImage() {
		return this.getClass().getResource("portimage2.bmp").getFile();
	}

	@Override
	protected void configurePort(List<Port> ports) {
		ports.add(new Port(FIP, 978, 27));
		ports.add(new Port(OPM, 308, 29));
		ports.add(new Port(SM1650, 535, 39));
		ports.add(new Port(SM1310_1550, 670, 39));
	}

}
