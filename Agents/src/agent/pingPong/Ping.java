package agent.pingPong;

import java.io.IOException;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.sun.xml.internal.ws.api.server.WSEndpoint;

import agents.AID;
import agents.AbstractAgent;
import agents.Agent;
import agents.AgentCenter;
import agents.AgentType;
import agents.AgentTypesEnum;
import jms.JMSProducer2;
import message.ACLMessage;
import message.Performative;
import node.StartApp;
import webSocket.WsEndpoint;

//@Stateless
//@Remote(Agent.class)
public class Ping extends AbstractAgent {

	private static AID posiljalac;
	
	public Ping(AID aid) {
		super(aid);
	}

	@Override
	protected void onMessage(ACLMessage message) {
		
		
	if(message.getPerformative().equals(Performative.AGREE)){
		System.out.println("PING" +message.getReceiver().getName()+" received message from PONG");
		
		ACLMessage odgovor=new ACLMessage();
		odgovor.setReceivers(posiljalac);
		odgovor.setSender(message.getReceiver());
		odgovor.setPerformative(Performative.INFORM);
		String pref=String.valueOf(Performative.INFORM.ordinal());
		
		String con= "Inform|PONG: "+message.getSender().getName()+" odgovorio PINGU: "+message.getReceiver().getName();
		odgovor.setContent(con);
		System.out.println("Posiljalac"+posiljalac.getName());
		
		if(odgovor.getSender().getHost().getPort().equals(odgovor.getReceiver().getHost().getPort())){
			JMSProducer2.sendJMS(odgovor);
		}else{
			System.out.println("is agree mora da gadja rest: "
				+	"http://localhost:" + odgovor.getReceiver().getHost().getPort() + "/Agents/rest/agent/proslediPoruku/" +pref + "/" + odgovor.getSender().getName()+"/"+odgovor.getReceiver().getName()+"/"+odgovor.getContent());
			 ResteasyClient client = new ResteasyClientBuilder().build();
			   ResteasyWebTarget target = client.target(
			     "http://localhost:" + odgovor.getReceiver().getHost().getPort() + "/Agents/rest/agent/proslediPoruku/" +pref + "/" + odgovor.getSender().getName()+"/"+odgovor.getReceiver().getName()+"/"+odgovor.getContent());
			   Response response = target.request().get();
			   String ret = response.readEntity(String.class);	
		}
		
	}else if(message.getPerformative().equals(Performative.REQUEST)){
		posiljalac=message.getSender();
		System.out.println("Posiljalac je: "+message.getSender().getName());
		System.out.println("I'm PING and I'm sending mesage to PONG");
		AgentCenter ac=new AgentCenter(StartApp.getCurrentAddress(), StartApp.getPort(), StartApp.getCurrentName());
		AgentType at=new AgentType(AgentTypesEnum.PONG.toString(), "ag");
		AID pong= new AID("pong",ac,at);
		
		Pong pongAgent=new Pong(pong);
		
		ACLMessage poruka=new ACLMessage();
		poruka.setPerformative(Performative.REQUEST);
		poruka.setSender(message.getReceiver());
		poruka.setReceivers(pong);
		poruka.setContent(message.getContent());
		
		pongAgent.handleMessage(poruka);
	}else if(message.getPerformative().equals(Performative.INFORM)){
		System.out.println("uso u inform: "+message.getContent());
		try {
			WsEndpoint.posaljiOdgovor(message.getContent());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	}

}
