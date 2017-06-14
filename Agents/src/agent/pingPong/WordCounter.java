package agent.pingPong;

import java.io.File;
import java.util.HashMap;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import agents.AID;
import agents.AbstractAgent;
import agents.AgentCenter;
import agents.AgentType;
import agents.AgentTypesEnum;
import jms.JMSProducer2;
import message.ACLMessage;
import message.Performative;
import node.StartApp;

public class WordCounter extends AbstractAgent{

	private HashMap<String, Integer> brojPoFile;
	private int ukupanBroj;
	private int brojRobova;
	private AID posiljalac;
	
	public WordCounter(AID aid) {
		super(aid);
		brojPoFile=new HashMap<String,Integer>();
		ukupanBroj=0;
		brojRobova=0;
	}

	@Override
	protected void onMessage(ACLMessage message) {
		
		if(message.getPerformative().toString().equals(Performative.REQUEST.toString())){
		
			posiljalac=message.getSender();
		String putanja=message.getContent();
		
		System.out.println("word counter metoda");
		
		File file=new File(putanja);
		File[] file2=file.listFiles();
		
		for(int i=0; i<file2.length; i++){
			if(file2[i].isFile()){
				System.out.println(i+" Tekstualni file");
				
				AgentType at=new AgentType(AgentTypesEnum.MAPREDUCE.toString(), "ag");
				AgentCenter ac=new AgentCenter(StartApp.getCurrentAddress(), StartApp.getPort(), StartApp.getCurrentName());
				AID aid= new AID("slave"+i, ac, at);
				WordCounterSlave slave=new WordCounterSlave(aid);
				brojRobova++;
				
				ACLMessage poruka=new ACLMessage();
				poruka.setContent(file2[i].getAbsolutePath());
				poruka.setSender(message.getReceiver());
				poruka.setReceivers(aid);
				poruka.setPerformative(Performative.REQUEST);
				
				slave.handleMessage(poruka);
			}
		}
	}else if(message.getPerformative().toString().equals(Performative.AGREE.toString())){
		brojRobova--;
		
		System.out.println("Robovi vracaju rezultate, broj robova: "+brojRobova);
		int broj=Integer.parseInt(message.getContent());
		ukupanBroj+=broj;
		System.out.println("WordCounterSlave vratio: "+broj+" ukupno file ima: "+ukupanBroj);
		
		if(brojRobova==0){
			
			ACLMessage odgovor=new ACLMessage();
			odgovor.setPerformative(Performative.INFORM);
			String perfomat=String.valueOf(Performative.INFORM.ordinal());
			odgovor.setSender(message.getReceiver());
			odgovor.setReceivers(posiljalac);
			String con="Inform|WordCounter:u fajlu ukupno ima: "+ukupanBroj+" reci";
			odgovor.setContent(con);
			
			if(odgovor.getSender().getHost().getPort().equals(odgovor.getReceiver().getHost().getPort()))
			{
				JMSProducer2.sendJMS(odgovor);
			}else{
				ResteasyClient client = new ResteasyClientBuilder().build();
				   ResteasyWebTarget target = client.target(
				     "http://localhost:" + odgovor.getReceiver().getHost().getPort() + "/Agents/rest/agent/proslediPoruku/" +perfomat + "/" + odgovor.getSender().getName()+"/"+odgovor.getReceiver().getName()+"/"+odgovor.getContent());
				   Response response = target.request().get();
				   String ret = response.readEntity(String.class);	
				
			}			
		}
	}
	}
}
