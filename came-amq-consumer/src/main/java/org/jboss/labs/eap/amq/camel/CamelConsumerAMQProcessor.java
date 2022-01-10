package org.jboss.labs.eap.amq.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import java.util.logging.Logger;


public class CamelConsumerAMQProcessor implements Processor {

    private Logger log = Logger.getLogger("CCAP");

    @Override
    public void process(Exchange exchange) throws Exception {
        Message camelMessage = exchange.getMessage();
        log.info(camelMessage.getBody(String.class));
    }
}
