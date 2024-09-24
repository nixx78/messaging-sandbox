package lv.nixx.poc.jms.commandline.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lv.nixx.poc.jms.commandline.domain.Event;
import lv.nixx.poc.jms.commandline.domain.ResponseToEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.io.IOException;

@Component
public class EventSynchListener {

	private static final Logger LOG = LoggerFactory.getLogger(EventSynchListener.class);

	private JmsTemplate jmsTemplate;
	private ObjectMapper om;

	@Autowired
	public void setJmsTemplate(JmsTemplate jmsQueueTemplate) {
		this.jmsTemplate = jmsQueueTemplate;
	}

	@Autowired
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.om = objectMapper;
	}

	@JmsListener(destination = "synch.event.queue", containerFactory = "containerFactory")
	public void receiveMessage(TextMessage message) throws JMSException, IOException {
		Destination jmsReplyTo = message.getJMSReplyTo();

		String request = message.getText();
		String jmsCorrelationID = message.getJMSCorrelationID();

		LOG.info("Synch message [{}] come, reply queue [{}]", request, jmsReplyTo);

		Event e = om.readValue(request, Event.class);

		String jsonResponse = om.writeValueAsString(new ResponseToEvent(e.getId(), e.getBody() + ".response"));

		MessageCreator messageCreator = session -> {
			TextMessage textMessage = session.createTextMessage(jsonResponse);
			textMessage.setJMSCorrelationID(jmsCorrelationID);
			return textMessage;
		};

		jmsTemplate.send(jmsReplyTo, messageCreator);
	}

}
