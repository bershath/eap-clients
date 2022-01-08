This example would move messages from JBoss EAP 7.3.10 to AMQ 7.9.1 instance using Camel. Camel has been integrated in EAP using the Fuse 7.8.0 distribution. 
It is worth noting the use of ActiveMQConnectionFactory in the application code; this class is not exposed by the EAP distribution although the Artemis JMS client 
library can be found in the Artemis module. Henceforth, you need to make the Artemis module available to the client application using a deployment descriptor:
jboss-deployment-structure.xml.
