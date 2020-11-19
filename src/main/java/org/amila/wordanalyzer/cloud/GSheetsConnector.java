package org.amila.wordanalyzer.cloud;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.amila.wordanalyzer.Analyzer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GSheetsConnector {
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GSheetsConnector.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Collects verbs from spreadsheet:
     */
    public static Set<String> getVerbs() throws Analyzer.AnalyzerException {
        try {
            Sheets sheetService = getSheetsService();

            final String spreadsheetId = "sid";

//        final String range = "SheetName Data!A2:E";
            final String rangeW = "Weak!A7:A";
            ValueRange responseW = sheetService.spreadsheets().values()
                    .get(spreadsheetId, rangeW)
                    .execute();
            List<List<Object>> valuesW = responseW.getValues();
            Set<String> weakVerbs =  valuesW.stream().map(e -> e.get(0).toString().trim()).collect(Collectors.toSet());
            final String rangeS = "Strong!A7:A";
            ValueRange responseS = sheetService.spreadsheets().values()
                    .get(spreadsheetId, rangeS)
                    .execute();
            List<List<Object>> valuesS = responseS.getValues();
            Set<String> strongVerbs =  valuesS.stream().map(e -> e.get(0).toString().trim()).collect(Collectors.toSet());
            final String rangeM = "Mixed!A7:A";
            ValueRange responseM = sheetService.spreadsheets().values()
                    .get(spreadsheetId, rangeM)
                    .execute();
            List<List<Object>> valuesM = responseM.getValues();
            Set<String> mixedVerbs =  valuesM.stream().map(e -> e.get(0).toString().trim()).collect(Collectors.toSet());

            Set<String> irregularVerbs = Stream.concat(strongVerbs.stream(), mixedVerbs.stream()).collect(Collectors.toSet());
            return Stream.concat(weakVerbs.stream(), irregularVerbs.stream()).collect(Collectors.toSet());
        } catch (Exception e) {
            throw new Analyzer.AnalyzerException("Error retrieving mastered words", e);
        }

    }

    private static Sheets getSheetsService() throws GeneralSecurityException, IOException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();


        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private static void createSpreadSheet(Sheets sheetService) throws IOException {
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties()
                        .setTitle("WordAnalyzer"));
        spreadsheet = sheetService.spreadsheets().create(spreadsheet)
                .setFields("spreadsheetId")
                .execute();
//        Spreadsheet spreadsheet = sheetService.spreadsheets().get(spreadsheetId).execute();
        System.out.println("Spreadsheet ID: " + spreadsheet.getSpreadsheetId());
    }
}