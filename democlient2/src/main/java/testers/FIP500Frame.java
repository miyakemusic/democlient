package testers;

import java.awt.Component;
import java.util.List;

import javax.swing.JPanel;

import com.miyake.demo.entities.MyTesterEntity;
import com.miyake.demo.entities.UserEntity;

import democlient2.RestClient;
import testers.fip.Fip500Panel;
import testers.fip.MpoFipTester;

public class FIP500Frame extends TesterDevFrame {

	public FIP500Frame(RestClient restClient, MyTesterEntity entity) {
		super(restClient, entity);
	}

	@Override
	protected String backgroundImage() {
		return this.getClass().getResource("fip500.bmp").getFile();
	}

	@Override
	protected void configurePort(List<Port> ports) {
		ports.add(new Port("Connector", 210, 40));
	}

	@Override
	protected String portImage() {
		return this.getClass().getResource("fip500_port2.png").getFile();
	}

	@Override
	protected int mainHeight() {
		return 502;
	}

	@Override
	protected int mainWidth() {
		return 380;
	}

	@Override
	protected int offsetY() {
		return 123;
	}

	@Override
	protected int offsetX() {
		return 165;
	}

	@Override
	protected Component createAppPanel(RestClient restClient2) {
		return new Fip500Panel(restClient2);
	}

	@Override
	protected Component createPortPanel() {
		return new JPanel();
	}

}
