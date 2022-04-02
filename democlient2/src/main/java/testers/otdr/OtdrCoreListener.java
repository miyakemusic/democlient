package testers.otdr;

public interface OtdrCoreListener {
	void onComplete(double wavelength, OtdrResult result);
	void onUpdate(double wavelength, int averaged, double[] x2, double[] y2);
}
