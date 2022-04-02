package testers.otdr;

import java.util.HashSet;
import java.util.Set;

import democlient2.RestClient;
import testers.uilib.AbstractTesterModel;
import testers.uilib.ValueListener;

public class OtdrModel extends AbstractTesterModel {
	private Set<OtdrModelListener> listeners = new HashSet<>();

	private Object sync = new Object();
	private OtdrCore otdrCore;
	
	public OtdrModel(RestClient restClient, OtdrCore otdrCore2) {
		super(restClient);
		
		this.otdrCore = otdrCore2;

		DependencyCalculator calculator = new DependencyCalculator();
		this.addDependency(calculator);
//				
//		ValueListener valueListener = new ValueListener() {
//			@Override
//			public void onChange(Long id, Object value) {
//
//			}
//
//			@Override
//			public void onEnableChange(Long id, boolean enabled) {}
//		};
//		this.addValueListener(InstDef_OTDR.TEST, valueListener);

		
		otdrCore.addListener(new OtdrCoreListener() {
			@Override
			public void onComplete(double wavelength, OtdrResult otdrResult) {
				listeners.forEach(l -> {
					l.onComplete(wavelength);
				});
				OtdrModel.this.setCurrentNumeric(InstDef_OTDR.TOTAL_LOSS, otdrResult.getTotalLoss());
				OtdrModel.this.setCurrentNumeric(InstDef_OTDR.FIBER_LENGTH, otdrResult.getFiberLength());
				OtdrModel.this.setCurrentNumeric(InstDef_OTDR.OPTICAL_RETURN_LOSS, otdrResult.getOrl());

				OtdrModel.this.setCurrentOptionId(InstDef_OTDR.STATUS, InstDef_OTDR.STATUS__IDLE);
				
				synchronized(sync) {
					sync.notify();
				}
			}

			@Override
			public void onUpdate(double wavelength, int averaged, double[] x2, double[] y2) {
				listeners.forEach(l -> {
					l.onTestUpdate(wavelength, averaged, x2, y2);
				});
				OtdrModel.this.setCurrentNumeric(InstDef_OTDR.CURRENT_AVERAGE, Double.valueOf(averaged+1));
			}
		});
	}

	public void runTestAsync() {	
		this.setCurrentOptionId(InstDef_OTDR.STATUS, InstDef_OTDR.STATUS__RUNNING_TEST);
		Long rangeOption = this.currentOptionId(InstDef_OTDR.DISTANCE_RANGE);
		Long wavelengthOption = this.currentOptionId(InstDef_OTDR.WAVELENGTH);
		Long pulseOption = this.currentOptionId(InstDef_OTDR.PULSE_WIDTH);

		double range = this.optionRealValue(InstDef_OTDR.DISTANCE_RANGE, rangeOption);
		double wavelength = this.optionRealValue(InstDef_OTDR.WAVELENGTH, wavelengthOption);
		double pulse = this.optionRealValue(InstDef_OTDR.PULSE_WIDTH, pulseOption);
		boolean autoAverage = this.currentOptionId(InstDef_OTDR.AVERAGE_TYPE).equals(InstDef_OTDR.AVERAGE_TYPE__ATUO);
		int averageTimes = super.integer(InstDef_OTDR.AVERAGE_TIMES);
		

		this.setCurrentOptionId(InstDef_OTDR.STATUS, InstDef_OTDR.STATUS__RUNNING_TEST);
		otdrCore.runTestAsync(wavelength, range, pulse, autoAverage, averageTimes);
	}

	public void addOtdrListener(OtdrModelListener otdrModelListener) {
		this.listeners.add(otdrModelListener);
	}

	public void runSync(Long wavelength) {
		this.setCurrentOptionId(InstDef_OTDR.WAVELENGTH, wavelength);
		this.runTestAsync();
		synchronized(sync) {
			try {
				sync.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onValueChange(Long id, Object value) {
		if (id.equals(InstDef_OTDR.TEST)) {
			if (value.equals(InstDef_OTDR.TEST__START_TEST)) {
				runTestAsync();
			}
			else if (value.equals(InstDef_OTDR.TEST__STOP_TEST)) {
				
			}
		}
	}

}
