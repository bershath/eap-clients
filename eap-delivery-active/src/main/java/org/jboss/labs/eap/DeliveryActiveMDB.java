package org.jboss.labs.eap;

import org.jboss.ejb3.annotation.DeliveryActive;
import org.jboss.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.transaction.UserTransaction;

@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
                @ActivationConfigProperty(propertyName = "destination", propertyValue = "/jms/queue/A"),
                @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
                @ActivationConfigProperty(propertyName = "user", propertyValue = "admin"),
                @ActivationConfigProperty(propertyName = "password", propertyValue = "jboss10)")
        })
@DeliveryActive(false)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class DeliveryActiveMDB implements MessageListener {

    private static Logger log = Logger.getLogger(DeliveryActiveMDB.class);
    @Override
    public void onMessage(Message message) {

        if (message instanceof TextMessage){
            try {
                log.info(message.getBody(String.class));
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
