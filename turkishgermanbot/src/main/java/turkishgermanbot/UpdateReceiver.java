package turkishgermanbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Dice;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import objects.AdayFormResults;
import objects.DiceStats;
import utilities.DBConnection;
import utilities.HelperFunctions;
import utilities.SheetsConnection;

import static java.lang.Math.toIntExact;

import java.io.IOException;

import java.security.GeneralSecurityException;


public class UpdateReceiver implements Runnable {
	

	


	
	public static final Random RANDOM = new Random();
	
	private static final Logger LOGGER = LogManager.getLogger(UpdateReceiver.class); 

	private Update update;
	private Message updateMsg;
	private String msgStr;
	private long chatId;
	private long fromId;
	private int msgId;
	ResourceBundle rb;
	private TurkishGermanBot tgb; ///< This isnt that big of a deal, it was passed by reference anyways...
	//^ In Java everything except boxed types, native types and strings is passed by reference. Boxed types, native types and strings are all immutable and are passed by value
	 
	public UpdateReceiver(TurkishGermanBot tgb, Update update) {
		//super();
		this.update = update;
		this.tgb = tgb;
	} 

	public void run() {

	
		//System.out.println("DENEMEMSG:" + update.getMessage().getText());
		
		//LOGGER.info("new update: {}", update);
		//LOGGER.trace("Received update");
		//LOGGER.debug("test");
		LOGGER.info(update.toString());
		int randInt = RANDOM.nextInt(Main.LANGUAGES.length);

		String language = Main.LANGUAGES[randInt]; ///< en, de, tr
		String country = Main.COUNTRIES[randInt];
		//LOGGER.error("test");

		rb = ResourceBundle.getBundle("MessagesBundle", new Locale(language, country));
		
		
		//System.out.println(rb.getString("name"));
		
		
		//System.out.println(update.toString());
		
		if (update.hasMessage()) {
			updateMsg = update.getMessage();
			chatId = updateMsg.getChatId();
			msgId = updateMsg.getMessageId();
			fromId = updateMsg.getFrom().getId();
			
			
			//String deneme = id + ":::" + String.valueOf(chatId);
			//DBConnection.addGroupToUnion(chatId, deneme);
			
			//System.out.println(updateMsg);
			
			if (updateMsg.hasSticker()) {
				Sticker st = updateMsg.getSticker();
				//System.out.println(st.getFileId());
				//System.out.println(st.getFileUniqueId());
				
				// Coin toss
				if (st.getFileUniqueId().contentEquals("AgADAQADXMGhIw")) {
					String emoji = st.getEmoji();
					int score = RANDOM.nextInt(2);
					System.out.println(score); // 0 Yazi 1 tura
					DiceStats diceStats = DBConnection.storeAndGetDiceStats(emoji, fromId, score);
					String coinState;
					if (score == 1) {
						coinState = rb.getString("heads");
					} else {
						coinState = rb.getString("tails");
					}
					

					SendMessage res = new SendMessage()
							.setText(rb.getString("coin") + ": " + rb.getString("yourScore") + ": " + score + " (" + coinState + ")" +
									"\n" + rb.getString("yourAverage")  + " (" + rb.getString("headsRate") + ")" + ": " + diceStats.average + "\n" + rb.getString("attemptCount") + ": " + diceStats.attempts)
							.setChatId(chatId)
							.setReplyToMessageId(msgId);
					try {
						tgb.execute(res);
					} catch (TelegramApiException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						LOGGER.error("Request Failed", e);
					}
				}
			}
			else if (updateMsg.hasDice()) {
				
				
				// For dices always do this
				Dice dice = updateMsg.getDice();
				int score = dice.getValue();
				String emoji = dice.getEmoji();
				try {
					Thread.sleep(2350);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
					LOGGER.error("Request Failed", e1);
				}
				DiceStats diceStats = DBConnection.storeAndGetDiceStats(emoji, fromId, score);
				
				
				//System.out.println(emoji.length()); ///< I guess always 3
				
				SendMessage res = new SendMessage()
						.setText(emoji + rb.getString("yourScore") + ": " + score + "\n" + rb.getString("yourAverage") + ": " + diceStats.average + "\n" + rb.getString("attemptCount") + ": " + diceStats.attempts)
						.setChatId(chatId)
						.setReplyToMessageId(msgId);
				try {
					tgb.execute(res);
				} catch (TelegramApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (updateMsg.hasText()) {
				// Text messages, private or group... both...
				msgStr = updateMsg.getText();
				
				
			}
			if (fromId == chatId) {
				// Private messages!
				
				// Check the database to see which chat state we are in with the user
				
			
				
				if (updateMsg.hasText()) {
					msgStr = updateMsg.getText();
					
					
					//DBConnection.addFilterToUnion("denemefilter" + id, msgStr, "denemeUnion" + id);
					
					if (msgStr.substring(1).startsWith("adaybilgiformu")) {
						List<String> departments = new ArrayList<String>();
						for (String dep : Main.DEPARTMENTS) {
							if(msgStr.substring(15).toLowerCase(new Locale("tr", "TR")).contains(dep.toLowerCase(new Locale("tr", "TR")).subSequence(0, 4))) {
								continue;
							} else {

								departments.add(dep);
							}
						}
						if (departments.size() == Main.DEPARTMENTS.length) {
							departments.clear();
							
						}
		
						sendAdayFormResults(departments);
					}
					else if (msgStr.startsWith("/start")) {
						if (msgStr.contentEquals("/start")) {
							sendCommands();
						} else {
							//String arg = msgStr.substring(7);
							sendAdayFormResults(null);
						
						}

					}
					
					else if (msgStr.toLowerCase().equals("help") || msgStr.substring(1).toLowerCase().equals("help")) {
						sendCommands();
	
					}
					
					else if (msgStr.startsWith("/md")) {
						// markdown
						sendMarkdown(msgStr.substring(4));
					}
					else if (msgStr.startsWith("/html")) {
						sendHtml(msgStr.substring(6));
					}
					else if (msgStr.startsWith("/parse")){
						//sendMarkdown(preParse(msgStr.substring(7)));
						
						preParse(msgStr.substring(7));
					}
					else if (msgStr.length() == 9) {
						int year = Integer.parseInt(msgStr.substring(0, 2));
						System.out.println(year);
						int faculty = Integer.parseInt(msgStr.substring(2, 4));
						System.out.println(faculty);
						int department = Integer.parseInt(msgStr.substring(4,6));
						System.out.println(department);
						int rank = Integer.parseInt(msgStr.substring(6));
						System.out.println(rank);
						if (year > 12 && faculty < 6 && department < 7 ) {
							// Muhtemelen geçerli numara
							sendOneButton("Gruba şuradan katılabilirsin :) Lütfen pinli mesajı oku", "Öğrenci Grubu", Main.TAUStudentGroupLink);
							// Bir veritabanina da kaydedebilirdim, veya herkese mail gonderebilirdim, ancak yasal olur mu bilmiyorum ogrenci numaralarini oyle kaydetmek. O yuzden boyle yapiyorum... Zaten ogrenci olmayan birinin bu standartlara gore numara uyduracagini sanmiyorum... Belki nadiren
						}
						
					}
				}
				
			
			} else {
				// Group chat!
				if (msgStr.substring(1).startsWith("adaybilgiformu")) {
					sendOneButton("Form Sonuçlarını Gör","Form Sonuçları" , "telegram.me/turkishgermanbot?start=adaybilgiformu");
				}
				// Check if group is in a 'Union' and so on
				
			}
			
		} else if (update.hasCallbackQuery()) {
			// Handling callback queries altogether
			CallbackQuery cbq = update.getCallbackQuery();
			
			String callData= cbq.getData();
			updateMsg = cbq.getMessage();
			msgId = updateMsg.getMessageId();
			fromId = cbq.getFrom().getId();
			System.out.println(cbq.getMessage().getText());
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
					//e.printStackTrace();
					LOGGER.error("Request Failed", e);
				}
						
				
			}
			
			
			else if (callData.contentEquals("TAUPRIVATE")) {
				sendTxt("Öğrenci numaranızı yazabilir misiniz?\n(Alternatif olarak @tausohbet grubuna girip\n'@admins ben öğrenciyim ama numaramı paylaşmak istemiyorum, gruba alır mısınız' benzeri bir şekilde de gruba alınmayı isteyebilirsiniz.");
			
				
			}
			if (callData.contentEquals("TAUADAY")) {
				sendTxt("Aday bilgi formu'nu dolduranlarin siralama listesini gormek icin !adaybilgiformu bilgisayar \n(veya hangi bolumseniz adinin bir kismini) yaziniz...");
				sendTxt("Aday gruplarina erismek icin @tauaday linkini kullanip grubu okuyunuz :)");
				AnswerCallbackQuery acb = new AnswerCallbackQuery()
						.setShowAlert(true)
						.setCallbackQueryId(cbq.getId())
						.setText("Tuşa bastın.")
						.setUrl("telegram.me/turkishgermanbot?start=adaybilgiformu");
			
				try {
					tgb.execute(acb);
				} catch (TelegramApiException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}

			
		} else if (update.hasInlineQuery()) {
			System.out.println(update.getInlineQuery().toString());
		}
		

	}
	 



	private SendMessage preParse(String s) {
		System.out.println(s);
		String[] lines = s.split(System.getProperty("line.separator"));
		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rowsInline = new ArrayList<List<InlineKeyboardButton>>();
		
		for(int i = 0; i < lines.length; i++) {
			List<InlineKeyboardButton> rowInline = new ArrayList<InlineKeyboardButton>();	
			for (int j = 0; j < lines[i].length(); j++) {
				System.out.println(lines[i].charAt(j));
				if (lines[i].charAt(j) == '[') {
					if (j == 0 || lines[i].charAt(j - 1) != '\\'){
							// A link or button starts
						System.out.println(lines[i].substring(j + 1));
							if (lines[i].substring(j + 1).startsWith("button:")) {
								int x = j + 8;
								int y = x + lines[i].substring(j + 8).indexOf(']');
								if ( x == y) { ///< there wasnt any ]
									return null;
								}
								String btnTxt = lines[i].substring(x, y);
								
								if (lines[i].charAt(y + 1) != '(') {
									return null;
								}
								int linkStart = y + 2;
								int linkEnd = linkStart + lines[i].substring(linkStart).indexOf(')');
								String linkTxt = lines[i].substring(linkStart, linkEnd);
								System.out.println(btnTxt);
								System.out.println(linkTxt);
								rowInline.add(new InlineKeyboardButton().setText(btnTxt).setUrl(linkTxt));
								j = linkEnd + 1;
							}
					}
				}
			}
			
			rowsInline.add(rowInline);
		}
		
		
		markupInline.setKeyboard(rowsInline);
		
		SendMessage sMsg = new SendMessage()
				.setChatId(chatId)
				.setText("Mao")
				.setReplyMarkup(markupInline);
		try {
			tgb.execute(sMsg);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			LOGGER.error("Request Failed", e);
		}
		return null;
	}
	
	
	private boolean sendMarkdown(String s) {
		SendMessage sMsg = new SendMessage()
				.setChatId(chatId)
				.setText(s)
				.setParseMode(ParseMode.MARKDOWNV2);
		try {
			tgb.execute(sMsg);
			return true;
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			sendTxt(rb.getString("requestFailed") + ": " + e.toString());
			LOGGER.error("Request Failed", e);
		}
		return false;
	}
	
	private boolean sendHtml(String s) {
		SendMessage sMsg = new SendMessage()
				.setChatId(chatId)
				.setText(s)
				.setParseMode(ParseMode.HTML);
		try {
			tgb.execute(sMsg);
			return true;
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			sendTxt(rb.getString("requestFailed") + ": \n" + HelperFunctions.stackTraceToString(e));
			LOGGER.error("Request Failed", e);
		}
		return false;
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
			//e.printStackTrace();
			LOGGER.error("Request Failed", e);
		}
		return false;
	}
	
