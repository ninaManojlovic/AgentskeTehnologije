package node;

import java.io.Serializable;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.json.JSONArray;
import org.json.JSONException;

import agents.AgentCenter;

@Startup
@Singleton
public class Nodes implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2666244875191947563L;
	
	private ArrayList<AgentCenter> nodes;
	private AgentCenter current;
	private AgentCenter master;
	
	public Nodes(){
		nodes= new ArrayList<AgentCenter>();
	}
	
	@PostConstruct
	public void init(){
		current=new AgentCenter(StartApp.getCurrentAddress(), StartApp.getPort(),StartApp.getMasterAlias());
		master= new AgentCenter(StartApp.getMasterAddress(), StartApp.getMasterPort(), StartApp.getMasterAlias());
	
		nodes.add(master);
		if (!master.getPort().equals(current.getPort())) {
			   ResteasyClient client = new ResteasyClientBuilder().build();
			   System.out.println("Cvor je slave na portu: "+current.getPort()+", gadja mastera za registraciju na portu: "+master.getPort());
			   ResteasyWebTarget target = client
			    // .target("http://" + master.getAddress() + ":" + master.getPort() + "/Agents/rest/node/registerNode/"+current.getAddress()+"/"+current.getPort()+"/"+current.getAlias());
					   .target("http://localhost:"+ master.getPort() + "/Agents/rest/node/registerNode/"+current.getAddress()+"/"+current.getPort()+"/"+current.getAlias());

			   Response response = target.request().get();
			   String ret = response.readEntity(String.class);

			   ArrayList<AgentCenter> lista = new ArrayList<AgentCenter>();

			   JSONArray jsonA;
			   try {
			    jsonA = new JSONArray(ret);
			    
			//    System.out.println("vratio cvorove:");
			    
			    for (int i = 0; i < jsonA.length(); i++) {
			     AgentCenter newOne = new AgentCenter();
			     newOne.setAddress(jsonA.getJSONObject(i).getString("address"));
			     newOne.setAlias(jsonA.getJSONObject(i).getString("alias"));
			     newOne.setPort(jsonA.getJSONObject(i).getString("port"));
			    // System.out.println(newOne.getAddress()+" "+newOne.getPort());
			     lista.add(newOne);
			    }
			   } catch (JSONException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			   }

			   setNodes(lista);
//System.out.println(StartApp.getPort()+" cvor ima listu od: "+nodes.size());
			  }
			 }
	
	@PreDestroy
	 public void unregister() {

	  if (!master.getPort().equals(current.getPort())) {
	   ResteasyClient client = new ResteasyClientBuilder().build();
	   ResteasyWebTarget target = client
	     .target("http://"+master.getAddress()+":"+master.getPort() +  "/Agents/rest/node/unregisterNode");
			  // .target("http://localhost:"+master.getPort() +  "/Agents/rest/node/unregisterNode");
	   Response response = target.request().put(Entity.entity(current, "application/json"));
	  }
	 }
	

	public ArrayList<AgentCenter> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<AgentCenter> nodes) {
		this.nodes = nodes;
	}

	public AgentCenter getCurrent() {
		return current;
	}

	public void setCurrent(AgentCenter current) {
		this.current = current;
	}

	public AgentCenter getMaster() {
		return master;
	}

	public void setMaster(AgentCenter master) {
		this.master = master;
	}
	
	

}
