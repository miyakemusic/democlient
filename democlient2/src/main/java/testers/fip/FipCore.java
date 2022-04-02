package testers.fip;

public interface FipCore {

	void stopTest();

	void runAsync();

	void addFipCoreListener(FipCoreListener listener);

}
