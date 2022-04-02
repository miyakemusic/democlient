package testers.fip;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import democlient2.TestItemDef;

public class MpoFipTester  extends FipTester {
	static class Inner {
		public static int VALUE= 0;
	}
	public static void main(String []arg) {
		JFrame frame = new JFrame();
		frame.setSize(new Dimension(800, 600));
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new MpoFipTester(), BorderLayout.CENTER);
		frame.setVisible(true);
	}
	
	public MpoFipTester() {
		super();
		
		JButton start = new JButton("Test");
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(start);
		this.add(panel, BorderLayout.NORTH);
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testAsync();
			}
		});
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
				TestItemDef.FIBER_END_FACE_CHARACTERRISTICS.CORE_SCRACHES,
				TestItemDef.FIBER_END_FACE_CHARACTERRISTICS.CORE_DEFECTS_MPO12,
				TestItemDef.FIBER_END_FACE_CHARACTERRISTICS.CORE_SCRACHES_MPO12
				);
	}

	@Override
	protected String sampleImage() {
		return "manta.jpg";
	}

}
