package agent.pingPong;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import agents.AID;
import agents.AbstractAgent;
import jms.JMSProducer2;
import message.ACLMessage;
import message.Performative;

public class WordCounterSlave extends AbstractAgent{

	public WordCounterSlave(AID aid) {
		super(aid);
	}

	@Override
	protected void onMessage(ACLMessage message) {
		
		String putanja=message.getContent();
		
		int cnt=0;
       
        BufferedReader br = null;
     

        try {

            String sCurrentLine=null;
 
            br = new BufferedReader(new FileReader(putanja));

            while ((sCurrentLine = br.readLine()) != null) {
               
                String replaced=sCurrentLine.replaceAll("[^a-zA-Z0-9]+", " ").trim();
               
                String[] splited=replaced.split("\\s+");
                cnt+=splited.length;
                
            }
            System.out.println(message.getContent()+" ima reci: "+cnt);
            
            ACLMessage odgovor=new ACLMessage();
            odgovor.setContent(String.valueOf(cnt));
            odgovor.setPerformative(Performative.AGREE);
            odgovor.setSender(message.getReceiver());
            odgovor.setReceivers(message.getSender());
            
            JMSProducer2.sendJMS(odgovor);

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null)
                    br.close();

                
            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
	}

}
