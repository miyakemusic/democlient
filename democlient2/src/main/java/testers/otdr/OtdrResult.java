package testers.otdr;

import java.util.List;

import lombok.Data;

@Data
public class OtdrResult {

	private double fiberLength;
	private double totalLoss;
	private double orl;
	private List<Event> events;

}
class Event {
	public Event(double pos, double loss, double reflection) {
		this.pos = pos;
		this.loss = loss;
		this.reflection = reflection;
	}
	double pos;
	double loss;
	double reflection;
}