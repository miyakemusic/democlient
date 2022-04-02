package democlient2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ClientFrame<T> extends JFrame {
	private List<Field> skipColumns = new ArrayList<>();
	
	protected List<String> getAdditionalColumns() {
		return new ArrayList<String>();
	}
	
	protected List<String> getTitles(Class clazz) {
		List<String> ret = new ArrayList<>();
		for (Field field : clazz.getDeclaredFields()) {
			boolean skip = skipColumn(field.getName());
			if (skip) {
				skipColumns.add(field);
			}
			else {
				ret.add(field.getName());
			}
		}
		
		ret.addAll(this.getAdditionalColumns());
		return ret;
	}
	
	protected boolean skipColumn(String name) {
		return false;
	}

	protected T[] retreive(MyHttpClient http2) throws JsonParseException, JsonMappingException, IOException {
		return (T[]) http.getObject(this.path_get_all, this.class_array);
		
	}
	protected void additional(JPanel panel, MyHttpClient http2, JTable table, List<T> list2, List<String> title2) {};
	protected T newInstance() {
		return newinstance();
	}
	
	
	private List<T> list = new ArrayList<T>();
	private MyHttpClient http;
	private AbstractTableModel model;
	private String path;
	private Class<?> clazz;
	private String path_get_all;
	private Class<?> class_array;
	private List<String> title;
	private Long parentid;
	
	private T newinstance() {
		try {
			return (T)clazz.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private JTable table;
	private String parentField;
	
	protected void updateValue(T port, Object aValue, int columnIndex) {
		if ((port.getClass().getDeclaredFields().length - skipColumns.size()) <= columnIndex) {
			return;
		}
		
		Field field = fieldAtColumn(port, columnIndex);
		String fieldName = field.getName();
		String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		
		try {
			Method method = port.getClass().getDeclaredMethod(methodName, field.getType());
			Object argument = null;
			if (field.getType().isEnum()) {
				for (Object o : field.getType().getEnumConstants()) {
					if (o.toString().equals(aValue.toString())) {
						argument = o;
						break;
					}
				}
			}
			else {
				if (field.getType().equals(Long.class)) {
					argument = Long.valueOf(aValue.toString());
				}
				else if (field.getType().equals(Integer.class)) {
					argument = Integer.valueOf(aValue.toString());
				}
				else if (field.getType().equals(Double.class)) {
					argument = Double.valueOf(aValue.toString());
				}
				else if (field.getType().equals(String.class)) {
					argument = aValue;
				}
				else if (field.getType().equals(Date.class)) {
					argument = dateFormat.parse( aValue.toString() );
				}
			}
			Object obj = method.invoke(port, argument);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} 
	}

	private Field fieldAtColumn(T port, int columnIndex) {
		int c = 0;
		for (int i = 0; i < port.getClass().getDeclaredFields().length; i++) {
			Field field = port.getClass().getDeclaredFields()[i];
			if (skipColumns.contains(field)) {
				continue;
			}
			if (c == columnIndex) {
				return field;
			}
			c++;
		}
		return null;
	}


	public ClientFrame(MyHttpClient http2, Class<?> clazz) {
		try {
			String path = clazz.getSimpleName();
			String path_get_all = path + "S";
			//Class<?> class_array = Class.forName(clazz.getName()).arrayType();
			Class<?> class_array = Class.forName("[L" + clazz.getName() + ";");
			
			initialize(http2, path, clazz, path_get_all, class_array);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public ClientFrame(MyHttpClient http2, Class<?> clazz, String parentField, Long parent) {
		try {
			String path = clazz.getSimpleName();
			String path_get_all = path + "S";
//			Class<?> class_array = Class.forName(clazz.getName()).arrayType();
			Class<?> class_array = Class.forName("[L" + clazz.getName() + ";");
			this.parentid = parent;
			this.parentField = parentField;
			
			initialize(http2, path, clazz, path_get_all + "?parent=" + parent, class_array);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public ClientFrame(MyHttpClient http, String path, Class clazz, String path_get_all, Class class_array) {		
		initialize(http, path, clazz, path_get_all, class_array);
	}

	private void initialize(MyHttpClient http, String path, Class clazz, String path_get_all, Class class_array) {
		this.setTitle(path);
		this.path = path;
		this.http = http;
		this.clazz = clazz;
		
		this.path_get_all = path_get_all;
		this.class_array = class_array;
				
		this.setSize(new Dimension(800, 400));
		this.getContentPane().setLayout(new BorderLayout());
		
		title = getTitles(clazz);
		
		model = new AbstractTableModel() {
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return true;
			}

			@Override
			public String getColumnName(int column) {
				return title.get(column);
			}

			@Override
			public int getRowCount() {
				return list.size();
			}

			@Override
			public int getColumnCount() {
				return title.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				T port = list.get(rowIndex);
				return valueAt(port, columnIndex);
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				T port = list.get(rowIndex);
				updateValue(port, aValue, columnIndex);
				try {
					http.post(path, port);
				} catch (IOException e) {
					e.printStackTrace();
				}
				updateTable();
			}
		};
		
		table = new JTable(model);
		table.setAutoCreateRowSorter(true);
		this.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		this.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new FlowLayout());
		
		JButton update = new JButton("Update");
		panel.add(update);
		update.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateTable();
			}
		});
		
		JButton addPort = new JButton("Add");
		panel.add(addPort);
		addPort.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					T obj = newInstance();
					if (parentField != null) {
						Method method = obj.getClass().getDeclaredMethod("set" + toUpperFirst(parentField), Long.class);
						method.invoke(obj, parentid);
					}
					
					http.post(path, obj);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				updateTable();
			}
		});
		
		JButton delete = new JButton("Delete");
		panel.add(delete);
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					http.delete(path + "?id=" + value("id"));
					updateTable();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		List<String> fieldNames = new ArrayList<>();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getType().equals(List.class)) {
				JButton optionButton = new JButton(field.getName());
				panel.add(optionButton);
				java.lang.reflect.Type t = field.getGenericType();
				String [] tmp = t.getTypeName().split("[<>]+");
				String className = tmp[tmp.length-1];
//				System.out.println(className);
				optionButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							Long val = Long.valueOf(value("id"));
							
							new ClientFrame<>(http, Class.forName(className), clazz.getSimpleName().replace("Entity", "").toLowerCase(), val) {
								@Override
								protected void onSelect(String value) {
									T object = ClientFrame.this.selectedObject();
									String methodName = "setDefault_" + field.getName().replace("_list", "");
									try {
										Method method = object.getClass().getMethod(methodName, Long.class);
										method.invoke(object, Long.valueOf(value));
										ClientFrame.this.http.post(path, object);
										ClientFrame.this.updateTable();
									} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e) {
										e.printStackTrace();
									}
								}
							}.setVisible(true);
						} catch (Exception e1) {
							e1.printStackTrace();
						};
					}
				});
			}
			else {
				fieldNames.add(field.getName());
			}
		}
		
		
		Set<String> selectedOption = new HashSet<>();
		for (int i = 0; i < fieldNames.size(); i++) {
			String fieldName = fieldNames.get(i);
			for (int j = 0; j < fieldNames.size(); j++) {
				String fieldName2 = fieldNames.get(j);
				if (fieldName.equals(fieldName2 + "Entity")) {
					selectedOption.add(fieldName2);
				}
			}
		}
		for (String selButton : selectedOption) {
			JButton button = new JButton(selButton);
			panel.add(button);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Long val = Long.valueOf(value("id"));
						new ClientFrame<>(http, clazz.getDeclaredField(selButton + "Entity").getType(), selButton, val) {
							@Override
							protected void onSelect(String value) {
								String setterName = toUpperFirst(selButton);
								setterName = "set"  + setterName;
								try {
									Method method = clazz.getDeclaredMethod(setterName, Long.class);
									T object = ClientFrame.this.selectedObject();
									method.invoke(object, Long.valueOf(value));
									postSelectedObject(object);
								} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
									e.printStackTrace();
								}
							}
						}.setVisible(true);
					}
					catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			});
			
			table.getColumnModel().getColumn(title.indexOf(selButton)).setMinWidth(0);
			table.getColumnModel().getColumn(title.indexOf(selButton)).setMaxWidth(0);
			table.getColumnModel().getColumn(title.indexOf(selButton)).setWidth(0);
		}
		
		additional(panel, http, table, list, title);
		regCombobox(table);
		
		updateTable();

		this.add(new OkCancelPanel() {
			@Override
			protected void onCancel() {
				ClientFrame.this.setVisible(false);
			}

			@Override
			protected void onOk() {
				ClientFrame.this.setVisible(false);
				try {
					String val = value("id");
					onSelect(val);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}
		}, BorderLayout.SOUTH);
	}
	
	protected void onSelect(String value) {
	}

	protected void regCombobox(JTable table) {
	    for (int i = 0; i < this.clazz.getDeclaredFields().length; i++) {
	    	Field field = this.clazz.getDeclaredFields()[i];
	    	if (field.getType().isEnum()) {
	    	    JComboBox<String> combo = new JComboBox<>();
	    	    for (Object o : field.getType().getEnumConstants()) {
	    	    	combo.addItem(o.toString());
	    	    }
	    	    
	    	    TableColumn col = table.getColumnModel().getColumn(i);
			    col.setCellEditor(new DefaultCellEditor(combo));
	    	}
	    }
	}
	
	protected void updateTable() {
		try {
			list.clear();
			list.addAll(Arrays.asList(retreive(http)));
			model.fireTableDataChanged();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	protected Object valueAt(T port, int columnIndex) {
		if  ((port.getClass().getDeclaredFields().length - this.skipColumns.size()) <= columnIndex) {
			return getAdditionalColumnValue(port, columnIndex);
		}
		Field field = this.fieldAtColumn(port, columnIndex);// port.getClass().getDeclaredFields()[columnIndex];
		String fieldName = field.getName();
		String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		
		try {
			Method method = port.getClass().getDeclaredMethod(methodName);
			Object obj = method.invoke(port);
			
			if (obj == null) {
				return "";
			}
			if (field.getType().equals(Date.class)) {
				obj = dateFormat.format(obj);
			}
			return obj;
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return "";
	}

	protected Object getAdditionalColumnValue(T port, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	protected MyHttpClient getHttp() {
		return http;
	}

	public String value(String title) throws Exception {
		if (this.table.getSelectedRow() < 0) {
			throw new Exception();
		}
		return table.getValueAt(this.table.getSelectedRow(), title.indexOf(title)).toString();
	}

	private T selectedObject() {
		int index = table.convertRowIndexToModel(ClientFrame.this.table.getSelectedRow());
		T object = list.get(index);
		return object;
	}
	
	private void postSelectedObject(T object) {
		try {
			http.post(path, object);
		} catch (IOException e) {
			e.printStackTrace();
		}
		updateTable();
	}

	private String toUpperFirst(String selButton) {
		return selButton.substring(0, 1).toUpperCase() + selButton.substring(1, selButton.length());
	}
}
