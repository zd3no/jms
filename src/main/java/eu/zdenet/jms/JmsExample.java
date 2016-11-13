package eu.zdenet.jms;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.enterprise.inject.spi.Producer;
import javax.inject.Inject;
import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Produces;

/**
 * Created by Zdeno on 11/13/2016.
 */
@Stateless
public class JmsExample implements eu.zdenet.jms.Producer{

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
            TextMessage message = session.createTextMessage("Hello World");
            connection.start();
            producer.send(message);
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
