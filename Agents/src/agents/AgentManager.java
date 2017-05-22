package agents;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.Singleton;

@Singleton
public class AgentManager {
	
	private HashMap<AID, AbstractAgent> existing;
	private HashMap<AID, AbstractAgent> running;
	
	
	public AgentManager(){
		
		existing=new HashMap<AID, AbstractAgent>();
		running=new HashMap<AID, AbstractAgent>();
	}


	public HashMap<AID, AbstractAgent> getExisting() {
		return existing;
	}


	public void setExisting(HashMap<AID, AbstractAgent> existing) {
		this.existing = existing;
	}


	public HashMap<AID, AbstractAgent> getRunning() {
		return running;
	}


	public void setRunning(HashMap<AID, AbstractAgent> running) {
		this.running = running;
	}
	
	public void addRunning(AID aid, AbstractAgent agent){
		running.put(aid, agent);
	}

}
