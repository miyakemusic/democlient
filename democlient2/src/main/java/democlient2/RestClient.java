package democlient2;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.miyake.demo.entities.EquipmentEntity;
import com.miyake.demo.entities.EquipmentEntitySimple;
import com.miyake.demo.entities.MyTesterEntity;
import com.miyake.demo.entities.PortDirectionEntity;
import com.miyake.demo.entities.PortEntity;
import com.miyake.demo.entities.PortEntitySimple;
import com.miyake.demo.entities.PortPresentationEntity;
import com.miyake.demo.entities.PortTestEntity;
import com.miyake.demo.entities.PortTestTemplateEntity;
import com.miyake.demo.entities.PropertyEntity;
import com.miyake.demo.entities.TestItemCategoryEntity;
import com.miyake.demo.entities.TestItemEntity;
import com.miyake.demo.entities.TestScenarioEntity;
import com.miyake.demo.entities.TesterEntity;
import com.miyake.demo.entities.EquipmentPresentationEntity;
import com.miyake.demo.entities.UserEntity;
import com.miyake.demo.jsonobject.DiagramItemContainers;
import com.miyake.demo.jsonobject.PortTemplate;
import com.miyake.demo.jsonobject.TestCaseRequest;
import com.miyake.demo.jsonobject.TestItemList;
import com.miyake.demo.jsonobject.TestPlan2;
import com.miyake.demo.jsonobject.TestPlan2Element;
import com.miyake.demo.jsonobject.TestResult;

import testers.MyWebSocketCallback;

public class RestClient {

	private MyHttpClient http;

	public RestClient(MyHttpClient http2) {
		this.http = http2;
	}

