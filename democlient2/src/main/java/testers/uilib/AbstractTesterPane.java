package testers.uilib;

import java.awt.Component;
import java.util.List;

import javax.swing.JPanel;

import com.miyake.demo.jsonobject.TestResult;

import democlient2.RestClient;
import testers.ITester;
import testers.TesterListener;

public abstract class AbstractTesterPane extends JPanel implements ITester{
	private TesterListener testerListener;
	abstract protected List<TestResult> runTestOnWorkerThread(List<Long> testitems);
	
	protected TesterListener testerListener() {
		return testerListener;
	}

	@Override
	public void setListener(TesterListener testerListener) {
		this.testerListener = testerListener;
	}


	@Override
	public Component panel(RestClient restClient) {
		return this;
	}


	private List<TestResult> ret = null;
	@Override
	public List<TestResult> handleSync(List<Long> testitems) {
		Object sync = new Object();
		new Thread() {
			@Override
			public void run() {
				ret = runTestOnWorkerThread(testitems);
				synchronized(sync) {
					sync.notify();
				}
			}
		}.start();
		try {
			synchronized(sync) {
				sync.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
}
