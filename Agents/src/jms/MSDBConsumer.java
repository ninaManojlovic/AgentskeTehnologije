package jms;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import agents.AID;
import agents.AbstractAgent;
import agents.AgentManager;
import message.ACLMessage;
import message.Performative;

@Startup
@Singleton
public class MSDBConsumer implements MessageListener{

	@EJB
	private AgentManager agentManager;
	 private QueueReceiver queueReceiver;
	 private QueueSession sessionPublish;
	 public static final String q1="jms/queue/queue1";
	 
	  private Context context;
	  private QueueConnectionFactory cf;
	  private Queue queue1;
	  private QueueConnection connection;

	 public MSDBConsumer() throws NamingException {
	  try {
	   this.context= new InitialContext();
	      this.cf=(QueueConnectionFactory) context.lookup("java:jboss/exported/jms/RemoteConnectionFactory");
	      this.queue1=(Queue) context.lookup(q1);
	      
	      context.close();
	      this.connection=cf.createQueueConnection();
	      
	      this.sessionPublish=connection.createQueueSession(false,Session.AUTO_ACKNOWLEDGE);
	      this.connection.start();
	   
	   queueReceiver=sessionPublish.createReceiver(queue1);
	   this.queueReceiver.setMessageListener(this);
	  } catch (JMSException e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	  }
	 }
	 
	 @Override
	 public void onMessage(Message arg0) {
		 ACLMessage message=null;
		try {
			message = (ACLMessage) ((ObjectMessage) arg0).getObject();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  AID odKoga= message.getSender();
	  AID primalac= message.getReceiver();
	  Performative tipPoruke=message.getPerformative();
	  String content=message.getContent();
	  
	 // System.out.println("MSDB primio poruku, posiljalac: "+odKoga.getName()+" za: "+primalac.getName()+" perfomativa: "+tipPoruke+" content: "+content);
	  //if(tipPoruke.equals(Performative.))
	  
	  AbstractAgent agentPrimalac=null;
	  for(AbstractAgent dd:agentManager.getRunning().values()){
		  if(dd.getAid().getName().equals(primalac.getName())){
			  agentPrimalac=dd;
		break;
		  }
	  }
	  System.out.println("agentprimalac je:"+agentPrimalac.getAid().getName());
	  agentPrimalac.handleMessage(message);
	  
	  
	 }
}
