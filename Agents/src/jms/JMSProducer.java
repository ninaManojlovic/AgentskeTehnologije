package jms;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

@Singleton
@Startup
public class JMSProducer {
	
	public static final String q1="jms/queue/queue1";
	
	 private Context context;
	 private QueueConnectionFactory cf;
	 private Queue queue1;
	 private QueueSession sessionSubscribe;
	private QueueConnection connection;
	 private QueueSession sessionPublish;
	 private QueueSender sender;
	public String odgovor;

	public JMSProducer(){
		try{
			   this.context= new InitialContext();
			   this.cf=(QueueConnectionFactory) context.lookup("java:jboss/exported/jms/RemoteConnectionFactory");
			   this.queue1=(Queue) context.lookup(q1);
			   
			   context.close();
			   this.connection=cf.createQueueConnection();
			   this.sessionSubscribe=connection.createQueueSession(false,Session.AUTO_ACKNOWLEDGE);
			   this.sessionPublish=connection.createQueueSession(false,Session.AUTO_ACKNOWLEDGE);
			   this.connection.start();
			   
			   this.sender=sessionPublish.createSender(queue1);
			  
			   System.out.println("konstruktor komunikatora bez broja");
			   
			  // sendMessage("Poruka od Start1");
			   
			  }catch(Exception ex){
			   ex.printStackTrace();
			  }
	}
	
	public void sendMessage(String message){
		  System.out.println("usao u metodu sendMessage ChatApp: "+message);
		  TextMessage msg;
		  try {
		   msg = this.sessionPublish.createTextMessage(message);
		   this.sender.send(msg);
		   System.out.println("poslata: "+message);
		  } catch (JMSException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  }
		  
		 }
}
