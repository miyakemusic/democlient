package testers.otdr;

public interface OtdrModelListener {

	void onComplete(double wavelength);

	void onTestUpdate(double wavelength, int average, double[] x2, double[] y2);

}
