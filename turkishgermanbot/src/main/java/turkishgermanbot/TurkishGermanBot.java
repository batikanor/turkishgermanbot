package turkishgermanbot;


import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;





public class TurkishGermanBot extends TelegramLongPollingBot{

	private ExecutorService executor;
	private int count = 0;
	
	public TurkishGermanBot(int numThreads) {
		
		executor = Executors.newFixedThreadPool(numThreads);
	}
	
	public void onUpdateReceived(Update update) {
		//System.out.println(update.getInlineQuery().toString());
		count++;
		executor.submit(new UpdateReceiver(this, update, count));

	
					
	}
	
	
	public String getBotUsername() {
		return Main.botUsername;
	}

	@Override
	public String getBotToken() {
		return Main.botToken;
	}

	




}




/*
SendMessage message = new SendMessage()
		.setChatId(chatId)
		.setText(text);

try {
	execute(message); ///< Sending message object to user
} catch (TelegramApiException e) {
	
	e.printStackTrace();
} 
*/