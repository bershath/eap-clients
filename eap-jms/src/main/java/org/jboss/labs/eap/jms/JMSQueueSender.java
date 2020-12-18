package org.jboss.labs.eap.jms;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JMSQueueSender {

    private static final String queueName = "exampleQueue";
    private static final String connectionFactoryName = "jms/RemoteConnectionFactory";
    private static final int numOfMessages = 200;
    private final String jmsUser = "admin";
    private final String jmsPassword = "jboss10)";

    JMSQueueSender(){
    }

    public static void main(String[] args){
        JMSQueueSender jmsQueueSender = new JMSQueueSender();
        jmsQueueSender.sendMessage(connectionFactoryName);
    }


    public void sendMessage(String connectionFactoryName){
        try(JMSContext jmsContext = getConnectionFactory(connectionFactoryName).createContext(jmsUser,jmsPassword)){
            Queue queue = jmsContext.createQueue(queueName);
            TextMessage textMessage = jmsContext.createTextMessage();
            for(int i = 1; i<= numOfMessages; i++){
                textMessage.setText("TXTMSG " + i);
                JMSProducer jmsProducer = jmsContext.createProducer();
                jmsProducer.send(queue,textMessage);
            }
        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }

    private ConnectionFactory getConnectionFactory(String connectionFactoryName) throws NamingException{
        Context context = new InitialContext();
        return (ConnectionFactory) context.lookup(connectionFactoryName);
    }


}
