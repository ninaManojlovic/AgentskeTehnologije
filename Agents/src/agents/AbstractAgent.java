package agents;

import javax.ejb.EJB;

import message.ACLMessage;

public abstract class AbstractAgent {//implements Agent{
	
	protected AID aid;
	
	public AbstractAgent(){
		
	}
	
	public AbstractAgent(AID aid){
		this.aid=aid;
	}
	
	/*@Override
	public void init(AID aid){
		this.aid=aid;
	}
	
	@Override*/
	public void handleMessage(ACLMessage message){
		onMessage(message);
	}
	
	protected abstract void onMessage(ACLMessage message);

	public AID getAid() {
		return aid;
	}

	public void setAid(AID aid) {
		this.aid = aid;
	}


	
	
}
