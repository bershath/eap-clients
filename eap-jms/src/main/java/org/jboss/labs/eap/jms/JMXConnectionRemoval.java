package org.jboss.labs.eap.jms;

import org.apache.activemq.artemis.api.core.management.ActiveMQServerControl;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;
import java.util.Map;

public class JMXConnectionRemoval {
    public static void main(String[] args) throws Exception {

        String host = "localhost";
        int port = 9990;
        String userName = "admin";
        String password = "jboss10)";
        String eapObjectName = "org.apache.activemq.artemis:broker=\"default\"";
        String urlString = "service:jmx:remote+http://" + host + ":" + port;

        JMXServiceURL serviceURL = new JMXServiceURL(urlString);
        Map map = new HashMap();
        String[] credentials = new String[] { userName, password };
        map.put("jmx.remote.credentials", credentials);
        JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceURL, map);
        MBeanServerConnection connection = jmxConnector.getMBeanServerConnection();
        ObjectName objectName  =  ObjectName.getInstance(eapObjectName);
        String connectionID = "ae7b9251-4005-11eb-a890-b42e99ea6f5c";
        ActiveMQServerControl activeMQServerControl = MBeanServerInvocationHandler.newProxyInstance(connection,objectName,ActiveMQServerControl.class,false);
        boolean outcome = activeMQServerControl.closeConnectionWithID(connectionID);

        if(outcome)
            System.out.println("Successfully remove the connection with id: " + connectionID);
        else
            System.out.println("Failed to remove the connection with id: " + connectionID);

        jmxConnector.close();
    }
}
