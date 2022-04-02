package testers.uilib;

import java.util.HashMap;
import java.util.Map;
import com.miyake.demo.entities.PropertyEntity;

import democlient2.RestClient;

public class PropertyCache {
	private Map<Long, PropertyEntity> cache = new HashMap<>();
	private RestClient restClient;
	
	public PropertyCache(RestClient restClient2) {
		this.restClient = restClient2;
	}
	

	public PropertyEntity property(Long id) {
		if (!cache.containsKey(id)) {
			PropertyEntity property = restClient.property(id);
			cache.put(id, property);
		}
		return cache.get(id);
	}


	public double optionRealValue(long id, Long optionid) {
		return property(id).findOption(optionid).getReal_value();
	}
}
