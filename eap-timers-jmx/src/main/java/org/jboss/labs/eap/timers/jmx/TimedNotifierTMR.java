package org.jboss.labs.eap.timers.jmx;

import org.apache.activemq.artemis.api.core.management.QueueControl;
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
import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;


/**
 *
 * @author  Tyronne Wickramarathne
 * @since   15-12-2020
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



@LocalBean
@Startup
@Singleton
public class TimedNotifierTMR {

    @Resource
    private TimerService timerService;

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext jmsContext;

    private final String queueName = "A";
    private static Logger log = Logger.getLogger(TimedNotifierTMR.class);

    @PostConstruct
    private void init(){
        timerService.createTimer(1000,600000,"Periodic JMS Message Producer");
    }

    @Timeout
    public void execute(Timer timer){
        log.debug(timer.getInfo());
        log.debug(timer.getTimeRemaining());

        try {
            String eapObjectName = "org.apache.activemq.artemis:broker=\"default\",component=addresses,address=\"jms.queue.exampleQueue\",subcomponent=queues,routing-type=\"anycast\",queue=\"jms.queue.exampleQueue\"";
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName = ObjectName.getInstance(eapObjectName);
            QueueControl queueControl = MBeanServerInvocationHandler.newProxyInstance(mBeanServer,objectName,QueueControl.class,false);
            long messageCount = queueControl.countMessages();
            if(messageCount > 200) {
                sendMessage(queueName, "Next TimeOut " + timer.getNextTimeout());
                log.info("Message count reached the limit of " + messageCount +", notification sent");
            }
            else{
                log.trace("Message count is " + messageCount + ", status \'ok\'");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendMessage(String queueName,String messageText){
        Queue queue = jmsContext.createQueue(queueName);
        JMSProducer jmsProducer = jmsContext.createProducer();
        jmsProducer.send(queue,messageText);
    }

}
