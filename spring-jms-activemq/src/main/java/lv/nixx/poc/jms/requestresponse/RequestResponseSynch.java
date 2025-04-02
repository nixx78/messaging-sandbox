package lv.nixx.poc.jms.requestresponse;

import java.lang.IllegalStateException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import jakarta.jms.*;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class RequestResponseSynch {

	public static final String REQUEST_SYNC_QUEUE = "request.sync.queue";
	public static final String RESPONSE_SYNC_QUEUE = "response.sync.queue";

	private static final Map<String, BlockingQueue<String>> map = new ConcurrentHashMap<>();

	private final JmsTemplate jmsTemplate;

	public RequestResponseSynch(JmsTemplate jmsQueueTemplate) {
		this.jmsTemplate = jmsQueueTemplate;
	}

	@JmsListener(destination = RESPONSE_SYNC_QUEUE, containerFactory = "containerFactory")
	public void responseCome(TextMessage message) throws Exception {

		String correlationID = message.getJMSCorrelationID();
		System.out.println("Response for Id [" + correlationID + "] come, message [" + message.getText() + "]");

		if (map.containsKey(correlationID)) {
			BlockingQueue<String> blockingQueue = map.get(correlationID);
			blockingQueue.put(message.getText());
		}
	}

	public String sendSyncRequest(String message) throws Exception {
		String id = UUID.randomUUID().toString();

		map.put(id, new ArrayBlockingQueue<>(1));

		jmsTemplate.send(REQUEST_SYNC_QUEUE, session -> {
            TextMessage msg = session.createTextMessage(message);
            msg.setJMSCorrelationID(id);
            return msg;
        });

		return waitForResponse(id);
	}

	public String waitForResponse(String id) throws Exception {

		try {
			if (map.containsKey(id)) {
				BlockingQueue<String> q = map.get(id);
				String resp = q.poll(5, TimeUnit.SECONDS);
				
				if (resp == null ) {
					throw new IllegalStateException("Timeout during waiting for response, id:" + id);
				}
				
				return resp;
			}
		} finally {
			// Always remove from map to avoid memory leak
			map.remove(id);
		}
		return null;
	}

}
