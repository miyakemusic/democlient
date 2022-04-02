package democlient2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.miyake.demo.entities.TesterCategoryEntity;
import com.miyake.demo.entities.TesterEntity;

public class TesterClientFrame extends ProjectFrame<TesterEntity> {

	public static void main(String[] args) {
		MyHttpClient http = new MyHttpClient("http://localhost:8080");		
		new TesterClientFrame(http, TesterEntity.class).setVisible(true);
	}

	private TesterCategoryEntity[] categories;
	
	public TesterClientFrame(MyHttpClient http, Class<?> clazz) {
		super(http, clazz);
	}

	@Override
	protected void onEdit(TesterEntity entity) {
//		TesterConfigPanel.showFrame(getHttp(), entity);
		
		TesterConfigPanel panel = new TesterConfigPanel(getHttp(), entity);
		MyJFrame dialog = new MyJFrame("Tester Config", panel);
		dialog.setModal(true);
		dialog.setVisible(true);
		
		
		if (dialog.isOkClicked()) {
			List<Long> checked = new ArrayList<>();
			for (Map.Entry<Long, Boolean> entry :  panel.getEnabled().entrySet()) {
				 if (entry.getValue()) {
					 checked.add(entry.getKey());
				 }
			}
			Map<String, String> param = new HashMap<>();
			param.put("id", entity.getId().toString());
			param.put("value", checked.toString().replace("[", "").replace("]", ""));
			try {
				getHttp().post("TesterCapabilityEntityS", param);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
}
