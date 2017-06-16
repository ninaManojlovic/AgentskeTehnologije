package agent.pingPong;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import agents.AID;
import agents.AbstractAgent;
import agents.AgentManager;
import jms.JMSProducer2;
import message.ACLMessage;
import message.Performative;
import webSocket.WsEndpoint;

public class ContractNet extends AbstractAgent{


	private AgentManager agm;
	private static HashMap<AID, Integer>mapa;
	private static int brojac;
	private static ArrayList<AbstractAgent> ponudjaci;
	private static AID posiljalac;
	private static AID inicijator;
	private static AID najboljiA;
	
	public ContractNet(AID aid,AgentManager am) {
		super(aid);
		agm=am;
		mapa=new HashMap<AID, Integer>();
		brojac=0;
		ponudjaci=new ArrayList<AbstractAgent>();
	}
	
	@Override
	protected void onMessage(ACLMessage message) {
		if(message.getPerformative().toString().equals(Performative.REQUEST.toString())){
			posiljalac=message.getSender();
			inicijator=message.getReceiver();
			
			Collection<AbstractAgent> listaAgenata= agm.getRunning().values();
			AID sender=message.getReceiver();
			
			
			for(AbstractAgent a:listaAgenata){
				if(a.getAid().getType().getName().equals("CONTRACTNET")){
					if(!a.getAid().getName().equals(message.getReceiver().getName())){
						ponudjaci.add(a);
						brojac++;
						System.out.println("u listu participants dodao: "+a.getAid().getName()+"brojac je: "+brojac);
					}
				}
			}
			
			for(AbstractAgent a:ponudjaci){
				if(sender.getHost().getPort().equals(a.getAid().getHost().getPort())){
					ACLMessage poruka=new ACLMessage();
					poruka.setContent(message.getContent());
					poruka.setSender(sender);
					poruka.setReceivers(a.getAid());
					poruka.setPerformative(Performative.CALL_FOR_PROPOSAL);
					
					JMSProducer2.sendJMS(poruka);
					
				}else{
					System.out.println(sender.getName()+" Gadja rest na port: "+a.getAid().getHost().getPort()+" agentu: "+a.getAid().getName());
					
					 ResteasyClient client = new ResteasyClientBuilder().build();
					   ResteasyWebTarget target = client.target(
					     "http://localhost:" + a.getAid().getHost().getPort() + "/Agents/rest/agent/proslediPoruku/" +String.valueOf(Performative.CALL_FOR_PROPOSAL.ordinal()) + "/" + sender.getName()+"/"+a.getAid().getName()+"/"+message.getContent());
					   Response response = target.request().get();
					   String ret = response.readEntity(String.class);
					
				}
			}
		}else if(message.getPerformative().toString().equals(Performative.CALL_FOR_PROPOSAL.toString())){
			System.out.println(message.getReceiver().getName()+" dobio CALL_FOR_PROPOSAL od: "+message.getSender().getName());
			
			Random r=new Random();
			int broj=Integer.parseInt(message.getContent());
			int randomBroj=r.nextInt(broj);
			
			ACLMessage odgovor=new ACLMessage();
			odgovor.setReceivers(message.getSender());
			odgovor.setSender(message.getReceiver());
			odgovor.setContent(String.valueOf(randomBroj));
			
			String pref="";
			
			if(r.nextBoolean()){
				odgovor.setPerformative(Performative.PROPOSE);
				pref=String.valueOf(Performative.PROPOSE.ordinal());
			}else{
				odgovor.setPerformative(Performative.REFUSE);
				pref=String.valueOf(Performative.REFUSE.ordinal());
			}
			
			//AKO SU NA ISTOM SALJE SE JMS-OM AKO NISU GADJA REST	
			if(odgovor.getReceiver().getHost().getPort().equals(odgovor.getSender().getHost().getPort())){
				JMSProducer2.sendJMS(odgovor);
				
			}else{	
				
				System.out.println(odgovor.getSender().getName()+" gadja rest na "+odgovor.getReceiver().getHost().getPort()+"/"+pref + "/" + odgovor.getSender().getName()+"/"+odgovor.getReceiver().getName()+"/"+odgovor.getContent());
				
				 ResteasyClient client = new ResteasyClientBuilder().build();
				   ResteasyWebTarget target = client.target(
				     "http://localhost:" + odgovor.getReceiver().getHost().getPort() + "/Agents/rest/agent/proslediPoruku/" +pref + "/" + odgovor.getSender().getName()+"/"+odgovor.getReceiver().getName()+"/"+odgovor.getContent());
				   Response response = target.request().get();
				   String ret = response.readEntity(String.class);	
			}
			
			
		}else if(message.getPerformative().toString().equals(Performative.REFUSE.toString())){
			
			brojac--;
			System.out.println("brojac: "+brojac);
			System.out.println(message.getReceiver().getName()+" dobio REFUSE od: "+message.getSender().getName());
			
			if(brojac==0){
				chooseTheBest(message);
			}
			
		}else if(message.getPerformative().toString().equals(Performative.PROPOSE.toString())){
			
			brojac--;
			System.out.println("brojac: "+brojac);
			mapa.put(message.getSender(), Integer.parseInt(message.getContent()));
			
			System.out.println(message.getReceiver().getName()+"  dobio PROPOSE od: "+message.getSender().getName());
			
			//ako su sve ponude stigle, izabrati najbolju
			if(brojac==0){
				chooseTheBest(message);
			}
			
		}else if(message.getPerformative().toString().equals(Performative.ACCEPT_PROPOSAL.toString())){
			
			System.out.println(message.getReceiver().getName()+" je dobio ACCEPT PROPOSAL od: "+message.getSender().getName());
			
			ACLMessage poruka=new ACLMessage();
			poruka.setSender(message.getReceiver());
			poruka.setReceivers(posiljalac);
			poruka.setPerformative(Performative.INFORM);
			
			
			String preformativaa=String.valueOf(Performative.INFORM.ordinal());
			String ponudjaci="";
			
			for(AID aid:mapa.keySet()){
				ponudjaci+=aid.getName()+":"+mapa.get(aid)+" ";
			}
			
			System.out.println("inicijator: "+inicijator+" LISTA:"+ponudjaci+" najbolji: "+najboljiA);
			
			String sadrzaj="Inform|Iniciator: "+inicijator.getName()+" ponudjaci su: "+ponudjaci+" ,najbolji je: "+najboljiA.getName();
			poruka.setContent(sadrzaj);
			
			
			//AKO SU NA ISTOM PORTU IDE JMS AKO NE REST
			if(poruka.getReceiver().getHost().getPort().equals(poruka.getSender().getHost().getPort())){
				JMSProducer2.sendJMS(poruka);
			}else{
				ResteasyClient client = new ResteasyClientBuilder().build();
				   ResteasyWebTarget target = client.target(
				     "http://localhost:" + poruka.getReceiver().getHost().getPort() + "/Agents/rest/agent/proslediPoruku/" +preformativaa + "/" + poruka.getSender().getName()+"/"+poruka.getReceiver().getName()+"/"+poruka.getContent());
				   Response response = target.request().get();
				   String ret = response.readEntity(String.class);	
			}
			
		}else if(message.getPerformative().toString().equals(Performative.REJECT_PROPOSAL.toString())){
			System.out.println(message.getReceiver().getName()+" je dobio REJECT PROPOSAL od: "+message.getSender().getName());

		}else if(message.getPerformative().equals(Performative.INFORM)){
			System.out.println("uso u inform: "+message.getContent());
			try {
				WsEndpoint.posaljiOdgovor(message.getContent());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void chooseTheBest(ACLMessage message){
			System.out.println("CHOOSE THE BEST AGENT");
			int min=Integer.MAX_VALUE;
			//AID najbolji=null;
			
			for(int b:mapa.values()){
				if(b<min){
					min=b;
				}
			}
			for(AID aid:mapa.keySet()){
				if(mapa.get(aid)==min){
					najboljiA=aid;
					System.out.println("Najbolji agent je: "+najboljiA.getName());
					break;
				}
			}
			//Svima salji reject proposal a najboljem accept proposal
			for(AbstractAgent ag:ponudjaci){
				
				String preformativa="";
				
				ACLMessage odgovor=new ACLMessage();
				odgovor.setContent(String.valueOf(min));
				odgovor.setSender(message.getReceiver());
				odgovor.setReceivers(ag.getAid());
				
				if(ag.getAid().getName().equals(najboljiA.getName())){
					odgovor.setPerformative(Performative.ACCEPT_PROPOSAL);
					preformativa=String.valueOf(Performative.ACCEPT_PROPOSAL.ordinal());
					//AKO SU NA ISTOM PORTU IDE JMS AKO NE REST
					if(ag.getAid().getHost().getPort().equals(odgovor.getReceiver().getHost().getPort())){
						JMSProducer2.sendJMS(odgovor);
					}else{
						ResteasyClient client = new ResteasyClientBuilder().build();
						   ResteasyWebTarget target = client.target(
						     "http://localhost:" + odgovor.getReceiver().getHost().getPort() + "/Agents/rest/agent/proslediPoruku/" +preformativa + "/" + odgovor.getSender().getName()+"/"+odgovor.getReceiver().getName()+"/"+odgovor.getContent());
						   Response response = target.request().get();
						   String ret = response.readEntity(String.class);	
					}
					
				}else{
					odgovor.setPerformative(Performative.REJECT_PROPOSAL);
					preformativa=String.valueOf(Performative.REJECT_PROPOSAL.ordinal());
					
					
					//AKO SU NA ISTOM PORTU IDE JMS AKO NE REST
					if(ag.getAid().getHost().getPort().equals(odgovor.getReceiver().getHost().getPort())){
						JMSProducer2.sendJMS(odgovor);
					}else{
						ResteasyClient client = new ResteasyClientBuilder().build();
						   ResteasyWebTarget target = client.target(
						     "http://localhost:" + odgovor.getReceiver().getHost().getPort() + "/Agents/rest/agent/proslediPoruku/" +preformativa + "/" + odgovor.getSender().getName()+"/"+odgovor.getReceiver().getName()+"/"+odgovor.getContent());
						   Response response = target.request().get();
						   String ret = response.readEntity(String.class);	
					}
				}
			}
		
	}

}
