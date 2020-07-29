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

		if (update.hasMessage()) {
			
		
			Message msg = update.getMessage();
			SendMessage toSend = new SendMessage();
			boolean toExecute = false;
			int updateMessageId = msg.getMessageId();
			
			// Do not even process bot messages
			
			
			
			System.out.println(msg.toString());
			
			if (msg.getDice() != null) {
				// basket, dart, dice ...
				int score = msg.getDice().getValue();
				
				toSend.setText(msg.getDice().getEmoji().toString() + " Skorunuz: " + score);
				toSend.setChatId(msg.getChatId());
				toSend.setReplyToMessageId(updateMessageId);
				try {
					Thread.sleep(3500);
					execute(toSend);
				} catch (TelegramApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			else if (msg.getFrom().getBot() == false) {
				
				if (msg.hasText()) {
					
					String text = msg.getText();
					String textLower = text.toLowerCase();
					long chatId = msg.getChatId();
					
					long fromId = update.getMessage().getFrom().getId();
					
					if (textLower.contains("türkü") || textLower.contains("turku")) {
					
						if (textLower.contains("soy")) {
							
							
							forwardMessage(fromId, chatId, updateMessageId);
							
							
						 	DeleteMessage dmsg = new DeleteMessage(chatId, updateMessageId);
						 	try {
								execute(dmsg);
							} catch (TelegramApiException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						 	
							toSend.setText("Soyadı türkü değil! Türkü onun ikinci adı...");
							//toSend.setReplyToMessageId(updateMessageId);
							toSend.setChatId(fromId); ///< Bot needs to have been started etc...
							//toSend.setChatId(batikansChatId);
							try {
								System.out.println(toSend.toString());
								execute(toSend); ///< Sending message object to user
								return;
							} catch (TelegramApiException e) {
								
								e.printStackTrace();
							} 
						}

					}
					

					
				} else if (msg.hasSticker()) {
					
				}
				
				
				
				
			}

		} else {
			// Update doesn't have message
			System.out.println("No mesage on update");
		}

	}
	
	public boolean forwardMessage(long toId, final long fromId, final int messageId ) {
	
		ForwardMessage fmsg = new ForwardMessage(toId, fromId, messageId);
		
		for (int tryy = 0; tryy < 10; tryy++) {
			try {
				execute(fmsg);
				return true;
			} catch (TelegramApiException e) {
				//Message couldn't be sent!
				class waitThread extends Thread{
					public void run() {
						if (couldntSend(fromId, messageId)) {
							// problem told to user successfully
							try {
								Thread.sleep(10000);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} else {
							
							// user couldnt be asked to start the bot somehow
							// Nvm then
							return;
						}
					}

				}

				
				
			}
			
			
		}
		return false;
	
		
	}
	
	public boolean couldntSend(long chatId, int messageId) {
		SendMessage toSend = new SendMessage();
		toSend.setText("Lütfen @turkishgermanbot a girip start/baslat a basiniz.");
		toSend.setChatId(chatId);
		toSend.setReplyToMessageId(messageId);
		try {
			execute(toSend);
			return true;
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean sendPrivateMessage(long userId, String msgText) {
		return false;
		

		// do it on another thread
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