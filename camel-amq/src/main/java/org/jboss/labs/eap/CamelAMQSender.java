package org.jboss.labs.eap;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.component.jms.JmsComponent;

import javax.annotation.Resource;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.ConnectionFactory;

@Startup
@ApplicationScoped
@ContextName("amq-camel-context")
public class CamelAMQSender extends RouteBuilder {
    /**
     *
     * @author  : Tyronne
     * @since   : 08-01-2022
     * @version : 1.0
     *
     * This application would move messages from a destination deployed in EAP 7.3.10 to
     * an external AMQ 7.9.1 instance using Camel.
     *
     */

    @Resource (mappedName = "java:/JmsXA")
    private ConnectionFactory connectionFactory;

    //Connection URL and credentials for the ActiveMQ Artemis component
    private final String userName = "admin";
    private final String password = "jboss100";
    private final String url = "tcp://localhost:61616?jms.watchTopicAdvisories=false&maxReconnectAttempts=3";

    @Override
    public void configure() throws Exception {
        JmsComponent jmsComponent = new JmsComponent();
        jmsComponent.setConnectionFactory(connectionFactory);
        getContext().addComponent("jms", jmsComponent);

        // Remote ActiveMQ Artemis broker component.
        // The ActiveMQConnectionFactory class was not exposed by the EAP server,
        // using the jboss-deployment-structure to achieve this
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(url,userName,password);
        JmsComponent artemisComponent = new JmsComponent();
        artemisComponent.setConnectionFactory(activeMQConnectionFactory);
        getContext().addComponent("artemis",artemisComponent);

        from("jms:queue:C").to("artemis:queue:inBound");
    }
}
