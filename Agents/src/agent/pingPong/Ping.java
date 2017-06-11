package agent.pingPong;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.ejb.Stateless;

import agents.AID;
import agents.AbstractAgent;
import agents.Agent;
import agents.AgentCenter;
import agents.AgentType;
import agents.AgentTypesEnum;
import message.ACLMessage;
import message.Performative;
import node.StartApp;

//@Stateless
//@Remote(Agent.class)
public class Ping extends AbstractAgent {

	public Ping(AID aid) {
		super(aid);
	}

	@Override
	protected void onMessage(ACLMessage message) {
		
		
	if(message.getPerformative().equals(Performative.AGREE)){
		System.out.println("PING received message from PONG");
		
	}else if(message.getPerformative().equals(Performative.REQUEST)){
		System.out.println("I'm PING and I'm sending mesage to PONG");
		AgentCenter ac=new AgentCenter(StartApp.getCurrentAddress(), StartApp.getPort(), StartApp.getCurrentName());
		AgentType at=new AgentType(AgentTypesEnum.PONG.toString(), "ag");
		AID pong= new AID("pong",ac,at);
		
		Pong pongAgent=new Pong(pong);
		
		ACLMessage poruka=new ACLMessage();
		poruka.setPerformative(Performative.REQUEST);
		poruka.setSender(message.getSender());
		poruka.setReceivers(pong);
		poruka.setContent(message.getContent());
		
		pongAgent.handleMessage(poruka);
	}
	}
	

}
