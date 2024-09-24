package lv.nixx.poc.jms.commandline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Message;
import java.io.Serializable;

@Service
public class JmsSender {

    private JmsTemplate jmsQueueTemplate;
    private JmsTemplate jmsTopicTemplate;

    @Autowired
    public void setJmsQueueTemplate(JmsTemplate jmsQueueTemplate) {
        this.jmsQueueTemplate = jmsQueueTemplate;
    }

    @Autowired
    @Qualifier("topicTemplate")
    public void setJmsTopicTemplate(JmsTemplate jmsTopicTemplate) {
        this.jmsTopicTemplate = jmsTopicTemplate;
    }

    public void sendMessageToQueue(String queue, String message) {
        jmsQueueTemplate.send(queue, session -> session.createTextMessage(message));
    }

    public void sendMessageToTopic(String topic, String message) {
        jmsTopicTemplate.send(topic, session -> session.createTextMessage(message));
    }

    public Message sendAndReceive(String queue, String message) {
        return jmsQueueTemplate.sendAndReceive(queue, session -> session.createTextMessage(message));
    }

    public void convertAndSend(String queue, Serializable object) {
        jmsQueueTemplate.convertAndSend(queue, object);
    }


}
