package org.jboss.labs.eap.jms;


import org.apache.activemq.artemis.api.core.management.QueueControl;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author  Tyronne Wickramarathne
 * @since   11-01-2023
 * @version 1.0
 *
 * This Timer bean would observe the message count of the assigned destination and
 * sends a notification message to a desired destination stating the observing destination
 * had reached the message count for which a notification was needed.
 *
 * It is essential to enable jmx management in the EAP configuration:
 *      <management jmx-enabled="true"/>
 *
 * The Artemis management API is not exposed to EE components deployed in the application server.
 * Thus, it is essential to utilise on the jboss-deployment-structure.xml file to expose the
 * implementation classes to the application or else the system would throw a class not found
 * exception for the org.apache.activemq.artemis.api.core.management.QueueControl class.
 *
 * Further, it is also possible to declare Artemis as a global module in the EAP configuration:
 *
 *      <subsystem xmlns="urn:jboss:domain:ee:4.0">
 *          <global-modules>
 *               <module name="org.apache.activemq.artemis"/>
 *          </global-modules>
 *          ....
 *          ....
 *      </subsystem>
 *
 */

public class JMXRemoveMessage {

    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 9990;  // management-http port
        String urlString = "service:jmx:remote+http://" + host + ":" + port;
        System.out.println("\n\n\t****  urlString: "+urlString);;
        JMXServiceURL serviceURL = new JMXServiceURL(urlString);
        String jmsDestination = "jms.queue.A";


        Map map = new HashMap();
        String[] credentials = new String[] { "admin", "jboss10)" };
        map.put("jmx.remote.credentials", credentials);
        JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceURL, map);

        MBeanServerConnection connection = jmxConnector.getMBeanServerConnection();

        String eapObjectName = "org.apache.activemq.artemis:broker=\"default\",component=addresses,address=\""+ jmsDestination+"\",subcomponent=queues,routing-type=\"anycast\",queue=\""+ jmsDestination+"\"";
        ObjectName objectName  =  ObjectName.getInstance(eapObjectName);
        QueueControl queueControl = MBeanServerInvocationHandler.newProxyInstance(connection,objectName,QueueControl.class,false);
        System.out.println(queueControl.getMessageCount());
        System.out.println(queueControl.removeMessage(30));
        System.out.println(queueControl.getMessageCount());
    }

}
