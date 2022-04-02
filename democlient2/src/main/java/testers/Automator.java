package testers;

import java.util.ArrayList;
import java.util.List;

import com.miyake.demo.entities.PassFailEnum;
import com.miyake.demo.jsonobject.TestPlan2;
import com.miyake.demo.jsonobject.TestPlan2Element;
import com.miyake.demo.jsonobject.TestResult;
import com.miyake.demo.shared.PassFailCalculator;

public abstract class Automator {
	
	private Long currentEquipment = -1L;
	private Long currentPort = -1L;
	private String currentDirection = "";
	private boolean single = false;
	private boolean equipmentChanged;
	private boolean portChanged;
	private boolean directionChanged;
	
	protected abstract void onComplete();
	protected abstract List<TestResult> onTest(List<Long> testItems);
	protected abstract boolean onMessage(String message);
	protected abstract void onChangeApplication(Long testitem);
	protected abstract boolean onTestIfValid(TestPlan2Element object);
	
	private PassFailCalculator passFailCalculator = new PassFailCalculator();
	
	public void runAll(TestPlan2 testPlan) {
		single = false;
		new Thread() {
			@Override
			public void run() {
				loop(testPlan, 0);
			}
		}.start();

	}

	public void runAllFrom(TestPlan2 testPlan, int index) {
		loop(testPlan, index);
	}
	
	private void loop(TestPlan2 testPlan, int startIndex) {
		currentEquipment = -1L;
		currentPort = -1L;
		currentDirection = "";
		
		for (int i = startIndex; i < testPlan.filteredElements().size(); i++) {
			TestPlan2Element e = testPlan.filteredElements().get(i);
			if (!e.getPassFail().equals(PassFailEnum.Untested) && !single) {
				continue;
			}
			onChangeApplication(e.getTestitem());
			
			if (!e.isMytest()) {
				boolean ret = onRequestOtherTester("<html> Use other tester to test <BR>" +
						testPlan.presentation().testItem(e.getTestitem()) + "</html>", e);
				
				if (!ret) {
					break;
				}			
				continue;
			}
			else if (changed(e)) {
				boolean ret = onMessage("<html><body>Let's test..<br>" + 
						"<font size=\"10\" weight=\"bold\">" + testPlan.presentation().testItem(e.getTestitem()) + "</font><br>" +
						"Connet fiber to... <br>" + 
						"<table border width=\"1\" width=\"80%\">" + 
						"<tr><td>Equipment</td><td>" + equipmentText(testPlan, e) + "</td></tr>" + 
						"<tr><td>Port</td><td>" + portText(testPlan, e) + "</td></tr>" + 
						"<tr><td>Direction</td><td>" + directionText(e) + "</td></tr>" + 
						"</table>" + 
						"<img src=\"" + "file:" + this.getClass().getResource(e.getDirection().equals("Equipment") ? "equipmentside.PNG": "fiberside.PNG").getFile() + "\" alt=\"Red dot\" />" + 
						"</body></html>");
				
				if (!ret) {
					break;
				}
				
			}
			
			List<TestPlan2Element> testGroup = new ArrayList<>();
			testGroup.add(e);
			String group = e.group();
			for (int j = i + 1; j < testPlan.filteredElements().size(); j++) {
				TestPlan2Element e2 = testPlan.filteredElements().get(j);
				if (group.equals(e2.group())) {
					testGroup.add(e2);
				}
			}
			
			List<TestResult> results = onTest(toIdList(testGroup));	
			
			for (TestPlan2Element gourpElement : testGroup) {
				for (TestResult result : results) {
					if (gourpElement.getTestitem().equals(result.getTestitem())) {
						gourpElement.setResult(result.getValue());
						gourpElement.setPassFail(passFailCalculator.judgePassFail(gourpElement.getCriteria(), gourpElement.getResult()));
					}
				}
			}
			
			onUpdate(testGroup);
			if (single) {
				break;
			}
		}
		onComplete();
	}

	protected abstract void onUpdate(List<TestPlan2Element> testGroup);
	private List<Long> toIdList(List<TestPlan2Element> testGroup) {
		List<Long> ret = new ArrayList<>();
		testGroup.forEach(c -> {
			ret.add(c.getTestitem());
		});
		return ret;
	}
	protected abstract boolean onRequestOtherTester(String string, TestPlan2Element e);

	protected abstract void onResult(TestPlan2Element ee);

	private String directionText(TestPlan2Element e) {
		String ret = e.getDirection();
		ret = decorate(ret, this.directionChanged);
		return ret;
	}

	private String decorate(String ret, boolean changed) {
		if (changed) {
			ret = "<font face=\"Arial\" color=\"RED\" font-weight=\"bold\">" + ret + "</font>";
		}
		return ret;
	}

	private String portText(TestPlan2 testPlan, TestPlan2Element e) {
		String ret = testPlan.presentation().port(e.getPort());
		ret = decorate(ret, this.portChanged);
		return ret;
	}

	private String equipmentText(TestPlan2 testPlan, TestPlan2Element e) {
		String ret = testPlan.presentation().equipment(e.getEquipment());
		ret = decorate(ret, this.equipmentChanged);
		return ret;
	}

	
	private boolean changed(TestPlan2Element e) {
		this.equipmentChanged = this.currentEquipment != e.getEquipment();
		this.portChanged = this.currentPort != e.getPort();
		this.directionChanged = !this.currentDirection.equals(e.getDirection());
		
		boolean ret = equipmentChanged || portChanged || directionChanged;
		this.currentEquipment = e.getEquipment();
		this.currentPort = e.getPort();
		this.currentDirection = e.getDirection();
		return ret;
	}

	public void runSingle(TestPlan2 testPlan, int index) {
		single  = true;
		new Thread() {
			@Override
			public void run() {
				loop(testPlan, index);
			}
		}.start();
	}

	public boolean runIfValid(TestPlan2Element object) {
		boolean ret = onTestIfValid(object);
		return ret;
	}

	

}
