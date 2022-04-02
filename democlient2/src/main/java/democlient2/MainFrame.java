package democlient2;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;

import com.miyake.demo.entities.ConnectorEntity;
import com.miyake.demo.entities.EquipmentCategoryEntity;
import com.miyake.demo.entities.EquipmentEntity;
import com.miyake.demo.entities.MyTesterEntity;
import com.miyake.demo.entities.PortDirectionEntity;
import com.miyake.demo.entities.PortEntity;
import com.miyake.demo.entities.PortTestEntity;
import com.miyake.demo.entities.ProjectEntity;
import com.miyake.demo.entities.PropertyEntity;
import com.miyake.demo.entities.PropertyOptionEntity;
import com.miyake.demo.entities.PropertyUnitEntity;
import com.miyake.demo.entities.TestInstrumentEntity;
import com.miyake.demo.entities.TestItemCategoryEntity;
import com.miyake.demo.entities.TestItemEntity;
import com.miyake.demo.entities.TesterCapabilityEntity;
import com.miyake.demo.entities.TesterCategoryEntity;
import com.miyake.demo.entities.TesterEntity;
import com.miyake.demo.entities.UserEntity;
import com.miyake.demo.entities.UserGroupEntity;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		try {
			new MainFrame().setVisible(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public MainFrame() throws IOException {
		this.setSize(new Dimension(800, 400));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		this.getContentPane().add(panel, BorderLayout.CENTER);
		
		MyHttpClient http = new MyHttpClient("http://localhost:8080");		
		RestClient restClient = new RestClient(http);
		MyTesterEntity userEntity = restClient.signin("miyakemusic@yahoo.co.jp", "marijuana");
	
		panel.add(createButton(http, ProjectEntity.class, "usergroup", userEntity.getUsergroup()));
//		panel.add(createButton(http, ProjectEntity.class));

		
		panel.add(createButton(http, PortDirectionEntity.class));
		panel.add(createButton(http, PortEntity.class));
		panel.add(createButton(http, TesterEntity.class));
		panel.add(createButton(http, PortTestEntity.class));
		panel.add(createButton(http, TesterCategoryEntity.class));
		panel.add(createButton(http, TestItemEntity.class));
		panel.add(createButton(http, TestItemCategoryEntity.class));
		panel.add(createButton(http, TesterCapabilityEntity.class));
		panel.add(createButton(http, EquipmentEntity.class));
		panel.add(createButton(http, EquipmentCategoryEntity.class));
		panel.add(createButton(http, ConnectorEntity.class));
		panel.add(createButton(http, UserEntity.class));
		panel.add(createButton(http, UserGroupEntity.class));
//		panel.add(createButton(http, TestInstrumentEntity.class, PropertyEntity.class));
		panel.add(createButton(http, PropertyEntity.class));
		panel.add(createButton(http, PropertyUnitEntity.class));

        JButton instSourceButton = new JButton("Inst Source");
        panel.add(instSourceButton);
        instSourceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					TestInstrumentEntity[] insts = http.getObject("TestInstrumentEntityS", TestInstrumentEntity[].class);
					for (TestInstrumentEntity inst : insts) {
						String packageName = inst.getName().toLowerCase();
						String  text = http.getObject("instsource?instrument=" + inst.getId(), String.class);
						String text2 = "package testers." + packageName + ";\n" + text;
						
						String className = text.split(" ")[2];
						Files.write(Paths.get("C:\\Users\\miyak\\eclipse-workspace\\democlient2\\src\\main\\java\\testers\\" + packageName + "\\" +  className + ".java"), 
								Arrays.asList( text2.split("\n") ), StandardOpenOption.CREATE);
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
        });
	}

	private Component createTestInstrument(MyHttpClient http, String path, Class<?> class1, String path2, Class<?> class2) {
		JButton button = new JButton(path);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new TestInstrumentFrame(http).setVisible(true);
			}
		});
		
		return button;
	}

	class TestInstrumentFrame extends ClientFrame {
		public TestInstrumentFrame(MyHttpClient http) {
			super(http, "TestInstrumentEntity", TestInstrumentEntity.class, "TestInstrumentEntityS", TestInstrumentEntity[].class);
		}
		@Override
		protected void additional(JPanel panel, MyHttpClient http2, JTable table, List list2, List title) {
			JButton button = new JButton("Property");
			panel.add(button);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String id = table.getValueAt(table.getSelectedRow(), 0).toString();
					new PropertyFrame(http2, id).setVisible(true);
				}
			});
		}
	}
	
	class PropertyFrame extends ClientFrame {
		private String testinstrument;

		public PropertyFrame(MyHttpClient http, String argument) {
			super(http, "PropertyEntity", PropertyEntity.class, "PropertyEntityS?testinstrument=" + argument, PropertyEntity[].class);
			this.testinstrument = argument;
		}

		@Override
		protected void additional(JPanel panel, MyHttpClient http, JTable table, List list2, List title2) {
			JButton options = new JButton("Options");
			panel.add(options);
			options.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String id = table.getValueAt(table.getSelectedRow(), 0).toString();
					ClientFrame optionFrame = new ClientFrame(http, "PropertyOptionEntity", 
							PropertyOptionEntity.class, "PropertyOptionEntityS?property=" + id, PropertyOptionEntity[].class) {

						@Override
						protected Object newInstance() {
							PropertyOptionEntity po = new PropertyOptionEntity();
							po.setProperty(Long.valueOf(id));
							return po;
						}

					};
					optionFrame.setVisible(true);
					optionFrame.addWindowListener(new WindowAdapter() {
						
						@Override
						public void windowClosing(WindowEvent e) {
							try {
								String optionid = optionFrame.value("id");
								table.setValueAt(optionid, table.getSelectedRow(), title2.indexOf("default_option"));
							}
							catch (Exception e2) {
								
							}
						}

						@Override
						public void windowClosed(WindowEvent e) {

						}			
					});

				}
			});
		}

		@Override
		protected Object newInstance() {
			PropertyEntity e = new PropertyEntity();
			e.setTestinstrument(Long.valueOf(this.testinstrument));
			return e;
		}
		
		
	}
	
	interface NextLayer {
		void onNext(String string);
	}
	private JButton createMultiButton(MyHttpClient http, String path, Class<?> class1, String path2, Class<?> class2, String buttonName, NextLayer nextLayer) {
		JButton button = new JButton(path);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ClientFrame(http, path, class1, path2, class2) {
					@Override
					protected void additional(JPanel panel, MyHttpClient http2, JTable table, List list2, List title) {
						JButton button = new JButton(buttonName);
						panel.add(button);
						button.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								nextLayer.onNext(table.getValueAt(table.getSelectedRow(), 0).toString());
							}
						});
					}
				}.setVisible(true);
			}
		});
		
		return button;
	}
	
	private JButton createPort(MyHttpClient http, String path, Class<?> class1, String path2, Class<?> class2) {
		JButton button = new JButton(path);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new PortClientFrame(http, path, class1, path2, class2).setVisible(true);
			}
		});
		
		return button;
	}
	
	interface NewInstance {
		Object newInstance(String id);
	}
	private JButton createProperty(MyHttpClient http, String path, Class<?> class1, String path2, Class<?> class2, 
			String nextPath, Class<?> nextClass1, String nextPath2, Class<?> nextClass2, NewInstance newInstance) {
		JButton button = new JButton(path);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ClientFrame clientFrame = new ClientFrame(http, path, class1, path2, class2) {
					@Override
					protected void additional(JPanel panel, MyHttpClient http2, JTable table, List list2, List title2) {
						JButton options = new JButton("Options");
						panel.add(options);
						options.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								String id = table.getValueAt(table.getSelectedRow(), 0).toString();
								ClientFrame optionFrame = new ClientFrame(http, nextPath, nextClass1, nextPath2 + id, nextClass2) {

									@Override
									protected Object newInstance() {
										return newInstance.newInstance(id);
									}

								};
								optionFrame.setVisible(true);
								try {
									String optionid = optionFrame.value("id");
									table.setValueAt(optionid, table.getSelectedRow(), title2.indexOf("default_option"));
								}
								catch (Exception e2) {
									
								}
							}
						});
					}
				};
				clientFrame.setVisible(true);
			}
		});
		
		return button;
	}
	
