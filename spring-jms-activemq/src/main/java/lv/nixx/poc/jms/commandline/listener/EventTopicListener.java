package lv.nixx.poc.jms.commandline.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

@Component
public class EventTopicListener {

    private static final Logger LOG = LoggerFactory.getLogger(EventTopicListener.class);

    @JmsListener(destination = "event.topic", containerFactory = "topicListenerFactory")
    public void receive(Message message) throws JMSException {
        LOG.info("Event in listener1 [{}]", ((TextMessage) message).getText());
    }

    @JmsListener(destination = "event.topic", containerFactory = "topicListenerFactory")
    public void receive1(Message message) throws JMSException {
        LOG.info("Event in listener2 [{}]", ((TextMessage) message).getText());
    }



}
