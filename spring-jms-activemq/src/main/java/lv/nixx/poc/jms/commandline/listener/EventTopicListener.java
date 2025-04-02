package lv.nixx.poc.jms.commandline.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;

@Component
public class EventTopicListener {

    private static final Logger LOG = LoggerFactory.getLogger(EventTopicListener.class);
    public static final String EVENT_TOPIC = "event.topic";

    @JmsListener(destination = EVENT_TOPIC, containerFactory = "topicListenerFactory")
    public void messageReceiver1(Message message) throws JMSException {
        LOG.info("Event in listener1 [{}]", ((TextMessage) message).getText());
    }

    @JmsListener(destination = EVENT_TOPIC, containerFactory = "topicListenerFactory")
    public void messageReceiver2(Message message) throws JMSException {
        LOG.info("Event in listener2 [{}]", ((TextMessage) message).getText());
    }



}
