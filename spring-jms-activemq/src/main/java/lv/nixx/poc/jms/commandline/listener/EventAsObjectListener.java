package lv.nixx.poc.jms.commandline.listener;

import lv.nixx.poc.jms.commandline.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class EventAsObjectListener {

	private static final Logger LOG  = LoggerFactory.getLogger(EventAsObjectListener.class);

	@JmsListener(concurrency = "1", destination = "event.queue", containerFactory = "containerFactory")
	public void receiveMessage(Event message) {
		LOG.info("Queue listener1: Event as object come {}", message);
	}

	// How we can configure queue name from properties
	@JmsListener(concurrency = "1", destination = "${event.queue.name}", containerFactory = "containerFactory")
	public void receiveMessage1(Event message) {
		LOG.info("Queue listener2: Event as object come {}", message);
	}

}
