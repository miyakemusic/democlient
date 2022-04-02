package testers.otdr;

import java.util.ArrayList;
import java.util.List;

public class OtdrCalculatorSim implements OtdrCalculator {

	@Override
	public OtdrResult calculate(double[] x, double[] y) {
		
		List<Event> events = new ArrayList<>();
		events.add(new Event(1000, 1.2, 0));
		events.add(new Event(2500, 10, 5));
		events.add(new Event(5500, 3.2, 4));
		events.add(new Event(5500, 2, 10));
		events.add(new Event(25500, 60, 10));
		
		OtdrResult otdrResult = new OtdrResult();
		otdrResult.setEvents(events);
		
		otdrResult.setFiberLength(34.0 + Math.random());
		otdrResult.setTotalLoss(23.0 + Math.random());
		otdrResult.setOrl(-30 + Math.random());
		return otdrResult;
	}

}
