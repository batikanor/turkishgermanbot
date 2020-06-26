package turkishgermanbot;

import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;




public class TurkishGermanBot extends TelegramLongPollingBot{

	private String botToken = "1009439204:AAF7MqFI0WCCtRxf5sC-DxSv5HpjN603vns";
	private String botUsername = "turkishgermanbot"; ///< Without '@'
	
	public void onUpdateReceived(Update update) {
				
		//System.out.println(update.getInlineQuery().toString());
		if (update.hasMessage() && update.getMessage().hasText()) {
			String text = update.getMessage().getText();
			long chatId = update.getMessage().getChatId();
			int replyToMessageId = update.getMessage().getMessageId();
			
			if (text.toLowerCase().contains("türkü") || text.toLowerCase().contains("turku")) {
				SendMessage message = new SendMessage();
				message.setChatId(chatId);
				message.setText("Soyadı türkü değil!");
				message.setReplyToMessageId(replyToMessageId);
				
				try {
					execute(message); ///< Sending message object to user
				} catch (TelegramApiException e) {
					
					e.printStackTrace();
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
			
		}
	}
	
	

	public String getBotUsername() {
		
		return botUsername;
	}

	@Override
	public String getBotToken() {
	
		return botToken;
	}

	




}
