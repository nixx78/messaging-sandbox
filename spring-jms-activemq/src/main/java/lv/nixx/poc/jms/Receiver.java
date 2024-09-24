package lv.nixx.poc.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;

@Component
public class Receiver {

	private JmsTemplate jmsTemplate;

	@Autowired
	@Qualifier("jmsQueueTemplate")
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	@JmsListener(destination = "synch.queue.request.jmstemplate", containerFactory = "containerFactory")
	public void receiveMessage(TextMessage message) throws JMSException {
		Destination jmsReplyTo = message.getJMSReplyTo();

		String request = message.getText();
		String jmsCorrelationID = message.getJMSCorrelationID();
		
		System.out.println("T:" + Thread.currentThread().getName() +  " message [" + message.getText() + "] reply queue [" + jmsReplyTo + "]");
		
		MessageCreator messageCreator = session -> {
			TextMessage msg = session.createTextMessage(request + ".response");
			msg.setJMSCorrelationID(jmsCorrelationID);
			return msg;
		};
		
		jmsTemplate.send(jmsReplyTo, messageCreator);
	}

}
