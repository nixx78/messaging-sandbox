package lv.nixx.poc.jms.commandline;

import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CommandLineApplicationRunner {

	public static void main(String[] args) throws Exception {
		// Launch the application
		ConfigurableApplicationContext context = SpringApplication.run(CommandLineApplicationRunner.class, args);

		CommandLineApplication app = context.getBean(CommandLineApplication.class);

		try (Scanner keyboard = new Scanner(System.in)) {
			String message = "";
			do {
				System.out.println("Enter an message text, or 'exit' for cancel");
				message = keyboard.nextLine();

				app.sendMessageAndLogResponse(message);
			} while (!message.equalsIgnoreCase("exit"));
		}
		
		System.out.println("Exiting...");
		context.close();
	}

}
