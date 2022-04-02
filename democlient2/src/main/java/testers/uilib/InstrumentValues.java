package testers.uilib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class InstrumentValues {
	protected abstract Long defaultOption(Long id);
	protected abstract Double defaultNumeric(Long id);
	protected abstract String defaultText(Long id);
	
	private Map<Long, Set<ValueListener>> listeners = new HashMap<>();
	private Map<Long, Object> values = new HashMap<>();
	private Map<Long, Boolean> enables = new HashMap<>();
	
	public Long currentOptionId(Long id) {
		if (!values.containsKey(id)) {
			values.put(id, defaultOption(id));
		}
		return (Long)values.get(id);
	}

	public void setCurrentOptionId(Long id, Long optionId) {
		this.values.put(id, optionId);	
		fireChanged(id, optionId);
	}
	
	public void setCurrentNumeric(Long id, Double value) {
		this.values.put(id, value);
		fireChanged(id, value);
	}
	
	private void fireChanged(Long id, Object value) {
		if (this.listeners.containsKey(id)) {
			this.listeners.get(id).forEach(c -> {
				c.onChange(id, value);
			});
		}
		if (this.listeners.containsKey(-1L)) {
			this.listeners.get(-1L).forEach(c -> {
				c.onChange(id, value);
			});
		}
	}

	public void addListener(Long id, ValueListener valueListener) {
		if (!this.listeners.containsKey(id)) {
			this.listeners.put(id, new HashSet<ValueListener>());
		}
		this.listeners.get(id).add(valueListener);
	}

	public void addListener(ValueListener valueListener) {
		Long id = -1L;
		if (!this.listeners.containsKey(id)) {
			this.listeners.put(id, new HashSet<ValueListener>());
		}
		this.listeners.get(id).add(valueListener);
	}
	
	public Integer currentInteger(Long id) {
		if (!values.containsKey(id)) {
			values.put(id, defaultNumeric(id));
		}
		return ((Double)values.get(id)).intValue();
	}
	public String text(Long id) {
		if (!values.containsKey(id)) {
			values.put(id, defaultText(id));
		}
		return this.values.get(id).toString();
	}
	
	public Double numeric(Long id) {
		if (!values.containsKey(id)) {
			values.put(id, defaultNumeric(id));
		}
		return (Double)this.values.get(id);
	}
	public void setEnabled(Long id, boolean enabled) {
		if (this.listeners.containsKey(id)) {
			this.listeners.get(id).forEach(l -> {
				l.onEnableChange(id, enabled);
			});
		}
	}
}
