package democlient2;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTable;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.miyake.demo.entities.PortEntity;
import com.miyake.demo.entities.TesterCategoryEntity;

public class TesterCategoryFrame extends ClientFrame<TesterCategoryEntity> {
	
	public static void main(String[] args) {
		MyHttpClient http = new MyHttpClient("http://localhost:8080");		
		new TesterCategoryFrame(http, "tester_category", TesterCategoryEntity.class,
				"tester_categories", TesterCategoryEntity[].class).setVisible(true);
	}
	
	public TesterCategoryFrame(MyHttpClient http, String path, Class clazz,
			String path_get_all, Class class_array) {
		super(http, path, clazz, path_get_all, class_array);
	}

	@Override
	protected void additional(JPanel panel, MyHttpClient http2, JTable table, List<TesterCategoryEntity> list2, List<String> title) {
		// TODO Auto-generated method stub
		
	}

}
