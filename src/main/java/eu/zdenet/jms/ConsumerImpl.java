package eu.zdenet.jms;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Created by Zdeno on 11/13/2016.
 */
@Stateless
public class ConsumerImpl implements Consumer {

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void consume() {
        Connection connection = null;
        Session session = null;

        try {
            InitialContext initialContext = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup("/ConnectionFactory");
            connection = connectionFactory.createConnection();
            session = connection.createSession();
            Queue queue = (Queue) initialContext.lookup("/jms/queue/testQueue");

            MessageConsumer consumer = session.createConsumer(queue);
            connection.start();
            TextMessage message = (TextMessage) consumer.receive();
            System.out.println(message.getText());
            connection = null;
            //uncomment above if you want to cause transaction rollback. check that the message queue is not consumed
            ///subsystem=messaging-activemq/server=default/jms-queue=testQueue:read-attribute(name=message-count)
            connection.stop();
            System.out.println(queue.getQueueName());
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
    }
}
