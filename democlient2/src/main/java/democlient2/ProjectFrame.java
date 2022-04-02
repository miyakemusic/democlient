package democlient2;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.miyake.demo.entities.ProjectEntity;
import com.miyake.demo.entities.ProjectEntitySimple;
import com.miyake.demo.entities.TesterEntity;
import com.miyake.demo.entities.UserEntity;

import democlient2.table.AllTablePanel;

public class ProjectFrame<T> extends ClientFrame<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static void main(String[] args) {
		MyHttpClient http = new MyHttpClient("http://localhost:8080");
		
		Map<String, String> userPass = new HashMap<>();
		userPass.put("username", "miyakemusic@yahoo.co.jp");
		userPass.put("password", "marijuana");
		
		try {
			http.postForm("login", userPass);
			UserEntity userEntity = http.getObject("me", UserEntity.class);
//			System.out.println(userEntity.getFirstName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ProjectFrame<ProjectEntitySimple> projectFrame = new ProjectFrame<ProjectEntitySimple>(http, ProjectEntitySimple.class) {
			@Override
			protected void onEdit(ProjectEntitySimple entity) {
				new TopologyFrame(entity.getName(), getHttp(), entity.getId()).setVisible(true);
			}
		};
		
		projectFrame.setVisible(true);
	}
	
	protected void onEdit(T entity) {};
	
	public ProjectFrame(MyHttpClient http, Class<?> clazz) {
		super(http, clazz);
	}
	
	
//	@Override
//	protected boolean skipColumn(String name) {
//		List<String> skipColumns = Arrays.asList("id", "usergroup");
//		return skipColumns.contains(name);
//	}

	@Override
	protected List<String> getAdditionalColumns() {
		return Arrays.asList("Edit");
	}
	@Override
	protected void additional(JPanel panel, MyHttpClient http, JTable table, List<T> list2, List<String> title) {
		int columnIndex = table.getColumnCount()-1;
	    ButtonCellRenderer2 renderer = new ButtonCellRenderer2(table, columnIndex) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClick(int row) {
				T entity = list2.get(row);	
				onEdit(entity);
			}
	    	
	    };
        TableColumn column0 = table.getColumnModel().getColumn(columnIndex);
        column0.setCellEditor(renderer);
        column0.setCellRenderer(renderer);

        JButton tester = new JButton("Tester");
        panel.add(tester);
        tester.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new TesterClientFrame(http, TesterEntity.class).setVisible(true);
			}
        });
        
//        JButton test_item = new JButton("Test Item");
//        panel.add(test_item);
//        test_item.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				new MyJFrame("Test Item", new TestItemPanel(new RestClient(http), Mode.Editable)).setVisible(true);
//			}
//        });
//
//        JButton myTesters = new JButton("My Testers");
//        panel.add(myTesters);
//        myTesters.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				new MyJFrame("My Testers", new MyTesterPanel(new RestClient(http))).modal().setVisible(true);;
//			}
//        });
        
        JButton sourceButton = new JButton("Source");
        panel.add(sourceButton);
        sourceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String  text = http.getObject("javasource", String.class);
					String text2 = "package democlient2;\n" + text;
					
					String filename = "C:\\Users\\miyak\\eclipse-workspace\\democlient2\\src\\main\\java\\democlient2\\TestItemDef.java";
					Files.deleteIfExists(Paths.get(filename));
					Files.write(Paths.get(filename), 
							Arrays.asList( text2.split("\n") ), StandardOpenOption.CREATE);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
        });
        
        JButton instSourceButton = new JButton("Inst Source");
        panel.add(instSourceButton);
        instSourceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String  text = http.getObject("instsource?instrument=1", String.class);
					String text2 = "package democlient2;\n" + text;
					
					Files.write(Paths.get("C:\\Users\\miyak\\eclipse-workspace\\democlient2\\src\\main\\java\\democlient2\\InstDef.java"), 
							Arrays.asList( text2.split("\n") ), StandardOpenOption.CREATE);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
        });
        
        JButton tableButton = new JButton("Table");
        panel.add(tableButton);
        tableButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Long col = (Long)table.getValueAt(table.getSelectedRow(), 0);
				Long projectid = Long.valueOf(col);
				MyJFrame myFrame = new MyJFrame("Table", new AllTablePanel(projectid, new RestClient(http)));
				myFrame.setVisible(true);
			}
        });
    }
	
	@Override
	protected Object getAdditionalColumnValue(T port, int columnIndex) {
		return super.getAdditionalColumnValue(port, columnIndex);
	}
}
abstract class ButtonCellRenderer2 extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JButton button;

	public ButtonCellRenderer2(final JTable table, int column) {
		this.button = new JButton("Edit");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireEditingStopped(); 
			}
		});
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Point point = e.getPoint();
				if (table.columnAtPoint(point) == column) {
					//fireEditingStopped();
					int row = table.rowAtPoint(point);
						onClick(row);
				}
			}
		});
	}

	abstract protected void onClick(int row);

	public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		return button;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		return button;
	}

	public Object getCellEditorValue() {
		return button.getText();
	}
}