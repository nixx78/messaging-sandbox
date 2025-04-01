package lv.nixx.poc.sandbox;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SimpleActiveMQListener {

    public static void main(String[] args) {

        loadProperties();

        Properties properties = System.getProperties();

        String brokerURL = String.valueOf(properties.get("spring.activemq.broker-url"));
        String user = String.valueOf(properties.get("spring.activemq.user"));
        String pass = String.valueOf(properties.get("spring.activemq.password"));
        String queue = String.valueOf(properties.get("event.queue.name"));

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerURL);
        factory.setUserName(user);
        factory.setPassword(pass);


        try (Connection connection = factory.createConnection()) {
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queue);

            MessageConsumer consumer = session.createConsumer(destination);
            System.out.printf("Waiting for messages from the queue [%s] broker [%s]%n", queue, brokerURL);

            consumer.setMessageListener(message -> {
                if (message instanceof TextMessage) {
                    try {
                        String text = ((TextMessage) message).getText();
                        System.out.println("Message received: " + text);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

            System.out.println("Press any key to exit");
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadProperties() {
        try (InputStream input = SimpleActiveMQListener.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("File application.properties not found!");
                return;
            }
            Properties properties = System.getProperties();
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
