
package lv.nixx.poc.jms.requestresponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@ComponentScan(basePackages = "lv.nixx.poc.jms.*")
public class RequestResponseApplication {
	
	public static void main(String[] args)  {
		ConfigurableApplicationContext context = SpringApplication.run(RequestResponseApplication.class, args);

		RequestResponseSynch synchRequest = context.getBean(RequestResponseSynch.class);

		ExecutorService pool = Executors.newFixedThreadPool(5);

		List<Future<String>> futures = Stream.iterate(1, i -> i + 1)
				.map(i -> new Request(synchRequest, "Message: " + i))
				.limit(10).map(pool::submit)
				.collect(Collectors.toList());

		for (Future<String> future : futures) {
			try {
				System.out.println("Synch response [" + future.get() + "]");
			} catch (Exception ex) {
				System.err.println(ex);
			}
		}

		System.out.println("Existing...");
		pool.shutdown();
		context.close();
	}

	static class Request implements Callable<String> {

		private RequestResponseSynch requester;
		private String message;

		Request(RequestResponseSynch requester, String message) {
			this.requester = requester;
			this.message = message;
		}

		@Override
		public String call() throws Exception {
			return requester.sendSynchRequest(message);
		}

	}

}
