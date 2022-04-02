package democlient2.topology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.miyake.demo.entities.EquipmentEntity;
import com.miyake.demo.entities.PortEntity;

import democlient2.MyHttpClient;

public class OrderCalculator {

	private Map<EquipmentEntity, PortEntity[]> map = new HashMap<>();
	private Map<PortEntity, EquipmentEntity> map2 = new HashMap<>();
	private Map<Long, PortEntity> map3 = new HashMap<>();
	
	public OrderCalculator(MyHttpClient http, EquipmentEntity[] equipments) {

		
		for (EquipmentEntity equipment : equipments) {
			try {
				PortEntity[] ports = http.getObject("PortEntityS?parent=" + equipment.getId(), PortEntity[].class);

				map.put(equipment, ports);
				
				for (PortEntity port : ports) {
					map2.put(port, equipment);
					map3.put(port.getId(), port);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for (PortEntity port : map2.keySet()) {
			if (port.getOpposite() != null) {
				PortEntity portOposite = map3.get(port.getOpposite());
				if (portOposite == null) {
					continue;
				}
				EquipmentEntity equipmentOposite = map2.get(portOposite);
				
				EquipmentEntity equipment = map2.get(port);
				
				setOrder(equipment, equipmentOposite);
				
			}
		}
		
		List<EquipmentScore> list = new ArrayList<>();
		for (Map.Entry<EquipmentEntity, Integer> entry : score.entrySet()) {
			list.add(new EquipmentScore(entry.getKey(), entry.getValue()));
		}
		Collections.sort(list, new Comparator<EquipmentScore>() {

			@Override
			public int compare(EquipmentScore o1, EquipmentScore o2) {
				return o2.score - o1.score;
			}
			
		});
//		System.out.println(order);
		
		List<EquipmentEntity> tmp = new ArrayList<>(Arrays.asList(equipments));
		order.clear();
		for (EquipmentScore e: list) {
			order.add(e.equipment);
			tmp.remove(e.equipment);
		}
		
		order.addAll(tmp);
		
	}

	private List<EquipmentEntity> order = new ArrayList<>();
	private Map<EquipmentEntity, Integer> score = new HashMap<>();
	
	private void setOrder(EquipmentEntity equipmentHigh, EquipmentEntity equipmentLow) {
		int scoreHigh = getScore(equipmentHigh);
		int scoreLow = getScore(equipmentLow);
		
		if (scoreLow >= scoreHigh) {
			setScore(equipmentHigh, scoreLow+1);
		}
		if (!order.contains(equipmentHigh) && !order.contains(equipmentLow)) {
			order.add(0, equipmentLow);
			order.add(0, equipmentHigh);
			
		}
		else if (!order.contains(equipmentHigh) && order.contains(equipmentLow)) {
			int index = order.indexOf(equipmentLow);
			order.add(index, equipmentHigh);
		}
		else if (order.contains(equipmentHigh) && !order.contains(equipmentLow)) {
			int index = order.indexOf(equipmentHigh);
			order.add(index+1, equipmentLow);
		}
	}

	private void setScore(EquipmentEntity equipmentHigh, int i) {
		this.score.put(equipmentHigh, i);
	}

	private int getScore(EquipmentEntity equipment) {
		if (!score.containsKey(equipment)) {
			score.put(equipment, 0);
		}
		return score.get(equipment);
	}

	public PortEntity[] getPorts(EquipmentEntity equipment) {
		return this.map.get(equipment);
	}

	public List<EquipmentEntity> getEquipments() {
		return order;
	}

}
class EquipmentScore {
	public EquipmentScore(EquipmentEntity key, Integer value) {
		this.equipment = key;
		this.score = value;
	}
	public EquipmentEntity equipment;
	public int score;
}