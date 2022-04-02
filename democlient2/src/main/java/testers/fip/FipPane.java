package testers.fip;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.miyake.demo.jsonobject.TestResult;

import democlient2.RestClient;
import democlient2.TestItemDef;
import testers.ITester;
import testers.MT1000AFrame;
import testers.TesterListener;

public class FipPane implements ITester{

	private FipModel fipModel;
	private FipCore fipCore;
	private TesterListener testerListener;

	public FipPane(FipCore fipCore) {
		this.fipCore = fipCore;
	}

	@Override
	public Component panel(RestClient restClient) {
		this.fipModel = new FipModel(restClient, fipCore);
		return new FipUi(fipModel);
	}

	@Override
	public List<Long> testitems() {
		return Arrays.asList(
				TestItemDef.FIBER_END_FACE_CHARACTERRISTICS.ADHESIVE_DEFECTS,
				TestItemDef.FIBER_END_FACE_CHARACTERRISTICS.ADHESIVE_SCRACHES,
				TestItemDef.FIBER_END_FACE_CHARACTERRISTICS.CLADDING_DEFECTS,
				TestItemDef.FIBER_END_FACE_CHARACTERRISTICS.CLADDING_SCRACHES,
				TestItemDef.FIBER_END_FACE_CHARACTERRISTICS.CONTACT_DEFECTS,
				TestItemDef.FIBER_END_FACE_CHARACTERRISTICS.CONTACT_SCRATCHES,
				TestItemDef.FIBER_END_FACE_CHARACTERRISTICS.CORE_DEFECTS,
				TestItemDef.FIBER_END_FACE_CHARACTERRISTICS.CORE_SCRACHES
		);
	}

	@Override
	public List<TestResult> handleSync(List<Long> testitems) {
		this.testerListener.requestPort(MT1000AFrame.FIP, this);
		this.fipModel.runSync();
		
		List<TestResult> ret = new ArrayList<TestResult>();
		for (Long testitem : testitems) {
			Integer v = (int)(Math.random() * 3);
			ret.add(new TestResult(testitem, v.toString()));
		}
		
		return ret;
	}

	@Override
	public void setListener(TesterListener testerListener) {
		this.testerListener = testerListener;
	}

}
