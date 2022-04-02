package testers.otdr;

import java.awt.Component;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.miyake.demo.jsonobject.TestResult;

import democlient2.RestClient;
import democlient2.TestItemDef;
import testers.ITester;
import testers.MT1000AFrame;
import testers.TesterListener;

//class OtdrResult {
//	double fiberLength;
//	double orl;
//	double totalLosss;
//}
public class OtdrPane implements ITester {
	private Object sync = new Object();
//	private DecimalFormat distanceFormat = new DecimalFormat("#,###.##");
//	private DecimalFormat lossFormat = new DecimalFormat("#,###.##");
	private String portName;
	private OtdrModel otdrModel;
	private TesterListener testerListener;
	private OtdrCore otdrCore;
	
	public OtdrPane(OtdrCore otdrCore) {
		this.otdrCore = otdrCore;
	}

	@Override
	public List<Long> testitems() {
		return Arrays.asList(
				TestItemDef.FIBER_CHARACTERISTICS.CONNECTION_COUNT,
				TestItemDef.FIBER_CHARACTERISTICS.FUSION_COUNT,
				TestItemDef.FIBER_CHARACTERISTICS.OPTICAL_FIBER_LENGTH__KM,
				TestItemDef.FIBER_CHARACTERISTICS.OPTICAL_RETURN_LOSS__DB,
				TestItemDef.FIBER_CHARACTERISTICS.TOTAL_LINK_LOSS__DB,
				TestItemDef.FIBER_CHARACTERISTICS.WORST_REFLECTION
				);
	}

	@Override
	public Component panel(RestClient restClient) {
		otdrModel = new OtdrModel(restClient, otdrCore);
		OtdrUi otdr = new OtdrUi(otdrModel);
		return otdr;
	}
	@Override
	public List<TestResult> handleSync(List<Long> testitems) {
		Set<Long> wavelengths = new OtdrIdChecker(testitems).wavelengths();
	
		ResultConverter resultConverter = new ResultConverter();
		resultConverter.wavelength(InstDef_OTDR.WAVELENGTH__1310_NM).idFiberLength(TestItemDef.FIBER_CHARACTERISTICS.OPTICAL_FIBER_LENGTH__KM).idOrl(TestItemDef.FIBER_CHARACTERISTICS.OPTICAL_RETURN_LOSS__DB).idTotalLength(TestItemDef.FIBER_CHARACTERISTICS.TOTAL_LINK_LOSS__DB);
		resultConverter.wavelength(InstDef_OTDR.WAVELENGTH__1550_NM).idFiberLength(TestItemDef.FIBER_CHARACTERISTICS.OPTICAL_FIBER_LENGTH__KM).idOrl(TestItemDef.FIBER_CHARACTERISTICS.OPTICAL_RETURN_LOSS__DB).idTotalLength(TestItemDef.FIBER_CHARACTERISTICS.TOTAL_LINK_LOSS__DB);
//		resultConverter.wavelength(InstDef.WAVELENGTH__1625_NM).idFiberLength(TestItemDef.FIBER_CHARACTERISTICS.fiber).idOrl(TestItemDef.FIBER_CHARACTERISTICS.retu).idTotalLength(TestItemDef.FIBER_CHARACTERISTICS.TOTAL_LINK_LOSS_1550NM);
		resultConverter.wavelength(InstDef_OTDR.WAVELENGTH__1650_NM).idFiberLength(TestItemDef.FIBER_CHARACTERISTICS.OPTICAL_FIBER_LENGTH__KM).idOrl(TestItemDef.FIBER_CHARACTERISTICS.OPTICAL_RETURN_LOSS__DB).idTotalLength(TestItemDef.FIBER_CHARACTERISTICS.TOTAL_LINK_LOSS__DB);

		
		for (Long wavelength : wavelengths) {
			if (wavelength.equals(InstDef_OTDR.WAVELENGTH__1650_NM)) {
				testerListener.requestPort(MT1000AFrame.SM1650, this);
			}
			else {
				testerListener.requestPort(MT1000AFrame.SM1310_1550, this);
			}
//			this.otdrModel.values().setCurrentOptionId(InstDef_OTDR.WAVELENGTH, wavelength);
			this.otdrModel.runSync(wavelength);
			//this.otdrModel.values().setCurrentOptionId(InstDef.TEST, InstDef.TEST__START_TEST);
			
			String fiberLength = this.otdrModel.formattedNumeric(InstDef_OTDR.FIBER_LENGTH);
			String orl = this.otdrModel.formattedNumeric(InstDef_OTDR.OPTICAL_RETURN_LOSS);
			String totalLoss = this.otdrModel.formattedNumeric(InstDef_OTDR.TOTAL_LOSS);
			
			resultConverter.wavelength(wavelength).resultFiberLength(fiberLength).resultOrl(orl).resultTotalLoss(totalLoss);
		}
		
		return resultConverter.convert();
		
	}
	@Override
	public void setListener(TesterListener testerListener) {
		this.testerListener = testerListener;
	}
}

