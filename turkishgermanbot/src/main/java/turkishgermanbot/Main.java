package turkishgermanbot;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

//import org.telegram.telegrambots.meta.generics.BotSession;


public class Main {
	public static final String[] LANGUAGES = { "tr", "en", "de" };
	public static final String[] COUNTRIES = { "TR", "US", "DE" };
	public static final String[] DEPARTMENTS = { "Bilgisayar Mühendisliği", "Elektrik-Elektronik Mühendisliği",
			"Endüstri Mühendisliği", "Hukuk",
			"İktisat", "İnşaat Mühendisliği", "İşletme", "Kültür ve İletişim Bilimleri", "Makine Mühendisliği",
			"Malzeme Bilimi ve Teknolojileri", "Moleküler Biyoteknoloji", "Siyaset ve Uluslararası İlişkiler", "Mekatronik Mühendisliği"};
	
	
	
	public static String botToken;
	public static String botUsername;
	public static long batikansChatId;

	public static String dbDriver;
    public static String dbUser;
    public static String dbPwd;
    public static String dbUrl;
    
    public static String SPREADSHEET_ID;
    public static String APPLICATION_NAME;
    
	private static final int THREAD_PER_CORE = 10; /// < If the bot becomes widely used, maybe turn this into * 1000

	// Instantiate TelegramBotsApi and register the bot
	public static void main(String[] args) throws IOException {

		int numCores = Runtime.getRuntime().availableProcessors();
		int numThreads = numCores * THREAD_PER_CORE; /// < // We still need to have more threads working, because most
														/// of these threads just may be waiting for IO, so out cores
														/// may remain inactive...
		// ^Because these are not CPU INTENSIVE tasks, these are IO INTENSIVE tasks.

		System.out.println("You are running this code on a system with " + numCores + " cores");
		
		String keysFileLoc = "src/main/resources/SECRET_KEYS/Keys.properties";
		FileInputStream fis = new FileInputStream(keysFileLoc);
		Properties prop = new Properties();
		prop.load(fis);
		System.out.println(prop.getProperty("name"));
	  
		
		// If you are helping develop this bot, please ensure that you do not share the following private variables with anyone! (the file including them is to be always ignored while pushing!)
		botToken = prop.getProperty("botToken");
		botUsername = prop.getProperty("botUsername");///< Without '@'
		batikansChatId = Long.parseLong(prop.getProperty("batikansChatId")); ///< For testing purposes
		
		dbDriver = prop.getProperty("dbDriver");
		dbUser = prop.getProperty("dbUser");
		dbPwd = prop.getProperty("dbPwd");
		dbUrl = prop.getProperty("dbUrl");
	   
		SPREADSHEET_ID = prop.getProperty("SPREADSHEET_ID");
		APPLICATION_NAME = prop.getProperty("APPLICATION_NAME");
		
		
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
