package testers.olts;

import java.awt.Component;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.miyake.demo.jsonobject.TestResult;

import democlient2.RestClient;
import democlient2.TestItemDef;
import testers.ITester;
import testers.MT1000AFrame;
import testers.TesterListener;

public class OltsPane implements ITester {
	
//	private DecimalFormat lossFormat = new DecimalFormat("#,###.##");
	private TesterListener listener;
	private OltsCore oltsCore;
	private OltsModel oltsModel;
	
	public OltsPane(OltsCore oltsCore) {
		this.oltsCore = oltsCore;
	}
	
	@Override
	public Component panel(RestClient restClient) {
		oltsModel = new OltsModel(restClient, oltsCore);
		return new OltsUi(oltsModel);
	}
	
	@Override
	public List<Long> testitems() {
		return Arrays.asList(
				TestItemDef.RECEIVE_SIGNAL_CHARACTERRISTICS.RECEIVE_POWER__DBM
				);
	}
	
	@Override
	public List<TestResult> handleSync(List<Long> testitems) {
		listener.requestPort(MT1000AFrame.OPM, this);
		oltsModel.runTestSync(5);
		List<TestResult> ret = new ArrayList<TestResult>();
		ret.add(new TestResult(TestItemDef.TRANSMIT_SIGNAL_CHARACTERISTICS.TRANSMIT_OPTICAL_POWER__DBM, oltsModel.formattedNumeric(InstDef_OLTS.RECEIVE_POWER)));
		ret.add(new TestResult(TestItemDef.RECEIVE_SIGNAL_CHARACTERRISTICS.RECEIVE_POWER__DBM, oltsModel.formattedNumeric(InstDef_OLTS.LOSS).toString()));
		return ret;
	}

	@Override
	public void setListener(TesterListener testerListener) {
		this.listener = testerListener;
	}

}
