package testers.fip;

import java.util.List;

import democlient2.RestClient;
import testers.MainApplicationPanel;
import testers.Port;

public class Fip500Panel extends MainApplicationPanel{

	public Fip500Panel(RestClient restClient) {
		super(restClient);
	}

	@Override
	protected String portImage() {
		return this.getClass().getResource("fip500_port2.png").getFile();
	}

	@Override
	protected void configurePort(List<Port> ports) {
		ports.add(new Port("Connector", 210, 40));
	}

}
