package testers;

import com.miyake.demo.jsonobject.TestPlan2Element;

public interface MyWebSocketCallback {

	void onResultUpdate(TestPlan2Element testPlan2Element);

	void onRequestTest(TestPlan2Element object);

}
