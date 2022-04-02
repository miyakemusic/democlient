package testers.otdr;

public interface OtdrCore {

	void runTestAsync(double wavelength, double range2, double pulse, boolean autoAverage, int times);

	void stopTest();

	void addListener(OtdrCoreListener otdrCoreListener);

	OtdrCalculator createCalculator();

}
