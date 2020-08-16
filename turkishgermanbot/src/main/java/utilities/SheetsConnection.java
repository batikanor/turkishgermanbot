package utilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import objects.AdayFormResults;
import turkishgermanbot.Main;

public class SheetsConnection {
	public static Sheets sheetsService;

	
	
	private static Credential authorize() throws IOException, GeneralSecurityException{
		InputStream in = SheetsConnection.class.getResourceAsStream("/SECRET_KEYS/sheets_credentials.json");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
				JacksonFactory.getDefaultInstance(), new InputStreamReader(in)
				);
		List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);
		
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
				clientSecrets, scopes)
				.setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
				.setAccessType("offline")
				.build();
		Credential credential = new AuthorizationCodeInstalledApp(
				flow, new LocalServerReceiver())
				.authorize("user");
		return credential;
				
	}

	public static Sheets getSheetsService() throws IOException, GeneralSecurityException{
		Credential credential = authorize();
		return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
				JacksonFactory.getDefaultInstance(), credential)
				.setApplicationName(Main.APPLICATION_NAME)
				.build();
	}
	
	public static List<AdayFormResults> returnAdayFormResults() throws IOException, GeneralSecurityException {
		String timeStamp, field, departmentChoice, otherChoices, ranking, specialQuota, unlikeliness, reason;
		List<AdayFormResults> afrl = new ArrayList<AdayFormResults>();
		SheetsConnection.sheetsService = SheetsConnection.getSheetsService();
		String range = "FormResponses!A1:I600";
		ValueRange response = sheetsService.spreadsheets().values()
				.get(Main.SPREADSHEET_ID, range)
				.execute();
		List<List<Object>> values = response.getValues();
		
		if (values == null || values.isEmpty()) {
			System.out.println("No data found!");
			return null;
		} else {
			
			for(List<?> row : values) {
				if (row.isEmpty()) {
					break;
				}
				timeStamp = (String) row.get(0);
				field = (String) row.get(2);
				departmentChoice = (String) row.get(3);
				otherChoices = (String) row.get(4);
				ranking = (String) row.get(5);
				specialQuota = (String) row.get(6);
				unlikeliness = (String) row.get(7);
				reason = (String) row.get(8);
				afrl.add(new AdayFormResults(
						timeStamp, field, departmentChoice, otherChoices, ranking, specialQuota, unlikeliness, reason
						));
				
				
			}
			return afrl;
		}
				
	
	}


}
