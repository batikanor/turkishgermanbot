package utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.hsqldb.util.DatabaseManagerSwing;


public class RunDatabaseGUI {
	public static void main(String[] args) throws IOException {
		
		String keysFileLoc = "src/main/resources/SECRET_KEYS/Keys.properties";
		FileInputStream fis = new FileInputStream(keysFileLoc);
		Properties prop = new Properties();
		prop.load(fis);
		String dbUrl = prop.getProperty("dbUrl");
		String dbUser = prop.getProperty("dbUser");
		String dbPwd = prop.getProperty("dbPwd");
		
		
		System.out.println("Running manager...");
		

		DatabaseManagerSwing.main(new String[] {
				"--user", dbUser, "--password", dbPwd, "--url", dbUrl, "--noexit"
		});
	}
}
