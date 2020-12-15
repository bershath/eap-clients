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
 * Please make sure to enable JMX statistics in the EAP server or this code might not work.
 *                 <management jmx-enabled="true"/>
 *
 */

public class JMXQueueMonitor {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 9990;  // management-http port
        String urlString = "service:jmx:remote+http://" + host + ":" + port;
        System.out.println("\n\n\t****  urlString: "+urlString);;
        JMXServiceURL serviceURL = new JMXServiceURL(urlString);

        Map map = new HashMap();
        String[] credentials = new String[] { "admin", "jboss10)" };
        map.put("jmx.remote.credentials", credentials);
        JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceURL, map);

        MBeanServerConnection connection = jmxConnector.getMBeanServerConnection();

        String eapObjectName = "org.apache.activemq.artemis:broker=\"default\",component=addresses,address=\"jms.queue.exampleQueue\",subcomponent=queues,routing-type=\"anycast\",queue=\"jms.queue.exampleQueue\"";
        ObjectName objectName  =  ObjectName.getInstance(eapObjectName);
        QueueControl queueControl = MBeanServerInvocationHandler.newProxyInstance(connection,objectName,QueueControl.class,false);

        System.out.println(queueControl.getMessageCount());
        System.out.println(queueControl.getConsumerCount());

        jmxConnector.close();
    }
}
