package org.jboss.labs.eap.timers;

import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;

@Singleton
@LocalBean
@Startup
public class TimedJMSProducer {

    @Resource
    private TimerService timerService;

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext jmsContext;

    private final String queueName = "A";
    private static Logger log = Logger.getLogger(TimedJMSProducer.class);

    @PostConstruct
    private void init(){
        timerService.createTimer(1000,10000,"Periodic JMS Message Producer");
    }

    @Timeout
    public void execute(Timer timer){
        log.debug(timer.getInfo());
        log.debug(timer.getTimeRemaining());
        sendMessage(queueName,"Next TimeOut " + timer.getNextTimeout());
        log.debug("Message successfully sent");
    }

    private void sendMessage(String queueName,String messageText){
        Queue queue = jmsContext.createQueue(queueName);
        JMSProducer jmsProducer = jmsContext.createProducer();
        jmsProducer.send(queue,messageText);
    }


}
