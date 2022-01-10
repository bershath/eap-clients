package org.jboss.labs.eap.amq.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.component.jms.JmsComponent;

import javax.annotation.Resource;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.ConnectionFactory;

@Startup
@ApplicationScoped
@ContextName("camel-consumer-amq")
public class CamelConsumerAMQ extends RouteBuilder {

    @Resource(mappedName = "java:/ConnectionFactorySimulator")
    private ConnectionFactory connectionFactory;

    @Override
    public void configure() throws Exception {
        JmsComponent jmsComponent = new JmsComponent();
        jmsComponent.setConnectionFactory(connectionFactory);
        getContext().addComponent("jms", jmsComponent);

        from("jms:inBound").process(new CamelConsumerAMQProcessor());
    }
}
