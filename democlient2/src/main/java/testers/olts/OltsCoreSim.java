package testers.olts;

import testers.MT1000AFrame;

interface OltsCoreListener {
	void onUpdate(OltsResult oltsResult);
}
public class OltsCoreSim implements OltsCore {

	private boolean stopRequest;
	private OltsCoreListener listener = new OltsCoreListener() {
		@Override
		public void onUpdate(OltsResult oltsResult) {
		}
	};
	private String portName = "";
	
	public OltsCoreSim() {
	}
	
	@Override
	public void runAsync() {
		this.stopRequest = false;
		new Thread() {
			@Override
			public void run() {
				while(!stopRequest) {
					double power = - 20 + Math.random();
					double loss = 20 + Math.random();
					power += Math.random();
					loss += Math.random();
					
					if (!portName.equals(MT1000AFrame.OPM)) {
						power = 0.0;
						loss = 100;
					}
					
					listener.onUpdate(new OltsResult(power, loss));
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}					
				}
			}
		}.start();
	}

	@Override
	public void stopTest() {
		this.stopRequest = true;
	}
	
	@Override
	public void setListener(OltsCoreListener listener) {
		this.listener = listener;
	}

	public void setSelectedPort(String name) {
		this.portName = name;
	}
}
