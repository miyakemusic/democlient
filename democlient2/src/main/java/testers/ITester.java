package testers;

import java.awt.Component;
import java.util.List;

import com.miyake.demo.jsonobject.TestResult;

import democlient2.RestClient;

public interface ITester {

	Component panel(RestClient restClient);

	List<Long> testitems();

	List<TestResult> handleSync(List<Long> testitems);

//	void currentPort(String name);

	void setListener(TesterListener testerListener);

}
