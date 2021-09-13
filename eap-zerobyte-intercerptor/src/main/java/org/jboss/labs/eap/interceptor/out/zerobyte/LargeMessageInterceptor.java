package org.jboss.labs.eap.interceptor.out.zerobyte;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.ICoreMessage;
import org.apache.activemq.artemis.api.core.Interceptor;
import org.apache.activemq.artemis.core.protocol.core.Packet;
import org.apache.activemq.artemis.core.protocol.core.impl.wireformat.MessagePacket;
import org.apache.activemq.artemis.spi.core.protocol.RemotingConnection;
import org.jboss.logging.Logger;
/**
 *
 * @author  :   Tyronne W
 * @since   :   13-09-2021
 * @version :   1.0
 *
 * This interceptor intercepts outgoing packets from the broker to filter zero byte length large-messages:
 *      - Obtains the destination details
 *      - Logs the zero byte length messageID and its destination
 *
 *
 *  Configuration:
 *  The easiest is to define a module in the EAP subsystem :
 *  The module.xml needs to be placed along with the packaged jar in the 'main' folder of the module.
 *  The module.xml should look identical to the following:
 *
 * <?xml version="1.0" encoding="UTF-8"?>
 * <module xmlns="urn:jboss:module:1.1" name="org.jboss.labs.eap.interceptor.out.zerobyte">
 *     <resources>
 *         <resource-root path="eap-zerobyte-intercerptor-1.0-SNAPSHOT.jar"/>
 *     </resources>
 *     <dependencies>
 *         <module name="org.apache.activemq.artemis"/>
 *         <module name="org.jboss.logging"/>
 *     </dependencies>
 * </module>
 *
 *
 * Deployment :
 * Start the eap server in admin mode:
 *     $ ./standalone.sh -c int.xml --start-mode=admin-only
 *
 * Log on to JBoss CLI:
 * Add the interceptor:
 *     ./subsystem=messaging-activemq/server=default:list-add(name=outgoing-interceptors,value={name=>"org.jboss.labs.eap.interceptor.out.zerobyte.LargeMessageInterceptor",module=>"org.jboss.labs.eap.interceptor.out.zerobyte"})
 *
 *
 * Optional:
 * This is an optional step to divert the interceptor logging to a separate file. The following logging configuration needs to
 * be added to the exiting logging configuration in the JBoss EAP configuration file.
 *
 * <!--  Artemis interceptor specific logging begins -->
 * 			<periodic-rotating-file-handler name="INTERCEPTOR_FILE" autoflush="true">
 *     			<level name="DEBUG"/>
 *     			<formatter>
 *         			<named-formatter name="PATTERN"/>
 *     			</formatter>
 *     			<file relative-to="jboss.server.log.dir" path="artemis_interceptor.log"/>
 *     			<suffix value=".yyyy-MM-dd"/>
 *     			<append value="true"/>
 * 			</periodic-rotating-file-handler>
 * 			<logger category="org.jboss.labs.jms" use-parent-handlers="false">
 *    				<level name="DEBUG"/>
 *    				<handlers>
 *         			<handler name="INTERCEPTOR_FILE"/>
 *    				</handlers>
 * 			</logger>
 * <!--  Artemis interceptor specific logging ends -->
 *
 *
 *
 */
public class LargeMessageInterceptor implements Interceptor {
    private static final Logger log = Logger.getLogger(LargeMessageInterceptor.class);

    @Override
    public boolean intercept(Packet packet, RemotingConnection remotingConnection) throws ActiveMQException {
        log.trace(Interceptor.class.getName() + " called");
        boolean healthyMessage = true;
        if(packet instanceof MessagePacket ){
            MessagePacket messagePacket = (MessagePacket) packet;
            ICoreMessage iCoreMessage = messagePacket.getMessage();
            // The sanity check would be performed against the large messages, omitting standard messages
            if(iCoreMessage.isLargeMessage()){
                if(iCoreMessage.getBodyBufferSize() == 0)
                    healthyMessage = false;
                    log.warn("Zero byte length message detected with messageID " + iCoreMessage.getMessageID() + " destination " + iCoreMessage.getAddress());
            }
        }
        return healthyMessage;
    }
}
