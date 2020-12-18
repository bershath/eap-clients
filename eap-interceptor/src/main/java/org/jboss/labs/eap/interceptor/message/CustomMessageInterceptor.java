package org.jboss.labs.eap.interceptor.message;

import org.apache.activemq.artemis.api.core.ActiveMQBuffer;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.ICoreMessage;
import org.apache.activemq.artemis.api.core.Interceptor;
import org.apache.activemq.artemis.core.protocol.core.Packet;
import org.apache.activemq.artemis.core.protocol.core.impl.wireformat.MessagePacket;
import org.apache.activemq.artemis.reader.BytesMessageUtil;
import org.apache.activemq.artemis.spi.core.protocol.RemotingConnection;
import org.jboss.logging.Logger;

public class CustomMessageInterceptor implements Interceptor {

    private static Logger log = Logger.getLogger(CustomMessageInterceptor.class);

    @Override
    public boolean intercept(Packet packet, RemotingConnection remotingConnection) throws ActiveMQException {
        log.trace(Interceptor.class.getName() + " called");
        log.trace("Processing packet: " + packet.getClass().getName() + " that came from " + remotingConnection.getRemoteAddress() +".");
        log.trace("RemotingConnection: " + remotingConnection.getRemoteAddress() + " with client ID = " + remotingConnection.getID());

        if(packet instanceof MessagePacket){
            MessagePacket messagePacket = (MessagePacket) packet;
            ICoreMessage iCoreMessage = messagePacket.getMessage();
            log.info("Message ID " + iCoreMessage.getMessageID());
            ActiveMQBuffer activeMQBuffer = iCoreMessage.getBodyBuffer();
            //String textMessage = activeMQBuffer.readString();
            String stringMessage = BytesMessageUtil.bytesReadUTF(activeMQBuffer);
            log.info("Payload " + stringMessage);
        }
        return true;
    }
}


//
//    ActiveMQBuffer buffer = getInnerMessage().getDataBuffer();
//    int size = buffer.readInt();
//    byte[] bytes = new byte[size];
//   buffer.readBytes(bytes);
//           payload = new Binary(bytes)