//
//	private JButton createButton(MyHttpClient http, Class<?> class1,
//			Class<?> class2) {
//
//		JButton button = new JButton(class1.getSimpleName());
//		button.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				new ClientFrame(http, class1, null, null) {
//					@Override
//					protected void additional(JPanel panel, MyHttpClient http2, JTable table, List list2, List title2) {
//						JButton button2 = new JButton(class2.getSimpleName());
//						panel.add(button2);
//						button2.addActionListener(new ActionListener() {
//							@Override
//							public void actionPerformed(ActionEvent e) {
//								try {
//									String val = value("id");
//									new ClientFrame(http, class2, val) {
//										
//									}.setVisible(true);
//									
//								} catch (Exception e1) {
//									e1.printStackTrace();
//								}
//								
//							}
//						});
//					}
//				}.setVisible(true);
//			}
//		});
//		
//		return button;
//	}
	private JButton createButton(MyHttpClient http, Class<?> class1) {
		JButton button = new JButton(class1.getSimpleName());
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ClientFrame(http, class1) {
					
				}.setVisible(true);
			}
		});
		
		return button;
	}
	
	private JButton createButton(MyHttpClient http, Class<?> class1, String parentField, Long parentId) {
		JButton button = new JButton(class1.getSimpleName());
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ClientFrame(http, class1, parentField, parentId) {
					
				}.setVisible(true);
			}
		});
		
		return button;
	}
	
	private JButton createButton(MyHttpClient http, String path, Class<?> class1, String path2, Class<?> class2) {
		JButton button = new JButton(path);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ClientFrame(http, path, class1, path2, class2).setVisible(true);
			}
		});
		
		return button;
	}
}
