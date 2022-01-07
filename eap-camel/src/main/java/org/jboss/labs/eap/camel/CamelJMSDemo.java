package org.jboss.labs.eap.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.component.jms.JmsComponent;

import javax.annotation.Resource;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.ConnectionFactory;


@Startup
@ApplicationScoped
@ContextName("jms-camel-context")
public class CamelJMSDemo extends RouteBuilder {

    @Resource(mappedName = "java:/JmsXA")
    private ConnectionFactory connectionFactory;

    @Override
    public void configure() throws Exception {
        JmsComponent jmsComponent = new JmsComponent();
        jmsComponent.setConnectionFactory(connectionFactory);
        getContext().addComponent("jms", jmsComponent);
        from("jms:queue:A").to("jms:queue:B");
    }
}
