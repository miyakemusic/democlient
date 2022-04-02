package testers.fip;

import java.util.HashSet;
import java.util.Set;

import democlient2.RestClient;
import testers.uilib.AbstractTesterModel;

public class FipModel extends AbstractTesterModel{

	private FipCore fipCore;
	private Object sync = new Object();
	private Set<FipModelListener> listeners = new HashSet<>();
	
	public FipModel(RestClient restClient, FipCore fipCore) {
		super(restClient);
		
		this.fipCore = fipCore;

		fipCore.addFipCoreListener(new FipCoreListener() {
			@Override
			public void onUpdate(byte[] array) {
				listeners.forEach(l -> {
					l.onUpdate(array);
				});
			}

			@Override
			public void onComplete() {
				synchronized(sync) {
					sync.notify();
				}
				setCurrentOptionId(InstDef_FIP.TEST, InstDef_FIP.TEST__STOP_TEST);
			}
			
		});
	}

	@Override
	protected void onValueChange(Long id, Object value) {
		if (id.equals(InstDef_FIP.TEST)) {
			if (value.equals(InstDef_FIP.TEST__START_TEST)) {
				fipCore.runAsync();
			}
			else if (value.equals(InstDef_FIP.TEST__STOP_TEST)) {
				
			}
		}
	}

	public void runSync() {
		this.fipCore.runAsync();
		synchronized(sync) {
			try {
				sync.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void addFipModelListener(FipModelListener fipModelListener) {
		this.listeners.add(fipModelListener);
	}

}
