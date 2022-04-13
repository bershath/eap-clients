package org.jboss.labs.eap.jms;

import org.jboss.logging.Logger;

import javax.ejb.*;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(name = "RemoteMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "HELLOWORLDMDBQueue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "user", propertyValue = "admin"),
        @ActivationConfigProperty(propertyName = "password", propertyValue = "jboss100"),
        @ActivationConfigProperty(propertyName = "minSession", propertyValue = "15"),
        @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "15")
})
@TransactionManagement(TransactionManagementType.CONTAINER)
public class RemoteMDB implements MessageListener {

    private static Logger log = Logger.getLogger(RemoteMDB.class);

    public RemoteMDB() {}


    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void onMessage(Message message) {
        try {
            if(message instanceof TextMessage){
                TextMessage textMessage = (TextMessage) message;
                log.info("[msg] " + textMessage.getJMSMessageID() + " rcvd ");
                //org.apache.activemq.artemis.ra.inflow.ActiveMQActivationSpec acmq;
                //throw new NullPointerException();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
