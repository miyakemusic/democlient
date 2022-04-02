package testers.otdr;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import testers.uilib.InstrumentValues;
import testers.uilib.PropertyCache;
import testers.uilib.ValueListener;

public class DependencyCalculator {
	private Set<ValueListener> listeners = new HashSet<>();
	private InstrumentValues values;
	private PropertyCache cache;
	public DependencyCalculator() {
	}

	protected void handleChange(Long id, Object value) {
		if (id.equals(InstDef_OTDR.AVERAGE_TYPE)) {
			boolean averageTimesEnabled = !values.currentOptionId(InstDef_OTDR.AVERAGE_TYPE).equals(InstDef_OTDR.AVERAGE_TYPE__ATUO);
			values.setEnabled(InstDef_OTDR.AVERAGE_TIMES, averageTimesEnabled);
		}
		else if (id.equals(InstDef_OTDR.STATUS)) {
			if (value.equals(InstDef_OTDR.STATUS__IDLE)) {
				values.setCurrentOptionId(InstDef_OTDR.TEST, InstDef_OTDR.TEST__STOP_TEST);
			}
		}
	}

	public void addListener(ValueListener valueListener) {
		this.listeners.add(valueListener);
	}

	public void set(PropertyCache cache2, InstrumentValues values2) {
		this.values = values2;
		this.cache = cache2;
		
		values.addListener(new ValueListener() {
			@Override
			public void onChange(Long id, Object value) {
				handleChange(id, value);
			}

			@Override
			public void onEnableChange(Long id, boolean enabled) {
			}

		});
	}

}
