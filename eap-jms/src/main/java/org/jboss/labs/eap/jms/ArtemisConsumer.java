package org.jboss.labs.eap.jms;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ArtemisConsumer implements MessageListener {

    public ArtemisConsumer(){

    }

    static DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z");
    private static Session session;


    public static void main(String[] args) {

        String queueStr = "jms/queue/A";
        String connectionFactoryStr = "jms/RemoteConnectionFactory";


        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY,"org.wildfly.naming.client.WildFlyInitialContextFactory");
        props.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
        props.put(Context.SECURITY_PRINCIPAL, "admin");
        props.put(Context.SECURITY_CREDENTIALS, "jboss10)");


        Connection connection = null;
        try {
            Context context = new InitialContext(props);
            ConnectionFactory connectionFactory = (ConnectionFactory)context.lookup(connectionFactoryStr);
            Queue queue = (Queue)context.lookup(queueStr);

            connection = connectionFactory.createConnection("admin", "jboss10)");
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //System.out.println("CF Class name " + connectionFactory.getClass().getName());

            ArtemisConsumer artemisConsumer = new ArtemisConsumer();
            MessageConsumer messageConsumer = session.createConsumer(queue);
            messageConsumer.setMessageListener(artemisConsumer);

            InputStreamReader inputStreamReader = new InputStreamReader(System.in);
            System.out.println("message consumer started at " + dateFormat.format(new Date(System.currentTimeMillis())));
            System.out.println("enter Q or q to end the application");
            char answer = '\0';

            while (!((answer == 'q') || (answer == 'Q'))) {
                try {
                    answer = (char) inputStreamReader.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (JMSException e){
            e.printStackTrace();
        } finally {
            try{
                if(connection != null)
                    connection.close();
            }catch (JMSException e){
                e.printStackTrace();
            }
        }
    }

    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            System.out.println(" MSG rcvd " + textMessage.getJMSMessageID() + " at : " +  dateFormat.format(new Date(System.currentTimeMillis())));
            Thread.sleep(30);
            throw new RuntimeException("Test Error Occured");
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }

    }

}