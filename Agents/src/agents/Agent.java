package agents;

import message.ACLMessage;

public interface Agent {

	
	public void init(AID aid);
	public void handleMessage(ACLMessage message);
	public AID getAID();
	
}
