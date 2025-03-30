package lv.nixx.poc.sandbox.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class EventQueueListener {

	private static final Logger LOG  = LoggerFactory.getLogger(EventQueueListener.class);

	@JmsListener(concurrency = "1", destination = "${event.queue.name}", containerFactory = "containerFactory")
	public void receiveMessage(String message) {
		LOG.info("Queue listener, message come [{}]", message);
	}

}
