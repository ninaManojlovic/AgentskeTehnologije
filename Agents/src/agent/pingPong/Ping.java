package agent.pingPong;

import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.ejb.Stateless;

import agents.AID;
import agents.AbstractAgent;
import agents.Agent;
import message.ACLMessage;

//@Stateless
//@Remote(Agent.class)
public class Ping extends AbstractAgent {

	public Ping(AID aid) {
		super(aid);
	}

	@Override
	protected void onMessage(ACLMessage message) {
		// TODO Auto-generated method stub
		
	}
	

}
