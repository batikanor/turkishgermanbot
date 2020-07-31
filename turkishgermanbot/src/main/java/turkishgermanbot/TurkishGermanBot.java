package turkishgermanbot;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;




public class TurkishGermanBot extends TelegramLongPollingBot{

	
	// If you are helping develop this bot, please ensure that you do not share the following private variables with anyone!
	private String botToken = "1009439204:AAF7MqFI0WCCtRxf5sC-DxSv5HpjN603vns";
	private String botUsername = "turkishgermanbot"; ///< Without '@'
	//private long batikansChatId = (long) 597803356; ///< For testing purposes
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
		
		return botUsername;
	}

	@Override
	public String getBotToken() {
	
		return botToken;
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