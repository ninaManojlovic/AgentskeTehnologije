package webSocket;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.json.JSONArray;
import org.json.JSONException;
import agents.AID;
import agents.AbstractAgent;
import agents.AgentController;
import agents.AgentManager;
import node.Nodes;

@Stateful
@ServerEndpoint("/websocket")
public class WsEndpoint {

	Logger log = Logger.getLogger("Websockets endpoint");
	static List<Session> sessions = new ArrayList<Session>();

	@EJB
	Nodes node;

	@EJB
	AgentManager am;

	@OnOpen
	public void open(Session session) {
		if (!sessions.contains(session)) {
			sessions.add(session);
			System.out.println("ON OPEEEEEEEEEEEEEEEEEEEEEEEN");
			log.info("Dodao sesiju: " + session.getId() + " u endpoint-u: " + this.hashCode() + ", ukupno sesija: "
					+ sessions.size());
		}
	}

	@OnClose
	public void close(Session session) {
		sessions.remove(session);
	}

	@OnError
	public void onError(Throwable error) {
		System.out.println("USAO U ONEREOR " + error.getMessage());
	}

	@OnMessage
	public void handleMessage(String message, Session session) {
		
		String poruka[] = message.split(";");
		String tip = poruka[0];

		if (tip.equals("Create")) {

			String imeAgenta = poruka[1];
			String tipAgenta = poruka[2];
			String port = poruka[4];

			String porukaZaJS = "Create|Agent " + imeAgenta + " kreiran.";

			System.out.println("WebSocket end point onMessage metoda za kreiranje agenta");
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target("http://localhost:" + port + "/Agents/rest/agent/startAgent/"
					+ tipAgenta + "/" + imeAgenta + "/" + port);
			Response response = target.request().get();
			String ret = response.readEntity(String.class);

			// AKO JE REST BIO USPESAN, POSALJI PORUKU NA KLIJENTA
			if (ret != null) {

				try {
				
					for (Session s : sessions) {
					
						s.getBasicRemote().sendText(porukaZaJS);
					}

					// OVO SADA DA APDEJTUJE ONE KOMBO BOKSOVE ZA PRIMA I SALJE
					ResteasyClient client1 = new ResteasyClientBuilder().build();
					ResteasyWebTarget target1 = client
							.target("http://localhost:" + port + "/Agents/rest/agent/runningAgents/");
					Response response1 = target1.request().get();
					String ret1 = response1.readEntity(String.class);
					

					ArrayList<String> lista = new ArrayList<String>();

					JSONArray jsonA;
					try {
						jsonA = new JSONArray(ret1);

						for (int i = 0; i < jsonA.length(); i++) {
							String ime = jsonA.getJSONObject(i).getJSONObject("aid").getString("name");

							
							lista.add(ime);
						}

						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					String porukaRefresh = "Refresh|" + lista;

					try {
						
						for (Session s : sessions) {

							s.getBasicRemote().sendText(porukaRefresh);
							

						}

					} catch (IOException e) {
						try {
							session.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				} catch (IOException e) {
					try {
						session.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		} else if (tip.equals("Message")) {

			System.out.println("WebSocket onMessage metoda za slanje poruka");

			String preformative = poruka[1];
			String sender = poruka[2];
			String receiver = poruka[3];
			String content = poruka[4];

			AbstractAgent posiljalac = null;
			AbstractAgent primalac = null;

	
			for (AID aid : am.getRunning().keySet()) {
				if (aid.getName().equals(sender)) {
					posiljalac = am.getRunning().get(aid);
					break;
				}
			}

			for (AID aid : am.getRunning().keySet()) {
			
				if (aid.getName().equals(receiver)) {
					primalac = am.getRunning().get(aid);
					
					break;
				}
			}

			

			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target(
					"http://localhost:" + primalac.getAid().getHost().getPort() + "/Agents/rest/agent/proslediPoruku/"
							+ preformative + "/" + sender + "/" + receiver + "/" + content);
			Response response = target.request().get();
			String ret = response.readEntity(String.class);
		}

	}

	public void addSession(Session session) {
		sessions.add(session);
	}

	public static void posaljiOdgovor(String odgovor) throws IOException {
		for (Session s : sessions) {
			s.getBasicRemote().sendText(odgovor);
		}
	}

}
