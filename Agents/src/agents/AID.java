package agents;

import java.io.Serializable;

public class AID implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -209185513513460516L;
	
	private String name;//IME AGENTA
	private AgentCenter host;//CVOR NA KOM JE AGENT
	private AgentType type;//TIP
	
	public AID(String aid){
		
	}
	
	public AID(){
		
	}

	public AID(String name, AgentCenter host, AgentType type) {
		super();
		this.name = name;
		this.host = host;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AgentCenter getHost() {
		return host;
	}

	public void setHost(AgentCenter host) {
		this.host = host;
	}

	public AgentType getType() {
		return type;
	}

	public void setType(AgentType type) {
		this.type = type;
	}
	
	

}
