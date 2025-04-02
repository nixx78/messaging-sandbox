package lv.nixx.poc.jms.requestresponse;

import jakarta.jms.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import static lv.nixx.poc.jms.requestresponse.RequestResponseSynch.REQUEST_SYNC_QUEUE;
import static lv.nixx.poc.jms.requestresponse.RequestResponseSynch.RESPONSE_SYNC_QUEUE;

@Component
public class SyncRequestReceiver {

	private JmsTemplate jmsTemplate;

	@Autowired
	public void setJmsTemplate(JmsTemplate jmsQueueTemplate) {
		this.jmsTemplate = jmsQueueTemplate;
	}

	@JmsListener(destination = REQUEST_SYNC_QUEUE, containerFactory = "containerFactory")
	public void receiveRequestMessage(TextMessage message) throws JMSException {

		final String request = message.getText();
		final String id = message.getJMSCorrelationID();
		
		System.out.println("Message received T:" + Thread.currentThread().getName() +  " message [" + request + "] id [" + id + "]");
		
		MessageCreator messageCreator = session -> {
            TextMessage msg = session.createTextMessage(request + ".response");
            msg.setJMSCorrelationID(id);
            return msg;
        };

		jmsTemplate.send(RESPONSE_SYNC_QUEUE, messageCreator);
	}

}
