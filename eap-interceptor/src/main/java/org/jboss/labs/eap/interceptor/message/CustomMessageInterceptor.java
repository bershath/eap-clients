/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.labs.eap.interceptor.message;

import org.apache.activemq.artemis.api.core.ActiveMQBuffer;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.ICoreMessage;
import org.apache.activemq.artemis.api.core.Interceptor;
import org.apache.activemq.artemis.api.core.Message;
import org.apache.activemq.artemis.core.protocol.core.Packet;
import org.apache.activemq.artemis.core.protocol.core.impl.wireformat.MessagePacket;
import org.apache.activemq.artemis.spi.core.protocol.RemotingConnection;
import org.jboss.logging.Logger;

/**
 *
 * @author  :   Tyronne W
 * @since   :   19-12-2020
 * @version :   3.0
 *
 * This interceptor intercepts incoming packets to the broker and performs a few operations:
 *      - Obtains the remote client's IP address
 *      - Obtains the client id
 *      - Logs the payload of String messages
 *
 *
 *  Configuration:
 *  The easiest is to define a module in the EAP subsystem :
 *  The module.xml needs to be placed along with the packaged jar in the 'main' folder of the module.
 *  The module.xml should look as follows:
 *
 * <?xml version="1.0" encoding="UTF-8"?>
 * <module xmlns="urn:jboss:module:1.1" name="org.jboss.labs.eap.interceptor.message">
 *     <resources>
 *         <resource-root path="eap-interceptor-1.0-SNAPSHOT.jar"/>
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
 *     ./subsystem=messaging-activemq/server=default:list-add(name=incoming-interceptors,value={name=>"org.jboss.labs.eap.interceptor.message.CustomMessageInterceptor",module=>"org.jboss.labs.eap.interceptor.message"})
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

public class CustomMessageInterceptor implements Interceptor {

    private static final Logger log = Logger.getLogger(CustomMessageInterceptor.class);

    @Override
    public boolean intercept(Packet packet, RemotingConnection remotingConnection) throws ActiveMQException {
        log.trace(Interceptor.class.getName() + " called");
        log.trace("Processing packet: " + packet.getClass().getName() + " that came from " + remotingConnection.getRemoteAddress() +".");
        log.trace("RemotingConnection: " + remotingConnection.getRemoteAddress() + " with client ID = " + remotingConnection.getID());

        if(packet instanceof MessagePacket){
            MessagePacket messagePacket = (MessagePacket) packet;
            ICoreMessage iCoreMessage = messagePacket.getMessage();
            // Make sure the message type to be a TextMessage
            if(iCoreMessage.getType() == Message.TEXT_TYPE){
                ActiveMQBuffer activeMQBuffer = iCoreMessage.getBodyBuffer();
                log.info("Payload " + activeMQBuffer.readNullableSimpleString());
            }
        }
        return true;  // Must return true to proceed to the next interceptor
    }
}