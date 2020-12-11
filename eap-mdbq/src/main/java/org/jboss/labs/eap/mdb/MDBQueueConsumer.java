package org.jboss.labs.eap.mdb;

import org.jboss.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
                @ActivationConfigProperty(propertyName = "destination", propertyValue = "/jms/queue/A"),
                @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
                @ActivationConfigProperty(propertyName = "user", propertyValue = "admin"),
                @ActivationConfigProperty(propertyName = "password", propertyValue = "jboss10)")
        })
@TransactionManagement(TransactionManagementType.CONTAINER)
public class MDBQueueConsumer implements MessageListener {

    private static Logger log = Logger.getLogger(MDBQueueConsumer.class);

    /**
     * Passes a message to the listener.
     *
     * @param message the message passed to the listener
     */
    @Override
    public void onMessage(Message message) {

        try {
            if(message instanceof TextMessage)
                log.info("MSG Recvd: " + message.getBody(String.class));
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}