	private boolean sendOneButton(String txt, String s, String url) {
		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rowsInline = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> rowInline = new ArrayList<InlineKeyboardButton>();
		InlineKeyboardButton ikb = new InlineKeyboardButton().setText(s).setCallbackData(s);
		
		if (url != null) {
			ikb.setUrl(url);
		}
		rowInline.add(ikb);
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
			//e.printStackTrace();
			sendTxt(rb.getString("requestFailed") + ": \n" + HelperFunctions.stackTraceToString(e));
			LOGGER.error("Request Failed", e);
		}
		return false;
	}
	
	private boolean sendCommands() {
		
		
		for (int i = 0; i < Main.LANGUAGES.length; i++) {
			String lang = Main.LANGUAGES[i];
			String coun = Main.COUNTRIES[i];
			sendTxt(lang.toUpperCase() + ": " + ResourceBundle.getBundle("MessagesBundle", new Locale(lang, coun)).getString("whyHere"));
		}
		
		
		sendTxt("Im still under logical development, hit @batikanor up for ideas.");
		sendOneButton("TR: TAÜ Aday Grupları", "TAUADAY", null);
		sendOneButton("EN: TGU English Chatroom", "TGUENGLISH", "t.me/tguenglish");
		sendOneButton("DE: TDU Deutscher Chatroom", "TDUDEUTSCH", "t.me/tdudeutsch");
		sendOneButton("ALL: TAÜ/TDU/TGU Public Groups", "TAUPUBLIC", "t.me/turkalman");
		sendOneButton("ALL: TAÜ/TDU/TGU Private Groups\n(Only students or other university associates are allowed)", "TAUPRIVATE", null);
		sendOneButton("ALL: Click to see a random number", "Random Number", null);
	
		return true;
		
		
	}
	
	private boolean sendAdayFormResults(List<String> departments) {
	try {
		
		if (departments == null) {
			departments = new ArrayList<String>();
		}
		
		String currentDepartment = null;
		boolean started = false;
		List<AdayFormResults> afrl = SheetsConnection.returnAdayFormResults();
		String toSend = "TAÜ Gayriresmi Aday Bilgi Formu'ndan alınan güncel verilerdir.\nFormu doldurmak veya sıralama tahmini yapmak için @tauaday da yazılanları okuyunuz :)\n";
		//String toSend = "ZAMAN///ALAN///BOLUM///DIGER_BOLUMLER///SIRALAMA///OZEL_KONTENJAN_DURUMU///USTTEKI_BIR_TERCIHE_YERLESME_OLASILIGI///TERCIH_SEBEBI\n";
	
		int j = 0;
		for (int i = 0; i < afrl.size(); i++){
			AdayFormResults afr = afrl.get(i);
			j++;
			if (started == false) {
				started = true;
				departments.add(afr.departmentChoice);
				currentDepartment = afr.departmentChoice;
			}
			else if (!departments.contains(afr.departmentChoice) ) {
				//System.out.println(toSend.length());
				sendTxt(toSend);
				toSend = "\n" + afr.departmentChoice + "\nSIRALAMA : BAŞKA YERE YERLEŞME İHTİMALİ : ÖZEL KONTENJAN DURUMU : TERCİH SEBEBİ\n";
				departments.add(afr.departmentChoice);
				currentDepartment = afr.departmentChoice;
			} else {
				if (!currentDepartment.contentEquals(afr.departmentChoice)) {
					j = 0;
					continue;
					
				}
			}
			//toSend += "\n" + afr.timeStamp + "///" + afr.field + "///" + afr.departmentChoice + "///" + afr.otherChoices + "///" + afr.ranking + "///" + afr.specialQuota + "///" + afr.Unlikeliness + "///" + afr.reason;
			toSend += "\n" + j + ". " + afr.ranking + " : " + afr.Unlikeliness + " : " + afr.specialQuota + " : " + afr.reason.substring(0, 10) + "...";
			
		}
		//System.out.println(toSend.length());
		sendTxt(toSend);
		return true;
	} catch (IOException e) {
		//e.printStackTrace();
		LOGGER.error("Request Failed", e);
	} catch (GeneralSecurityException e) {
		//e.printStackTrace();
		LOGGER.error("Request Failed", e);
	}
	return false;
	
}
	
}

