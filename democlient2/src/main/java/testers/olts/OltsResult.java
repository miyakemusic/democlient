package testers.olts;

import lombok.Data;

@Data
public class OltsResult {

	private double power;
	private double loss;

	public OltsResult(double power, double loss) {
		this.power = power;
		this.loss = loss;
	}

}
