package democlient2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import com.miyake.demo.entities.PortEntity;
import com.miyake.demo.entities.PortTestEntity;

public class PortClientFrame extends ClientFrame<PortEntity> {
	
	public PortClientFrame(MyHttpClient http, String path, Class<?> clazz, 
			String path_get_all, Class<?> class_array) {
		super(http, path, clazz, path_get_all, class_array);
		
	}
	
	public static void main(String[] args) {
		MyHttpClient http = new MyHttpClient("http://localhost:8080");		
		new PortClientFrame(http, "PortEntity", PortEntity.class, "PortEntityS", PortEntity[].class).setVisible(true);
	}

	@Override
	protected void additional(JPanel panel, MyHttpClient http, JTable table, List<PortEntity> list, List<String> title) {
		JButton button = new JButton("Add Tester");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PortEntity port = list.get(table.getSelectedRow());
				PortTestEntity port_tester = new PortTestEntity();
				port_tester.setTestItem(1L);
				port_tester.setPort(port.getId());
				try {
					http.post("PortTestEntity", port_tester);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				updateTable();
			}
		});
		panel.add(button);
	}
}
