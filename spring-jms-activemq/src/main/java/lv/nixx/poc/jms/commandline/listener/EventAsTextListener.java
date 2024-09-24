package lv.nixx.poc.jms.commandline.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
public class EventAsTextListener {

	private static final Logger LOG  = LoggerFactory.getLogger(EventAsTextListener.class);

	@JmsListener(concurrency = "1", destination = "event.text.queue", containerFactory = "containerFactory")
	public void receiveMessage(TextMessage message) throws JMSException {
		LOG.info("Receive message as TextMessage, body [{}]", message.getText());
	}

}
