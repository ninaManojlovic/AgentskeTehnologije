package agents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.json.JSONException;

import agent.pingPong.ContractNet;
import agent.pingPong.Ping;
import agent.pingPong.Pong;
import agent.pingPong.WordCounter;
import jms.JMSProducer2;
import message.ACLMessage;
import message.Performative;
import node.Nodes;
import node.StartApp;


@LocalBean
@Path("/agent")
@Stateless
public class AgentController {
	
	@EJB
	AgentManager am;

@EJB
Nodes nodes;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/startAgent/{type}/{name}/{port}")
	public String startAgent(@PathParam("type") String type,@PathParam("name") String name, @PathParam("port") String port1){
		
	//	int br=Integer.parseInt(type);
		
		//int br=Integer.valueOf(type1);
		//String type=AgentTypesEnum.values()[br].toString();
		
		System.out.println("pogodio rest iz angulara na portu: "+StartApp.getPort()+" dobio: "+type+name+" lista ima: "+am.getRunning().values().size());
		
		AgentCenter ac=new AgentCenter(StartApp.getCurrentAddress(), port1, StartApp.getCurrentName());
		
		AgentType at=new AgentType(type, "ag");
		AID aid=new AID(name, ac, at);
		
		AbstractAgent agent=null;
		String povratniTip="";
		
		System.out.println("!!!!!!!!!!!!: "+at.getName());
		if(at.getName().equals(AgentTypesEnum.PING.toString())){
			System.out.println("usao u ping");
			agent=new Ping(aid);
			
		}else if(at.getName().equals(AgentTypesEnum.PONG.toString())){
			agent=new Pong(aid);
			povratniTip=agent.getAid().getType().getName();
		}else if(at.getName().equals(AgentTypesEnum.MAPREDUCE.toString())){
			agent=new WordCounter(aid);
		}else if(at.getName().equals(AgentTypesEnum.CONTRACTNET.toString())){
			agent=new ContractNet(aid,am);
		}
		povratniTip=agent.getAid().getType().getName();
		//PROVERA DA NE MOGU DA SE DODAJU 2 AGENTA SA ISTIM IMENOM
		  boolean ok=true;
		  for(AbstractAgent aa:am.getRunning().values()){
			  System.out.println(aa.getAid().getName()+" a name je: "+name);
		   if(aa.getAid().getName().equals(name)){
			System.out.println("OK je postavljen na false");
		    ok=false;
		    break;
		   }
		  }
		   if(ok){
		   am.addRunning(aid, agent);
		  // AgentManager.running.put(aid, agent);
		   System.out.println("dodao novog agenta u listu running: "+aid.getName());
		   for(AgentCenter a:nodes.getNodes()) {
			   ResteasyClient client = new ResteasyClientBuilder().build();
			   ResteasyWebTarget target = client.target(
			     "http://localhost:" + a.getPort() + "/Agents/rest/agent/startAgent/" + type + "/" + name+"/"+port1);
			  Response response = target.request().get();
			   String ret = response.readEntity(String.class);
		   }
		  
		   return povratniTip;
		   }else{
		    return null;
		   }
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/startAgentRest/{type}/{name}/{port}")
	public AbstractAgent startAgentRest(@PathParam("type") String type,@PathParam("name") String name, @PathParam("port") String port1){
		
		
		System.out.println("startAgent rest: "+StartApp.getPort()+" dobio: "+type+name+" lista ima: "+am.getRunning().values().size());
		
		AgentCenter ac=new AgentCenter(StartApp.getCurrentAddress(), port1, StartApp.getCurrentName());
		
		AgentType at=new AgentType(type, "ag");
		AID aid=new AID(name, ac, at);
		
		AbstractAgent agent=null;
		String povratniTip="";
		
		System.out.println("!!!!!!!!!!!!: "+at.getName());
		if(at.getName().equals(AgentTypesEnum.PING.toString())){
			System.out.println("usao u ping");
			agent=new Ping(aid);
			
		}else if(at.getName().equals(AgentTypesEnum.PONG.toString())){
			agent=new Pong(aid);
			povratniTip=agent.getAid().getType().getName();
		}else if(at.getName().equals(AgentTypesEnum.MAPREDUCE.toString())){
			agent=new WordCounter(aid);
		}else if(at.getName().equals(AgentTypesEnum.CONTRACTNET.toString())){
			agent=new ContractNet(aid,am);
		}
		povratniTip=agent.getAid().getType().getName();
		//PROVERA DA NE MOGU DA SE DODAJU 2 AGENTA SA ISTIM IMENOM
		  boolean ok=true;
		  for(AbstractAgent aa:am.getRunning().values()){
			  System.out.println(aa.getAid().getName()+" a name je: "+name);
		   if(aa.getAid().getName().equals(name)){
			System.out.println("OK je postavljen na false");
		    ok=false;
		    break;
		   }
		  }
		   if(ok){
		   am.addRunning(aid, agent);
		  // AgentManager.running.put(aid, agent);
		   System.out.println("dodao novog agenta u listu running: "+aid.getName());
		   for(AgentCenter a:nodes.getNodes()) {
			   ResteasyClient client = new ResteasyClientBuilder().build();
			   ResteasyWebTarget target = client.target(
			     "http://localhost:" + a.getPort() + "/Agents/rest/agent/startAgent/" + type + "/" + name+"/"+port1);
			  Response response = target.request().get();
			   String ret = response.readEntity(String.class);
		   }
		  
		   return agent;
		   }else{
		    return null;
		   }
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/runningAgents")
	public List<AbstractAgent> getRunning(){
		
		List<AbstractAgent> lista=new ArrayList<AbstractAgent>();
		
		for(AbstractAgent a:am.getRunning().values()){
			lista.add(a);
		}
		return lista;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/sendMessage/{tipPoruke}/{sender}/{receiver}")
	public void sendMessage(@PathParam("tipPoruke") String tipPoruke,@PathParam("sender") String sender,@PathParam("receiver") String receiver, String content) throws JsonParseException, JsonMappingException, IOException, JSONException{
		
		//ObjectMapper mapper=new ObjectMapper();
	
		int perf=Integer.parseInt(tipPoruke);
		String perfomativa=Performative.values()[perf].toString();
		String message=perfomativa+"/"+sender+"/"+receiver+"/"+content;
	
		System.out.println("DA LI JE PRIMIOO " +message);	
		
		AbstractAgent posiljalac=null;
		AbstractAgent primalac=null;
		
		for(AID aid:am.getRunning().keySet()){
			if(aid.getName().equals(sender)){
				posiljalac=am.getRunning().get(aid);
				break;
			}
		}
		
		for(AID aid:am.getRunning().keySet()){
			if(aid.getName().equals(receiver)){
				primalac=am.getRunning().get(aid);
				break;
			}
		}
		
		if(posiljalac!=null && primalac!=null){
			System.out.println("nadjeni su:"+posiljalac.getAid().getName()+" primalac: "+primalac.getAid().getName());
		}
		
		 ResteasyClient client = new ResteasyClientBuilder().build();
		   ResteasyWebTarget target = client.target(
		     "http://localhost:" + primalac.getAid().getHost().getPort() + "/Agents/rest/agent/proslediPoruku/" + tipPoruke + "/" + sender+"/"+receiver+"/"+content);
		   Response response = target.request().get();
		   String ret = response.readEntity(String.class);
		
		/*try {
			
			
			
			
			System.out.println(obj);
	
			
			System.out.println("prosaoprva tri");
			
			
			
			System.out.println("sender ime: "+poruka.getSender().getName());
			System.out.println("primio ime: "+poruka.getReceiver().getName());
			System.out.println("kontent: "+poruka.getContent());
			
			System.out.println(sender.getName()+sender.getHost().getPort()+sender.getType().getName());
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		/*
			ACLMessage poruka;
			try {
				JSONObject obj=new JSONObject(message);
				AID sender=mapper.readValue(obj.get("sender").toString(), AID.class);
				//AID reciever=mapper.readValue(obj.get("receivers").toString(), AID.class);
				//String content=mapper.readValue(obj.get("content").toString(), String.class);
				
				//poruka = mapper.readValue(message, ACLMessage.class);
				poruka=new ACLMessage();
				
				poruka.setSender(sender);
				//poruka.setReceivers(reciever);
				//poruka.setContent(content);
				
				System.out.println("sender ime: "+poruka.getSender().getName());
				System.out.println("primio ime: "+poruka.getReceiver().getName());
				System.out.println("kontent: "+poruka.getContent());
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	*/
		//System.out.println("pogodio rest za send poruku: "+poruka);
		//System.out.println(poruka.getPerformative());
		
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/proslediPoruku/{pref}/{sender}/{rec}/{content}")
	public void proslediPoruku(@PathParam("pref") String pref,@PathParam("sender") String sender,@PathParam("rec") String rec,@PathParam("content") String content){
	
		System.out.println("usao u rest end point prosledi poruku, content: "+content);
		
		AbstractAgent posiljalac=null;
		AbstractAgent primalac=null;
		int br=Integer.parseInt(pref);
		Performative per=Performative.values()[br];
		
		for(AID aid:am.getRunning().keySet()){
			if(aid.getName().equals(sender)){
				posiljalac=am.getRunning().get(aid);
				break;
			}
		}
		
		for(AID aid:am.getRunning().keySet()){
			if(aid.getName().equals(rec)){
				primalac=am.getRunning().get(aid);
				break;
			}
		}
		
		ACLMessage acl=new ACLMessage();//(per, posiljalac.getAid(), primalac.getAid(), replyTo, content, contentObject, userArgs, language, encoding, ontology, protocol, conversationId, replyWith, inReplyTo, replyBy)
		acl.setPerformative(per);
		acl.setSender(posiljalac.getAid());
		acl.setReceivers(primalac.getAid());
		acl.setContent(content);
		
		JMSProducer2.sendJMS(acl);
	}
		
}
