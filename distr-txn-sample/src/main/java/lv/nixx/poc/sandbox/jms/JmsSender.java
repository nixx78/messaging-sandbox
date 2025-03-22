package lv.nixx.poc.sandbox.jms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class JmsSender {

    private final JmsTemplate jmsQueueTemplate;
    private final String queue;

    public JmsSender(JmsTemplate jmsQueueTemplate, @Value("${event.queue.name}") String queue) {
        this.jmsQueueTemplate = jmsQueueTemplate;
        this.queue = queue;
    }

    public void sendMessageToQueue(String message) {
        jmsQueueTemplate.send(queue, session -> session.createTextMessage(message));
    }


}
