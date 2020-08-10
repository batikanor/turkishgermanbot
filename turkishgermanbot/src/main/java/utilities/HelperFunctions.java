package utilities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class HelperFunctions {
	public static byte[] hashPassword(String password){
		
		//SecureRandom random = new SecureRandom();
		//byte[] salt = new byte[16]; 
		//random.nextBytes(salt);
		//for (byte x : salt) {
		//	System.out.print(x + ", "); ///< 91, -49, -81, 99, 31, 51, -13, 11, 8, 9, 94, 65, -84, 70, 39, -50
		//}
		
		// Also an option: do not hard-code the salt, but make it a separate file, which can be changed if they wish to disable access to all previous logins.
		byte[] salt = {91, -49, -81, 99, 31, 51, -13, 11, 8, 9, 94, 65, -84, 70, 39, -50};
		
		// Creation of a PBEKeySpec and a SecretKeyFactory and instantiation with PBKDF2WithHmacSHA1 algorithm
		// Third argument of the function below (65536 : binary 10000000000000000) is  the strength parameter. It indicates how many iterations this algorithm runs for, increasing the time it takes to produce hash.
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
		SecretKeyFactory factory = null;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();		
		}
		byte[] hash = null;
		try {
			hash = factory.generateSecret(spec).getEncoded();
			
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return hash;
		
	}
	
	public static String stackTraceToString(Exception e) {
		StringWriter strw = new StringWriter();
		PrintWriter printw = new PrintWriter(strw);
		e.printStackTrace(printw);
		String str = strw.toString(); ///< stack trace as a string
		return str;
	}
}
