package project.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseService {

    public static boolean initializeFirebase() {
        String filePath = "src/main/resources/firebaseKey/serviceAccountKey.json";
        File file = new File(filePath);

        if (!file.exists() || !file.isFile()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Service account file not found or invalid at:\n" + file.getAbsolutePath(),
                    "File Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        try (FileInputStream serviceAccount = new FileInputStream(file)) {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("************** Firebase Connected **************");
                return true;
            } else {
                System.out.println("--- Firebase is already initialized ---");
                return true;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Failed to load Firebase configuration file\nCheck file permissions or path\n" + "Error: " + e.getMessage(),
                    "Firebase Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Unexpected error during Firebase initialization\n" + "Error: " + e.getMessage(),
                    "Unexpected Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
    }
}