class ResultConverter {
	private Map<Long, ResultConverterElement> results = new HashMap<>();

	public ResultConverterElement wavelength(Long wavelength) {
		if (!results.containsKey(wavelength)) {
			results.put(wavelength, new ResultConverterElement());
		}
		return this.results.get(wavelength);
	}

	public List<TestResult> convert() {
		List<TestResult> ret = new ArrayList<>();
		results.forEach( (wavelength, element) -> {
			element.results.forEach((type, result) -> {
				if (result.value != null) {
					ret.add(new TestResult(result.id, result.value.toString()));
				}
			});
		});
		return ret;
	}
}

class ResultConverterElement{
	enum ResultType {
		FiberLength,
		ORL,
		TotalLoss
	}
	class ResultPair {
		public ResultPair(Long id2) {
			this.id = id2;
		}
		Long id;
		String value;
	}
	
	public Map<ResultType, ResultPair> results = new HashMap<>();
	
	public ResultConverterElement idFiberLength(Long id) {
		this.results.put(ResultType.FiberLength, new ResultPair(id));
		return this;
	}

	public ResultConverterElement resultTotalLoss(String totalLoss) {
		this.results.get(ResultType.TotalLoss).value = totalLoss;
		return this;
	}

	public ResultConverterElement resultOrl(String orl) {
		this.results.get(ResultType.ORL).value = orl;
		return this;
	}

	public ResultConverterElement resultFiberLength(String fiberLength) {
		this.results.get(ResultType.FiberLength).value = fiberLength;
		return this;
	}

	public ResultConverterElement idTotalLength(Long id) {
		this.results.put(ResultType.TotalLoss, new ResultPair(id));
		return this;
	}

	public ResultConverterElement idOrl(Long id) {
		this.results.put(ResultType.ORL, new ResultPair(id));
		return this;
	}

}
class OtdrIdChecker {
	private List<Long> testitems;
	private List<Long> wl1310 = Arrays.asList(TestItemDef.FIBER_CHARACTERISTICS.TOTAL_LINK_LOSS__DB, TestItemDef.FIBER_CHARACTERISTICS.OPTICAL_FIBER_LENGTH__KM, TestItemDef.FIBER_CHARACTERISTICS.OPTICAL_RETURN_LOSS__DB);
	private List<Long> wl1550 = Arrays.asList(TestItemDef.FIBER_CHARACTERISTICS.TOTAL_LINK_LOSS__DB, TestItemDef.FIBER_CHARACTERISTICS.OPTICAL_FIBER_LENGTH__KM, TestItemDef.FIBER_CHARACTERISTICS.OPTICAL_RETURN_LOSS__DB);
	private List<Long> wl1625 = Arrays.asList(TestItemDef.FIBER_CHARACTERISTICS.TOTAL_LINK_LOSS__DB, TestItemDef.FIBER_CHARACTERISTICS.OPTICAL_FIBER_LENGTH__KM, TestItemDef.FIBER_CHARACTERISTICS.OPTICAL_RETURN_LOSS__DB);
	private List<Long> wl1650 = Arrays.asList(TestItemDef.FIBER_CHARACTERISTICS.TOTAL_LINK_LOSS__DB, TestItemDef.FIBER_CHARACTERISTICS.OPTICAL_FIBER_LENGTH__KM, TestItemDef.FIBER_CHARACTERISTICS.OPTICAL_RETURN_LOSS__DB);	
					
	public OtdrIdChecker(List<Long> testitems) {
		this.testitems = testitems;
	}

	public Set<Long> wavelengths() {
		Set<Long> ret = new HashSet<>();
		for (Long testitem : testitems) {
			if (wl1310.contains(testitem)) {
				ret.add(InstDef_OTDR.WAVELENGTH__1310_NM);
			}
			else if (wl1550.contains(testitem)) {
				ret.add(InstDef_OTDR.WAVELENGTH__1550_NM);
			}
			else if (wl1625.contains(testitem)) {
				ret.add(InstDef_OTDR.WAVELENGTH__1625_NM);
			}
			else if (wl1650.contains(testitem)) {
				ret.add(InstDef_OTDR.WAVELENGTH__1650_NM);
			}
		}
		return ret;
	}
	
}