	public TestItemEntity[] test_items() {
		try {
			return  http.getObject("TestItemEntityS", TestItemEntity[].class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		return null;
	}

	public TestItemCategoryEntity[] test_item_categories() {
		try {
			return  http.getObject("TestItemCategoryEntityS", TestItemCategoryEntity[].class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		return null;
	}

	public void post(Object object) throws IOException {
		if (object instanceof TestItemEntity) {
			http.post("test_item", object);
		}
//		else if (object instanceof UserGroupEntity) {
//			http.post("user_group", object);
//		}
		else if (object instanceof TestResult) {
			http.post("test_result", object);
		}
		else if (object instanceof MyTesterEntity) {
			http.post("mytester", object);
		}
		else if (object instanceof TestPlan2Element) {
			http.post("test_result", object);
		}
		else {
			if (object.getClass().isArray()) {
				http.post(object.getClass().getSimpleName().replace("[]", "S"), object);
			}
			else {
				http.post(object.getClass().getSimpleName(), object);
			}
		}
	}

	public MyHttpClient http() {
		return this.http;
	}

	public void delete(Object object) {
		if (object instanceof TestItemEntity) {
			http.delete("TestItemEntity?id=" + ((TestItemEntity)object).getId());
		}
		else if (object instanceof MyTesterEntity) {
			http.delete("MyTesterEntity?id=" + ((MyTesterEntity)object).getId());
		}
		else {
			http.delete(object);
		}
	}

	public MyTesterEntity signin(String username, String password) throws IOException {
		Map<String, String> userPass = new HashMap<>();
		userPass.put("username", username);
		userPass.put("password", password);
		http.postForm("login", userPass);
		return myinfo();
	}


	public void signout() {
		this.http.delete("signout");
	}
	
	public MyTesterEntity myinfo() throws JsonParseException, JsonMappingException, IOException {
		return http.getObject("testerOnline", MyTesterEntity.class);
	}

	public MyTesterEntity[] mytesters() {
		try {
			return http.getObject("mytesters", MyTesterEntity[].class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public TesterEntity[] testers() {
		try {
			return http.getObject("TesterEntityS", TesterEntity[].class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public PortEntity[] ports(Long equipment_id) {
		try {
			return http.getObject("PortEntityS?parent=" + equipment_id, PortEntity[].class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public PortEntitySimple[] portsSimple(Long equipment_id) {
		try {
			return http.getObject("PortEntitySimpleS?parent=" + equipment_id, PortEntitySimple[].class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public TestItemList testitems(Long equipment_id) {
		try {
			return http.getObject("testitemlist?equipmentid=" + equipment_id, TestItemList.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public TestItemList applyTester(Long equipment_id, Long myTesterId) {
		try {
			return http.getObject("applyMyTester?equipmentid=" + equipment_id + "&mytesterid=" + myTesterId, TestItemList.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	public void clearResults(Long scenario_id) {
		this.http.delete("clear_results?scenario_id=" + scenario_id);
	}
	
	public TestPlan2 testPlan(Long id) {
		try {
			return http.getObject("testPlan2?id=" + id, TestPlan2.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void testStomp() {
		WebSocketClient client = new StandardWebSocketClient();
	    String url = "ws://localhost:8080/ws";
	    client.doHandshake(new AbstractWebSocketHandler() {

	        @Override
	        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
	            session.sendMessage(new TextMessage("{\"cmd\":\"system-monitor\",\"params\":{\"type\":\"cpu\"}}"));
	        }

	        @Override
	        public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
	            System.out.println(message.getPayload());
	        }

			@Override
			public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
				exception.printStackTrace();
			}

			@Override
			protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
				// TODO Auto-generated method stub
				super.handleTextMessage(session, message);
			}

			@Override
			public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
				// TODO Auto-generated method stub
				super.afterConnectionClosed(session, status);
			}
	        
	        
	    }, url);
	    try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void webSocket3(String endpoint) {
		URI stompUrlEndpoint = null;
		try {
			stompUrlEndpoint = new URI("ws://localhost:8080/wd");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		//Calls initialize() after the container applied all property values.
		taskScheduler.afterPropertiesSet();

		StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
		List<Transport> transports = new ArrayList<>(2);

		transports.add(new WebSocketTransport(webSocketClient));

		SockJsClient sockJsClient = new SockJsClient(transports);   

		WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);

		stompClient.setMessageConverter(new SimpleMessageConverter());          // default converter: SimpleMessageConverter

		// Configure a scheduler to use for heartbeats and for receipt tracking. 
		stompClient.setTaskScheduler(taskScheduler);

		StompSessionHandlerImp stompSessionHandlerImp = new StompSessionHandlerImp();

		ListenableFuture<StompSession> stompSessionFuture2 = stompClient.connect(stompUrlEndpoint.toString(), stompSessionHandlerImp);
	}

	class StompSessionHandlerImp extends StompSessionHandlerAdapter {
		private StompSession session;
	
	
		@Override
		public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
			this.session =  session;
		
			session.setAutoReceipt(true);
			session.subscribe("/user/queue/bancaria.readcard", new StompFrameHandler() {
	
				@Override
				public Type getPayloadType(StompHeaders headers) {
					// TODO Auto-generated method stub
					return null;
				}
		
				@Override
				public void handleFrame(StompHeaders headers, Object payload) {
					// TODO Auto-generated method stub
					
				}
			});
		}
	}
	   
	public void webSocket(String endpoint, MyWebSocketCallback myWebSocketCallback) {
		this.http.webSocket(endpoint, myWebSocketCallback);
//		StandardWebSocketClient client = new StandardWebSocketClient();
//
//		WebSocketStompClient stompClient = new WebSocketStompClient(client);
//		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//
//		StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
//
//			@Override
//			public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
//			    session.subscribe("/topic/messages", this);
//			    //session.send("/app/chat", getSampleMessage());
//			}
//
//			@Override
//			public void handleFrame(StompHeaders headers, Object payload) {
//				// TODO Auto-generated method stub
//				super.handleFrame(headers, payload);
//			}
//
//			@Override
//			public void handleException(StompSession session, StompCommand command, StompHeaders headers,
//					byte[] payload, Throwable exception) {
//				// TODO Auto-generated method stub
//				super.handleException(session, command, headers, payload, exception);
//			}
//
//			@Override
//			public void handleTransportError(StompSession session, Throwable exception) {
//				// TODO Auto-generated method stub
//				super.handleTransportError(session, exception);
//				exception.printStackTrace();
//			}
//		};
//		stompClient.connect(endpoint, sessionHandler);
	}

	public void requestOtherTester(TestPlan2Element e) {
		try {
			this.http.post("requestOtherTester", e);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public PropertyEntity property(Long id) {
		try {
			return http.getObject("PropertyEntity?id=" + id, PropertyEntity.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public EquipmentEntity[] equipments(Long project) {
		try {
			return http.getObject("EquipmentEntityS?parent=" + project, EquipmentEntity[].class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public EquipmentPresentationEntity[] equipmentPresentations(Long project) {
		try {
			return http.getObject("EquipmentPresentationEntityS?parent=" + project, EquipmentPresentationEntity[].class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public PortPresentationEntity[] portPresentation(Long equipment) {
		try {
			return http.getObject("PortPresentationEntityS?parent=" + equipment, PortPresentationEntity[].class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void linkPort(Long id, Long id2) {
		try {
			http.getObject("linkport?port1=" + id + "&port2=" + id2, String.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public EquipmentEntity equipment(Long id) {
		try {
			return http.getObject("EquipmentEntity?id=" + id, EquipmentEntity.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public PortEntity port(Long id) {
		try {
			return http.getObject("PortEntity?id=" + id, PortEntity.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public PortDirectionEntity[] directions() {
		try {
			return http.getObject("PortDirectionEntityS", PortDirectionEntity[].class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void registerPortTestTemplate(String templateName, Long portid) {
		PortTemplate json = new PortTemplate();
		json.name = templateName;
		json.portid = portid;
		
		try {
			this.http.post("PortTestTemplate", json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public <T> T getList(Class<T> class1) {
		try {
			return this.http.getObject(class1.getSimpleName().replace("]", "").replace("[", "") + "S", class1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void applyPortTemplate(Long portid, Long templateid) {
		try {
			PortTemplate portTemplate = new PortTemplate(portid, templateid);
			
			this.http.post("applyPortTemplate", portTemplate);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void applyPortTemplate(List<Long> portids, Long templateid) {
		try {
			List<PortTemplate> list = new ArrayList<>();
			for (Long portid : portids) {
				list.add(new PortTemplate(portid, templateid));
			}
			
			this.http.post("applyPortTemplates", list);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public PortTestEntity[] portTests(Long portid) {
		try {
			return this.http.getObject("PortTestEntityS?parent="+ portid, PortTestEntity[].class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void copyEquipment(Long id) {
		try {
			this.http.getObject("copyEquipment?id=" + id, String.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void copyEquipment(Long id, Integer number) {
		try {
			this.http.getObject("copyEquipment?id=" + id + "&number=" + number, String.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteEquipment(Long id) {
		this.http.delete("EquipmentEntity?id=" + id);
	}
	
	public void deleteEquipments(List<Long> ids) {
		this.http.delete("EquipmentEntityS?id=" + ids.toString().replace("[", "").replace("]", ""));
	}

	public void addNewEquipment(Long projectid) {
		this.http.get("createEquipment?projectid=" + projectid);
	}

	public void createPort(Long equipmentid) {
		this.http.get("createPort?equipmentid=" + equipmentid);
	}

	public void deletePort(Long id) {
		this.http.delete("PortEntity?id=" + id);
	}

	public void deletePorts(List<Long> ids) {
		this.http.delete("PortEntityS?id=" + ids.toString().replace("]", "").replace("[", ""));
	}
	
	public void renameEquipmentName(Long id, String text) {
		try {
			this.http.getObject("renameEquipment?id=" + id + "&name=" + URLEncoder.encode(text, "UTF-8"), String.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public EquipmentEntitySimple[] equipmentsSimple(Long projectid) {
		try {
			return this.http.getObject("EquipmentEntitySimpleS?parent=" + projectid, EquipmentEntitySimple[].class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public PortEntitySimple[] portsByEquipment(Long equipmentid) {
		try {
			return this.http.getObject("PortEntitySimpleS?parent="+equipmentid, PortEntitySimple[].class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void copyPort(Long id) {
		try {
			this.http.getObject("copyPort?id=" + id, String.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void copyPorts(Long id, Integer number) {
		try {
			this.http.getObject("copyPort?id=" + id + "&number=" + number, String.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createTestCase(List<Long> equipments, String candidateName) {
		TestCaseRequest req = new TestCaseRequest();
		req.name = candidateName;
		req.equipments = equipments;
		try {
			this.http.post("createTestScenario", req);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public TestScenarioEntity[] testPlanList() {
		try {
			return this.http.getObject("TestScenarioEntityS",  TestScenarioEntity[].class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void uploadResult(List<TestPlan2Element> testGroup) {
		try {
			this.http.post("test_results", testGroup);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public PortTestTemplateEntity[] getPortTestTemplates() {
		return this.getList(PortTestTemplateEntity[].class);
	}

	public DiagramItemContainers equipmentDiagram(Long projectid) {
		try {
			return this.http.getObject("equipmentDiagram?parent=" + projectid, DiagramItemContainers.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}