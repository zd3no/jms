package eu.zdenet.jms;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.enterprise.inject.spi.Producer;
import javax.inject.Inject;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Produces;
import java.util.Enumeration;
import java.util.Properties;

import static javax.naming.Context.INITIAL_CONTEXT_FACTORY;
import static javax.naming.Context.PROVIDER_URL;

@Stateless
public class JmsExample implements eu.zdenet.jms.Producer{

    //<jms-queue name="testQueue" entries="java:/jms/queue/testQueue"/>
    //<jms-queue name="testQueueRemote" entries="java:/jboss/exported/jms/queue/testQueueRemote"/>
    // in wildfly: /wildfly/bin/add-user.sh test test
    //service:jmx:remote+http://localhost:9990

    @Inject
    Consumer consumer;

    public void addMessage(){
        Connection connection = null;
        Session session = null;

        try {
            InitialContext initialContext = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup("/ConnectionFactory");
            connection = connectionFactory.createConnection();
            session = connection.createSession();
            Queue queue = (Queue) initialContext.lookup("/jms/queue/testQueue");

            MessageProducer producer = session.createProducer(queue);
            QueueBrowser browser = session.createBrowser(queue);
            TextMessage message = session.createTextMessage("Hello World");
            connection.start();
            producer.send(message);
            Enumeration msgs = browser.getEnumeration();
            int count = 0;
            while (msgs.hasMoreElements()) {
                Message tempMsg = (Message)msgs.nextElement();
                count ++;
            }
            assert count == 1;
            connection.stop();
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
                session.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        consumer.consume();
    }

}
