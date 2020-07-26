package turkishgermanbot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;

public class Main {
	
	// Instantiate TelegramBotsApi and register the bot
	public static void main(String[] args) {
	
		int numCores = Runtime.getRuntime().availableProcessors();
		System.out.println("You are running this code on a system with " + numCores + " cores");
		// We still need to have more threads working, because most of these threads just may be waiting for IO, so out cores may remain inactive...
		// Because these are not CPU INTENSIVE tasks, these are IO INTENSIVE tasks.
		int numThreads = numCores * 10; ///< If the bot becomes widely used, maybe turn this into * 1000
		// Initialize Api Context
		ApiContextInitializer.init();
		
		// Instantiate TelegramBots API
		TelegramBotsApi botsApi = new TelegramBotsApi();
		
		// Register the bot
		try {
			
			botsApi.registerBot(new TurkishGermanBot());
			TurkishGermanBot.executor = Executors.newFixedThreadPool(numThreads);
			System.out.println("Bot regisered succesfully");
		
			
		} catch (TelegramApiRequestException e) {
			
			e.printStackTrace();
		}
		
	}
	
}
