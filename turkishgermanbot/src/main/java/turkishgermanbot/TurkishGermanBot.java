package turkishgermanbot;


import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;


public class TurkishGermanBot extends TelegramLongPollingBot{

	

	
	private ExecutorService executor;
	
	public TurkishGermanBot(int numThreads) {
		
		executor = Executors.newFixedThreadPool(numThreads);
	}
	
	public void onUpdateReceived(Update update) {
		//System.out.println(update.getInlineQuery().toString());
		executor.submit(new UpdateReceiver(this, update));
			
	}
	
	
	public String getBotUsername() {
		return Main.botUsername;
	}

	@Override
	public String getBotToken() {
		return Main.botToken;
	}

	




}


