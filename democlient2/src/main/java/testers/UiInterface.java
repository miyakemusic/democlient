package testers;

import com.miyake.demo.entities.PropertyEntity;

import testers.uilib.ValueListener;

public interface UiInterface {
	PropertyEntity property(Long id);
	void addValueListener(Long id, ValueListener valueListener);

	void setCurrentOptionId(Long id, Long optionId);
	double optionRealValue(Long id, Long optionId);

	Long currentOptionId(Long id);
	void setCurrentNumeric(Long id, Double valueOf);
//	Integer currentInteger(Long id);
	String formattedNumeric(Long id);
}