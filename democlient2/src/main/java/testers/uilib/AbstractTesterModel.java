package testers.uilib;

import java.text.DecimalFormat;

import com.miyake.demo.entities.PropertyEntity;

import democlient2.RestClient;
import testers.UiInterface;
import testers.otdr.DependencyCalculator;

public abstract class AbstractTesterModel implements UiInterface {
	private PropertyCache cache;
	private InstrumentValues values;
	
	public AbstractTesterModel(RestClient restClient) {
		cache = new PropertyCache(restClient);
		values = new InstrumentValues() {
			@Override
			protected Long defaultOption(Long id) {
				return cache.property(id).getDefault_option();
			}

			@Override
			protected Double defaultNumeric(Long id) {
				return cache.property(id).getDefault_numeric();
			}

			@Override
			protected String defaultText(Long id) {
				return null;
			}
		};
		values.addListener(new ValueListener() {
			@Override
			public void onChange(Long id, Object value) {
				onValueChange(id, value);
			}

			@Override
			public void onEnableChange(Long id, boolean enabled) {
				
			}
		});
	}
	
	protected abstract void onValueChange(Long id, Object value);

	protected void addDependency(DependencyCalculator calculator) {
		calculator.set(this.cache, this.values);
	}
	
	@Override
	public PropertyEntity property(Long id) {
		return this.cache.property(id);
	}

	@Override
	public void addValueListener(Long id, ValueListener valueListener) {
		this.values.addListener(id, valueListener); // Maybe it should fire after depenency resolved.
	}

	@Override
	public void setCurrentOptionId(Long id, Long optionId) {
		this.values.setCurrentOptionId(id, optionId);
	}

	@Override
	public double optionRealValue(Long id, Long optionId) {
		return this.cache.optionRealValue(id, optionId);
	}

	@Override
	public Long currentOptionId(Long id) {
		return this.values.currentOptionId(id);
	}

	@Override
	public void setCurrentNumeric(Long id, Double valueOf) {
		this.values.setCurrentNumeric(id, valueOf);
	}
//
//	@Override
//	public Integer currentInteger(Long id) {
//		return this.values.currentInteger(id);
//	}

	@Override
	public String formattedNumeric(Long id) {
		PropertyEntity property = this.property(id);
		String sharp = "#,###";
		if (property.getDecimals() != null && property.getDecimals() > 0) {
			sharp += ".";
			for (int i = 0; i < property.getDecimals(); i++) {
				sharp += "#";
			}
		}
		DecimalFormat formatter = new DecimalFormat(sharp);
		
		
		return formatter.format(values.numeric(id));
	}

	public int integer(long id) {
		return this.values.currentInteger(id);
	}
}