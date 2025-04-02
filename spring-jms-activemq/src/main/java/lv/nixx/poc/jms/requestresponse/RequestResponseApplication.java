
package lv.nixx.poc.jms.requestresponse;

import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

@SpringBootApplication
@ComponentScan(basePackages = "lv.nixx.poc.jms.*")
public class RequestResponseApplication {
	
	public static void main(String[] args)  {
		ConfigurableApplicationContext context = SpringApplication.run(RequestResponseApplication.class, args);

		RequestResponseSynch syncRequest = context.getBean(RequestResponseSynch.class);

		ExecutorService pool = Executors.newFixedThreadPool(5);

		List<Future<String>> futures = Stream.iterate(1, i -> i + 1)
				.map(i -> new Request(syncRequest, "Message: " + i))
				.limit(10).map(pool::submit)
				.toList();

		for (Future<String> future : futures) {
			try {
				System.out.println("Sync response [" + future.get() + "]");
			} catch (Exception ex) {
				System.err.println(ex);
			}
		}

		System.out.println("Existing...");
		pool.shutdown();
		context.close();
	}

	@AllArgsConstructor
	static class Request implements Callable<String> {

		private final RequestResponseSynch requester;
		private final String message;

		@Override
		public String call() throws Exception {
			return requester.sendSyncRequest(message);
		}

	}

}
