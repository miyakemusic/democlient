package testers.fip;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import democlient2.TestItemDef;
import testers.MT1000AFrame;
import testers.PortPanel;

public class SingleFipTester extends FipTester {
	public static void main(String []arg) {
		JFrame frame = new JFrame();
		frame.setSize(new Dimension(800, 600));
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new SingleFipTester(), BorderLayout.CENTER);
		frame.setVisible(true);
	}

	private String portName = "";
	
	
	public SingleFipTester() {
		super();
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(5, 1));
		JButton start = new JButton("Start");
		panel.add(start);
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testAsync();
			}
		});
		this.add(panel, BorderLayout.EAST);
	}

	@Override
	public List<Long> testitems() {
		return Arrays.asList(
				TestItemDef.FIBER_END_FACE_CHARACTERRISTICS.ADHESIVE_DEFECTS,
				TestItemDef.FIBER_END_FACE_CHARACTERRISTICS.ADHESIVE_SCRACHES,
				TestItemDef.FIBER_END_FACE_CHARACTERRISTICS.CLADDING_DEFECTS,
				TestItemDef.FIBER_END_FACE_CHARACTERRISTICS.CLADDING_SCRACHES,
				TestItemDef.FIBER_END_FACE_CHARACTERRISTICS.CONTACT_DEFECTS,
				TestItemDef.FIBER_END_FACE_CHARACTERRISTICS.CONTACT_SCRATCHES,
				TestItemDef.FIBER_END_FACE_CHARACTERRISTICS.CORE_DEFECTS,
				TestItemDef.FIBER_END_FACE_CHARACTERRISTICS.CORE_SCRACHES
		);
	}
	
	@Override
	protected String sampleImage() {
		if (portName.equals(MT1000AFrame.FIP)) {
			return "Microscope-SM.jpg";
		}
		else {
			return "Microscope-SM_fail.jpg";
		}
	}

//	@Override
//	public void currentPort(String name) {
//		this.portName = name;
//	}
//	
}
