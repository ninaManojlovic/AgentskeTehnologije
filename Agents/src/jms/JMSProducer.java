package jms;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import message.ACLMessage;
/*
@Singleton
@Startup
public class JMSProducer {

 public static final String q1="jms/queue/queue1";
 
 private Context context;
 private QueueConnectionFactory cf;
 private Queue queue1;

private QueueConnection connection;
 private QueueSession sessionPublish;
 private QueueSender sender;
public String odgovor;

public JMSProducer(){
 
 
}

public void sendMessage(ACLMessage message){
 
 try{
     this.context= new InitialContext();
     this.cf=(QueueConnectionFactory) context.lookup("java:jboss/exported/jms/RemoteConnectionFactory");
     this.queue1=(Queue) context.lookup(q1);
     
     context.close();
     this.connection=cf.createQueueConnection();
     this.sessionPublish=connection.createQueueSession(false,Session.AUTO_ACKNOWLEDGE);
     this.connection.start();
     
     this.sender=sessionPublish.createSender(queue1);
 
    }catch(Exception ex){
     ex.printStackTrace();
    }
   
   try {
  
	   System.out.println("jms produser salje poruku");
    //message=(ACLMessage) this.sessionPublish.createObjectMessage(message);
	   ObjectMessage om=this.sessionPublish.createObjectMessage(message);
    this.sender.send(om);
    
    connection.close();
    
    System.out.println("poslata: "+message.getContent());
   } catch (JMSException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
   
  }
}
*/