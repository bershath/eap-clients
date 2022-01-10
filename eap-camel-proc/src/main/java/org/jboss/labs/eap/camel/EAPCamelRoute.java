package org.jboss.labs.eap.camel;

import org.apache.camel.BeanInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.component.jms.JmsComponent;

import javax.annotation.Resource;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.util.Nonbinding;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;


@Startup
@ApplicationScoped
@ContextName("camel-eap-route-consumer")
public class EAPCamelRoute extends RouteBuilder {

    @Resource(mappedName = "java:/JmsXA")
    private ConnectionFactory connectionFactory;

    @Override
    public void configure() throws Exception {
        JmsComponent jmsComponent = new JmsComponent();
        jmsComponent.setConnectionFactory(connectionFactory);
        getContext().addComponent("jms", jmsComponent);
        from("jms:queue:B").process(new MessageProcessor());
    }
}
