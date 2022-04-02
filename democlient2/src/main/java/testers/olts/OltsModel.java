package testers.olts;

import democlient2.RestClient;
import testers.uilib.AbstractTesterModel;

public class OltsModel extends AbstractTesterModel {

	private OltsCore oltsCore;

	public OltsModel(RestClient restClient, OltsCore oltsCore) {
		super(restClient);
		this.oltsCore = oltsCore;
		this.oltsCore.setListener(new OltsCoreListener() {
			@Override
			public void onUpdate(OltsResult oltsResult) {
				OltsModel.this.setCurrentNumeric(InstDef_OLTS.RECEIVE_POWER, oltsResult.getPower());
				OltsModel.this.setCurrentNumeric(InstDef_OLTS.LOSS, oltsResult.getLoss());
			}
		});
	}

	@Override
	protected void onValueChange(Long id, Object value) {
		if (id.equals(InstDef_OLTS.TEST)) {
			if (value.equals(InstDef_OLTS.TEST__START)) {
				runTest();
			}
			else {
				stopTest();
			}
		}
	}

	private void stopTest() {
		this.oltsCore.stopTest();
	}

	private void runTest() {
		oltsCore.runAsync();
	}

	public void runTestSync(int average) {
		this.runTest();
		try {
			Thread.sleep(average * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		oltsCore.stopTest();
	}
}

