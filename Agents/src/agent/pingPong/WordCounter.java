package agent.pingPong;

import java.io.File;
import java.util.HashMap;

import agents.AID;
import agents.AbstractAgent;
import agents.AgentCenter;
import agents.AgentType;
import agents.AgentTypesEnum;
import message.ACLMessage;
import message.Performative;
import node.StartApp;

public class WordCounter extends AbstractAgent{

	private HashMap<String, Integer> brojPoFile;
	private int ukupanBroj;
	
	public WordCounter(AID aid) {
		super(aid);
		brojPoFile=new HashMap<String,Integer>();
		ukupanBroj=0;
	}

	@Override
	protected void onMessage(ACLMessage message) {
		
		if(message.getPerformative().toString().equals(Performative.REQUEST.toString())){
		
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
				
				ACLMessage poruka=new ACLMessage();
				poruka.setContent(file2[i].getAbsolutePath());
				poruka.setSender(message.getReceiver());
				poruka.setReceivers(aid);
				poruka.setPerformative(Performative.REQUEST);
				
				slave.handleMessage(poruka);
			}
		}
	}else if(message.getPerformative().toString().equals(Performative.INFORM.toString())){
		System.out.println("Robovi vracaju rezultate");
		int broj=Integer.parseInt(message.getContent());
		ukupanBroj+=broj;
		System.out.println("WordCounterSlave vratio: "+broj+" ukupno file ima: "+ukupanBroj);
	}
	}
}
