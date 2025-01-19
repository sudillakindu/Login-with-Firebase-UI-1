package project.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class InternetUtility {

    public static boolean isConnectedToInternet() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://www.google.com").openConnection();
            connection.setConnectTimeout(5000);
            connection.connect();
            return connection.getResponseCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }

}