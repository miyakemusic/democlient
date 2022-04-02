package testers.olts;

public interface OltsCore {

	void stopTest();

	void setListener(OltsCoreListener listener);

	void runAsync();

}
