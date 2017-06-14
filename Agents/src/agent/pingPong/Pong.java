package agent.pingPong;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import agents.AID;
import agents.AbstractAgent;
import agents.Agent;
import agents.AgentManager;
import jms.JMSProducer2;
import message.ACLMessage;
import message.Performative;

public class Pong extends AbstractAgent implements Serializable{


	public Pong(AID aid) {
		super(aid);
	}
	


	@Override
	protected void onMessage(ACLMessage message) {
		
		if(message.getPerformative().equals(Performative.REQUEST)){
		
		System.out.println("I'm PONG, and I receve meesage from PING");
		
		
		ACLMessage odgovor=new ACLMessage();
		odgovor.setSender(message.getReceiver());
		odgovor.setReceivers(message.getSender());
		odgovor.setPerformative(Performative.AGREE);
		odgovor.setContent("PONG recevied message, and send message back");
		String pref=String.valueOf(Performative.AGREE.ordinal());
		
		if(message.getReceiver().getHost().getPort().equals(message.getSender().getHost().getPort())){
		
		System.out.println("salje pong sad pingu: "+odgovor.getReceiver().getName());
		JMSProducer2.sendJMS(odgovor);
		}else{
			 ResteasyClient client = new ResteasyClientBuilder().build();
			   ResteasyWebTarget target = client.target(
			     "http://localhost:" + odgovor.getReceiver().getHost().getPort() + "/Agents/rest/agent/proslediPoruku/" +pref + "/" + odgovor.getSender().getName()+"/"+odgovor.getReceiver().getName()+"/"+odgovor.getContent());
			   Response response = target.request().get();
			   String ret = response.readEntity(String.class);	
			
		}
		}
	}



}
