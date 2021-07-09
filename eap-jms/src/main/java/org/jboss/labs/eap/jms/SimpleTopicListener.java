package org.jboss.labs.eap.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;


public class SimpleTopicListener implements MessageListener {


    public static void main(String[] args) {

        final String topicName = "jms/topic/notifications";
        final String url = "http-remoting://localhost:8080";
        final String connectionFactory = "jms/RemoteConnectionFactory";
        final String userName = "admin";
        final String password = "jboss10)";
        final DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z");


        Context jndiContext = null;
        try {
            Hashtable props = new Hashtable();
            props.put(Context.INITIAL_CONTEXT_FACTORY,"org.jboss.naming.remote.client.InitialContextFactory");

            //props.put(Context.INITIAL_CONTEXT_FACTORY,"org.wildfly.naming.client.WildFlyInitialContextFactory");
            props.put(Context.PROVIDER_URL, url);
            props.put(Context.SECURITY_PRINCIPAL, userName);
            props.put(Context.SECURITY_CREDENTIALS, password);
            jndiContext = new InitialContext(props);
        } catch (NamingException e) {
            System.out.println("Could not create JNDI " + "context: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }

        TopicConnectionFactory topicConnectionFactory = null;
        Topic topic = null;
        try {
            topicConnectionFactory = (TopicConnectionFactory)jndiContext.lookup(connectionFactory);
            topic = (Topic) jndiContext.lookup(topicName);
        } catch (NamingException e) {
            System.out.println("JNDI API lookup failed: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }

        TopicConnection  topicConnection = null;
        TopicSession topicSession = null;
        TopicSubscriber topicSubscriber = null;
        SimpleTopicListener simpleTopicListener = null;
        InputStreamReader inputStreamReader = null;


        char answer = '\0';
        try {
            topicConnection = topicConnectionFactory.createTopicConnection(userName, password);
            topicConnection.setClientID("Miles");
            topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            topicSubscriber = topicSession.createDurableSubscriber(topic, "Davis");
            simpleTopicListener = new SimpleTopicListener();
            topicSubscriber.setMessageListener(simpleTopicListener);
            topicConnection.start();
            System.out.println("SimpleTopicSubscriber started at :"+ dateFormat.format(new Date(System.currentTimeMillis())) + " , to end program, enter Q or q, " + "then <return>");
            inputStreamReader = new InputStreamReader(System.in);
            while (!((answer == 'q') || (answer == 'Q'))) {
                try {
                    answer = (char) inputStreamReader.read();
                } catch (IOException e) {
                    System.out.println("I/O exception: " + e.toString());
                }
            }
        } catch (JMSException e) {
            System.out.println("Exception occurred: " + e.toString());
        } finally {
            if (topicConnection != null) {
                try {
                    topicConnection.close();
                } catch (JMSException e) {}
            }
        }
    }

    public void onMessage(Message message) {
        System.out.println("[subs1 ] "+ message);

    }

}