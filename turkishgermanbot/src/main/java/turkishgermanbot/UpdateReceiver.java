package turkishgermanbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Dice;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import objects.DiceStats;
import utilities.DBConnection;
import static java.lang.Math.toIntExact;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
public class UpdateReceiver implements Runnable {
	


	private enum privateChatState{
		START,
		THIS,
		THAT
	
	}
	
	public static final Random RANDOM = new Random();
	

	
	//Locale randomLanguage = new Locale("")
	//private String 
	
	private Update update;
	private int id;
	private Message updateMsg;
	private String msgStr;
	private long chatId;
	private long fromId;
	private int msgId;
	private TurkishGermanBot tgb; ///< This isnt that big of a deal, it was passed by reference anyways...
	//^ In Java everything except boxed types, native types and strings is passed by reference. Boxed types, native types and strings are all immutable and are passed by value
	 
	public UpdateReceiver(TurkishGermanBot tgb, Update update, int id) {
	
		//super();
		this.update = update;
		this.id = id;
		this.tgb = tgb;
	} 

	public void run() {

	
		//System.out.println("DENEMEMSG:" + update.getMessage().getText());
		
		

		int randInt = RANDOM.nextInt(Main.LANGUAGES.length);

		String language = Main.LANGUAGES[randInt]; ///< en, de, tr
		String country = Main.COUNTRIES[randInt];


		ResourceBundle rb = ResourceBundle.getBundle("MessagesBundle");
		Locale.setDefault(new Locale(language, country));
		
		System.out.println(rb.getString("name"));
		

		System.out.println(update.toString());
		
		if (update.hasMessage()) {
			updateMsg = update.getMessage();
			chatId = updateMsg.getChatId();
			msgId = updateMsg.getMessageId();
			fromId = updateMsg.getFrom().getId();
			
			
			String deneme = id + ":::" + String.valueOf(chatId);
			DBConnection.addGroupToUnion(chatId, deneme);
			
			System.out.println(updateMsg);
			
			if (updateMsg.hasDice()) {
				
				// For dices always do this
				Dice dice = updateMsg.getDice();
				int score = dice.getValue();
				String emoji = dice.getEmoji();
				try {
					Thread.sleep(2350);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				DiceStats diceStats = DBConnection.storeAndGetDiceStats(emoji, fromId, score);
				
				
				//System.out.println(emoji.length()); ///< I guess always 3
				
				SendMessage res = new SendMessage()
						.setText(emoji + "Skorunuz: " + score + "\nOrtalamanız: " + diceStats.average + "\nToplam deneme sayınız: " + diceStats.attempts)
						.setChatId(chatId)
						.setReplyToMessageId(msgId);
				try {
					tgb.execute(res);
				} catch (TelegramApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (fromId == chatId) {
				// Private messages!
				
				// Check the database to see which chat state we are in with the user
				
				if (updateMsg.hasText()) {
					msgStr = updateMsg.getText();
					
					
					DBConnection.addFilterToUnion("denemefilter" + id, msgStr, "denemeUnion" + id);
					
					if (msgStr.equals("/start") || msgStr.toLowerCase().equals("help") || msgStr.substring(1).toLowerCase().equals("help")) {
						sendTxt("I'm listing my functionalities!");
						sendOneButton("Click to see a random number", "Random Number");

					}
				}
				
			
			} else {
				// Group chat!
				
				// Check if group is in a 'Union' and so on
				
			}

		} else if (update.hasCallbackQuery()) {
			// Handling callback queries altogether
			CallbackQuery cbq = update.getCallbackQuery();
			String callData= cbq.getData();
			updateMsg = cbq.getMessage();
			msgId = updateMsg.getMessageId();
			fromId = cbq.getFrom().getId();
			chatId = updateMsg.getChatId();
			
			if (callData.contentEquals("Random Number")) {
			
				int rnum = RANDOM.nextInt();
				//System.out.println(rnum);
				
				EditMessageText editedMsg = new EditMessageText()
						.setChatId(chatId)
						.setMessageId(toIntExact(msgId))
						.setText("Random Number: " + rnum);
				try {
					tgb.execute(editedMsg);
				} catch (TelegramApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
						
			}
		}
		

	}
	 
	
	
	
	
	private boolean sendTxt(String s) {
		// Do not call this method before adding a chatId!
		
		SendMessage sMsg = new SendMessage()
				.setChatId(chatId)
				.setText(s);
		try {
			tgb.execute(sMsg);
			return true;
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean sendOneButton(String txt, String s) {
		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rowsInline = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> rowInline = new ArrayList<InlineKeyboardButton>();
		rowInline.add(new InlineKeyboardButton().setText(s).setCallbackData(s));
		rowsInline.add(rowInline);
		markupInline.setKeyboard(rowsInline);
		
		SendMessage sMsg = new SendMessage()
				.setChatId(chatId)
				.setText(txt)
				.setReplyMarkup(markupInline);
				
				
		try {
			tgb.execute(sMsg);
			return true;
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
}

