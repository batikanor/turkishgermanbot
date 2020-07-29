package turkishgermanbot;


import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
//import org.telegram.telegrambots.meta.generics.BotSession;

public class Main {
	private static final int THREAD_PER_CORE = 10; ///< If the bot becomes widely used, maybe turn this into * 1000
	// Instantiate TelegramBotsApi and register the bot
	public static void main(String[] args) {
	
		int numCores = Runtime.getRuntime().availableProcessors();
		int numThreads = numCores * THREAD_PER_CORE; ///< // We still need to have more threads working, because most of these threads just may be waiting for IO, so out cores may remain inactive...
		// ^Because these are not CPU INTENSIVE tasks, these are IO INTENSIVE tasks.
		
		System.out.println("You are running this code on a system with " + numCores + " cores");
		

		// Initialize Api Context
		ApiContextInitializer.init();
		
		// Instantiate TelegramBots API
		TelegramBotsApi botsApi = new TelegramBotsApi();
		
		// Register the bot
		try {
			
			botsApi.registerBot(new TurkishGermanBot(numThreads));
			
			System.out.println("Bot regisered succesfully");
		
			
		} catch (TelegramApiRequestException e) {
			
			e.printStackTrace();
		}
		
	}
	
}
