package node;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import agents.AgentCenter;

@LocalBean
@Stateless
@Path("/node")
public class NodeController {

	@EJB
	Nodes nodes;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/registerNode/{address}/{port}/{alias}")
	public List<AgentCenter> register(@PathParam("address") String address,@PathParam("port") String port,@PathParam("alias") String alias){
		
		System.out.println("registracija slave cvora: "+port+" na master cvoru");
		if (!(nodes.getCurrent().getAddress().equals(address)) || !(nodes.getCurrent().getAlias().equals(alias))
			    || !(nodes.getCurrent().getPort().equals(port))) {
			   nodes.getNodes().add(new AgentCenter(address, port, alias));
			   //UPDATE OSTALIH CVOROVA DA JE DODAT NOVI CVOR
			   for (AgentCenter h : nodes.getNodes()) {

				   System.out.println("master gadja update na: "+h.getPort());
			    ResteasyClient client = new ResteasyClientBuilder().build();
			    ResteasyWebTarget target = client
		//	      .target("http://" + h.getAddress()+":"+h.getPort() + "/Agents/rest/node/updateNodes");
			    		 .target("http://localhost:"+h.getPort() + "/Agents/rest/node/updateNodes");
			    Response response = target.request().post(Entity.entity(nodes.getNodes(), "application/json"));

			    String ret = response.readEntity(String.class);

			   }
			  }
			  return(List<AgentCenter>) nodes.getNodes();
		
	}
	
	@POST
	 @Path("/updateNodes")
	 @Consumes(MediaType.APPLICATION_JSON)
	 public void updateNodes(List<AgentCenter> newNodes) {
	  ArrayList<AgentCenter> newList = (ArrayList<AgentCenter>) newNodes;
	  nodes.setNodes(newList);
	 
	  System.out.println("izvrsen update nodova, sada ima: " + nodes.getNodes().size() + " cvorova");

	 }
	
	 @PUT
	 @Path("/unregisterNode")
	 @Consumes(MediaType.APPLICATION_JSON)
	 public void unregister(AgentCenter host) {
		 System.out.println("usao u unregister na masteru");
	  for (int i = 0; i < nodes.getNodes().size(); i++) {
	   AgentCenter tren = nodes.getNodes().get(i);
	   if (tren.getAddress().equals(host.getAddress()) && tren.getAlias().equals(host.getAlias())
	     && tren.getPort().equals(host.getPort())) {
		   System.out.println("izbrisao:"+nodes.getNodes().get(i).getAddress()+" "+nodes.getNodes().get(i).getPort());
	    nodes.getNodes().remove(i);
	    
	    break;
	   }
	  }

	  for (AgentCenter h : nodes.getNodes()) {
		 // System.out.println("salje update na: "+h.getAddress()+" "+h.getPort());
	   ResteasyClient client = new ResteasyClientBuilder().build();
	  
	   ResteasyWebTarget target = client
			      .target("http://" + h.getAddress()+":"+h.getPort() + "/Agents/rest/node/updateNodes");

	   Response response = target.request().post(Entity.entity(nodes.getNodes(), "application/json"));
	   


	   String ret = response.readEntity(String.class);

	  }
	 }
}
