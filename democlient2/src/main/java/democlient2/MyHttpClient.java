package democlient2;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miyake.demo.entities.PortEntity;
//import com.miyake.demo.restobject.Port;
import com.miyake.demo.entities.PortTestEntity;
import com.miyake.demo.jsonobject.TestPlan2Element;
import com.miyake.demo.jsonobject.WebSocketSignal;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import testers.MyWebSocketCallback;

public class MyHttpClient {

	public static void main(String[] arg) {
		
		MyHttpClient http = new MyHttpClient("http://localhost:8080");
		
//		String users2 = http.get("json");
		
		http.login();
			
	    Map<String, String> formParamMap = new HashMap<>();
	    formParamMap.put("username", "miyakemusic@yahoo.co.jp");
	    formParamMap.put("password", "marijuana");
	    
		try {
			http.postForm("login", formParamMap);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		http.get("auth");
		
		try {
			User users = http.getObject("json", User.class);
			System.out.println(users.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			
			PortEntity port = new PortEntity();
			port.setPort_name("port1");
			http.post("port", port);
			
			port.setPort_name("port2");
			http.post("PortEntity", port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
	private String csrf = "";

	public <T> T  getObject(String path, Class<T> class1) throws JsonParseException, JsonMappingException, IOException {
		String string = this.get(path);

		if (class1.equals(String.class)) {
			return (T)string;
		}
		return new ObjectMapper().readValue(string, class1);
	}

	void login(/*MyHttpClient http*/) {
		String response = get("login");

		for (String  s: response.split("\n")) {
			if (s.contains("_csrf")) {
				String []tmp = s.split("value=\"");
				csrf = tmp[1].split("\"")[0];
				break;
			}
		}
	}

	public void post(String path, Object object) throws IOException {
		String json = new ObjectMapper().writeValueAsString(object);
	    RequestBody requestBody = RequestBody.create(JSON, json);

		doPost(path, requestBody);
	}
	
	public void postForm(String path, Map<String, String> formParamMap) throws IOException {
		if (!csrf.isBlank()) {
			formParamMap.put("_csrf", csrf);
		}
	    final FormBody.Builder formBuilder = new FormBody.Builder();
	    formParamMap.forEach(formBuilder::add);
	    RequestBody requestBody = formBuilder.build();

	    doPost(path, requestBody);
	}

	private void doPost(String path, RequestBody requestBody) throws IOException {
		Request.Builder builder = new Request.Builder();
		if (!cookie.isBlank()) {
			builder.addHeader("Cookie", cookie);
		}
		if (!X_CSRF_TOKEN.isBlank()) {
			builder.addHeader("X-CSRF-TOKEN", X_CSRF_TOKEN);
		}
	
		Request request =  builder.url(url + "/" + path).post(requestBody).build();

	    Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            System.out.println("error!!");
        }
        if (response.body() != null) {
			List<String> Cookielist = response.headers().values("Set-Cookie");
			if (Cookielist.size() > 0) {
				cookie = Cookielist.get(0);
			}
        }
        response.close();
	}

	private String url;
	private String cookie = "";
	private OkHttpClient okHttpClient;
	private String X_CSRF_TOKEN = "";
	
	public String get(String path) {
		Request request = new Request.Builder().addHeader("Cookie", cookie).url(url + "/" + path).build();
		
		Call call = okHttpClient.newCall(request);

		try {
			Response response = call.execute();
			List<String> Cookielist = response.headers().values("Set-Cookie");
			if (Cookielist.size() > 0) {
				cookie = (Cookielist .get(0).split(";"))[0];
			}
						
			ResponseBody body = response.body();
			
			String string = body.string();
			response.close();
			for (String s : string.split("\n")) {
				if (s.contains("<meta name=\"_csrf\" content=\"")) {
					String[] tmp = s.split("[\"]+");
					X_CSRF_TOKEN = tmp[3];
				}
				else if (s.contains("")) {
					
				}
			}
			response.close();
			return string;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public MyHttpClient(String url) {
		this.url = url;
	    okHttpClient = new OkHttpClient.Builder().followRedirects(false)
	            .build();
	}

	public void delete(String path) {
	    Request request = new Request.Builder()
	            .url(url + "/" + path)
	            .addHeader("Cookie", cookie)
	            .delete()
	            .build();

	    try (Response response = okHttpClient.newCall(request).execute()) {
	        int responseCode = response.code();
	        System.out.println("responseCode: " + responseCode);

	        if (!response.isSuccessful()) {
	            System.out.println("error!!");
	        }
	        if (response.body() != null) {
	            System.out.println("body: " + response.body().string());
	        }
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void webSocket(String endpoint, MyWebSocketCallback myWebSocketCallback) {
		OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(endpoint).addHeader("Cookie", cookie).build();
        WebSocket ws = client.newWebSocket(request, new WebSocketListener () {
			@Override
			public void onClosed(WebSocket webSocket, int code, String reason) {
				// TODO Auto-generated method stub
				super.onClosed(webSocket, code, reason);
			}

			@Override
			public void onClosing(WebSocket webSocket, int code, String reason) {
				// TODO Auto-generated method stub
				super.onClosing(webSocket, code, reason);
			}

			@Override
			public void onFailure(WebSocket webSocket, Throwable t, Response response) {
				t.printStackTrace();
			}

			@Override
			public void onMessage(WebSocket webSocket, ByteString bytes) {
				
			}

			@Override
			public void onMessage(WebSocket webSocket, String text) {
				try {
					WebSocketSignal signal = new ObjectMapper().readValue(text, WebSocketSignal.class);
					
					if (signal.getSignalType().equals(WebSocketSignal.SignalType.ResultUpdated)) {
//						myWebSocketCallback.onResultUpdate((TestPlan2Element)signal.getObject());
					}
					else if (signal.getSignalType().equals(WebSocketSignal.SignalType.RequestTest)) {
//						myWebSocketCallback.onRequestTest((TestPlan2Element)signal.getObject());
					}
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onOpen(WebSocket webSocket, Response response) {
				// TODO Auto-generated method stub
				super.onOpen(webSocket, response);
			}
        });
 
        okHttpClient.dispatcher().executorService().shutdown();
	}

	public void delete(Object object) {
		try {
			String json = new ObjectMapper().writeValueAsString(object);
		    RequestBody requestBody = RequestBody.create(JSON, json);
		    
			Request.Builder builder = new Request.Builder();
			if (!cookie.isBlank()) {
				builder.addHeader("Cookie", cookie);
			}
			if (!X_CSRF_TOKEN.isBlank()) {
				builder.addHeader("X-CSRF-TOKEN", X_CSRF_TOKEN);
			}
		
			Request request =  builder.url(url + "/" + object.getClass().getSimpleName()).delete(requestBody).build();
	
		    Response response = okHttpClient.newCall(request).execute();
	        if (!response.isSuccessful()) {
	            System.out.println("error!!");
	        }
	        if (response.body() != null) {
				List<String> Cookielist = response.headers().values("Set-Cookie");
				if (Cookielist.size() > 0) {
					cookie = Cookielist.get(0);
				}
	        }
	        response.close();
		}
		catch (Exception e) {
			
		}
	}

}
