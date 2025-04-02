package lv.nixx.poc.jms.commandline;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lv.nixx.poc.jms.commandline.domain.Event;
import lv.nixx.poc.jms.commandline.domain.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;

@Component
public class CommandLineApplication {

    private static final Logger LOG = LoggerFactory.getLogger(CommandLineApplication.class);

    private final JmsSender jmsSender;
    private final ObjectMapper om;

    public CommandLineApplication(JmsSender jmsSender, ObjectMapper om) {
        this.jmsSender = jmsSender;
        this.om = om;
    }

    public void sendMessageAndLogResponse(String message) throws JMSException, JsonProcessingException {

        Event event = new Event(System.currentTimeMillis(), message, EventType.INTERNAL);
        jmsSender.convertAndSend("event.queue", event);

        String jsonEvent = om.writeValueAsString(event);
        jmsSender.sendMessageToTopic("event.topic", jsonEvent);
        jmsSender.sendMessageToQueue("event.text.queue", jsonEvent);

        Message sendAndReceive = jmsSender.sendAndReceive("sync.event.queue", jsonEvent);
        LOG.info("Sync response to request [{}]", ((TextMessage) sendAndReceive).getText());
    }


}
