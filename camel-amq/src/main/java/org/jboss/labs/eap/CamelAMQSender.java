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
     * <b>Called on initialization to build the routes using the fluent builder syntax.</b>
     * <p/>
     * This is a central method for RouteBuilder implementations to implement the routes using the Java fluent builder
     * syntax.
     *
     * @throws Exception can be thrown during configuration
     */

    @Resource (mappedName = "java:/JmsXA")
    private ConnectionFactory connectionFactory;

    public static final String userName = "admin";
    public static final String password = "jboss100";
    public static final String url = "tcp://localhost:61616?jms.watchTopicAdvisories=false&maxReconnectAttempts=3";

    @Override
    public void configure() throws Exception {
        JmsComponent jmsComponent = new JmsComponent();
        jmsComponent.setConnectionFactory(connectionFactory);
        getContext().addComponent("jms", jmsComponent);

        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(url,userName,password);
        JmsComponent artemisComponent = new JmsComponent();
        artemisComponent.setConnectionFactory(activeMQConnectionFactory);
        getContext().addComponent("artemis",artemisComponent);

        from("jms:queue:C").to("artemis:queue:inBound");
    }
}
