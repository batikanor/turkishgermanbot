package utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.hsqldb.util.DatabaseManagerSwing;


public class RunDatabaseGUI {
	public static void main(String[] args) throws IOException {

		InputStream ins = ClassLoader.getSystemResourceAsStream("SECRET_KEYS/Keys.properties");
	
		Properties prop = new Properties();
		
		prop.load(ins);
		

		String dbUrl = prop.getProperty("dbUrl");
		String dbUser = prop.getProperty("dbUser");
		String dbPwd = prop.getProperty("dbPwd");
		
		
		System.out.println("Running manager...");
		

		DatabaseManagerSwing.main(new String[] {
				"--user", dbUser, "--password", dbPwd, "--url", dbUrl, "--noexit"
		});
	}
}
