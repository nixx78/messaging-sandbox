package lv.nixx.poc.jms.requestresponse;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class SynchRequestReceiver {

	private JmsTemplate jmsTemplate;

	@Autowired
	public void setJmsTemplate(JmsTemplate jmsQueueTemplate) {
		this.jmsTemplate = jmsQueueTemplate;
	}

	@JmsListener(destination = "synch.queue.request", containerFactory = "containerFactory")
	public void receiveMessage(TextMessage message) throws JMSException {

		final String request = message.getText();
		final String id = message.getJMSCorrelationID();
		
		System.out.println("Message received T:" + Thread.currentThread().getName() +  " message [" + request + "] id [" + id + "]");
		
		MessageCreator messageCreator = new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage msg = session.createTextMessage(request + ".response");
				msg.setJMSCorrelationID(id);
				return msg;
			}};

		jmsTemplate.send("synch.queue.response", messageCreator);
	}

}
