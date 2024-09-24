
package lv.nixx.poc.jms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class MultipleThreadApplication {
	
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(MultipleThreadApplication.class, args);

		JmsTemplate jmsTemplate = context.getBean("jmsQueueTemplate", JmsTemplate.class);
		ExecutorService pool = Executors.newFixedThreadPool(5);
		
		List<Future<String>> futures = Stream.iterate(1, i->i+1)
				.map(i -> new Request(jmsTemplate, "Message: " + i))
				.limit(10)
				.map(pool::submit)
				.collect(Collectors.toList());
		
		for (Future<String> future : futures) {
			System.out.println("Synch response [" + future.get() + "]");
		}
		
		System.out.println("Existing...");
		pool.shutdown();
		context.close();
	}

	
	static class Request implements Callable<String> {
		
		private JmsTemplate jmsTemplate;
		private String message;

		Request(JmsTemplate jmsTemplate, String message) {
			this.jmsTemplate = jmsTemplate;
			this.message = message;
		}

		@Override
		public String call() throws Exception {
			Message sendAndReceive = jmsTemplate.sendAndReceive("synch.queue.request.jmstemplate", session -> session.createTextMessage(message));
			return ((TextMessage)sendAndReceive).getText();
		}
		
	}

	
	


}
