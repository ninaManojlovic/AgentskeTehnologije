package jms;

import javax.jms.Connection;
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

public class JMSProducer2 {
	
	public static void sendJMS(ACLMessage message){
		
		try{
			Context context=new InitialContext();
			QueueConnectionFactory cf=(QueueConnectionFactory)context.lookup("java:jboss/exported/jms/RemoteConnectionFactory");
			Queue queue1=(Queue)context.lookup("jms/queue/queue1");
			context.close();
			
			QueueConnection connection=cf.createQueueConnection();
			QueueSession sessionPublish=connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			connection.start();
			
			QueueSender sender=sessionPublish.createSender(queue1);
			
			ObjectMessage om=sessionPublish.createObjectMessage(message);
			sender.send(om);
			connection.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
