package agents;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import agent.pingPong.Ping;
import agent.pingPong.Pong;
import jms.JMSProducer;
import node.StartApp;

@Path("/agent")
public class AgentController {
	
	@EJB
	AgentManager am;
	@EJB
	JMSProducer jmsp;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/startAgent/{type}/{name}")
	public String startAgent(@PathParam("type") String type1,@PathParam("name") String name){
		
	//	int br=Integer.parseInt(type);
		
		int br=Integer.valueOf(type1);
		String type=AgentTypesEnum.values()[br].toString();
		
		System.out.println("pogodio rest iz angulara: "+type+name);
		
		AgentCenter ac=new AgentCenter(StartApp.getCurrentAddress(), StartApp.getPort(), StartApp.getCurrentName());
		AgentType at=new AgentType(type, "ag");
		AID aid=new AID(name, ac, at);
		
		AbstractAgent agent=null;
		if(at.getName().equals(AgentTypesEnum.PING)){
			System.out.println("usao u ping");
			agent=new Ping(aid);
		}else if(at.getName().equals(AgentTypesEnum.PONG)){
			agent=new Pong(aid);
		}else if(at.getName().equals(AgentTypesEnum.MAPREDUCE)){
			
		}else if(at.getName().equals(AgentTypesEnum.CONTRACTNET)){
			
		}
		if(agent==null){
			System.out.println("JEsil null");
		}else{
			System.out.println("nope");
		}		
		am.addRunning(aid, agent);
		return "dd";//agent.getAID().getName();
	}
}
