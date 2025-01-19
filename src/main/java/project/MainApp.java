package project;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.io.InputStream;
import java.util.Objects;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {

            checkConnection();

            checkFirebase();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml-view/SignInAndSignUp-View.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 1024, 768);
            primaryStage.setScene(scene);
            primaryStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            primaryStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
            setApplicationIcon(primaryStage);
            primaryStage.centerOnScreen();
            primaryStage.show();
            startFadeOutTransition(primaryStage, scene);

        } catch (Exception e) {
            showMessageDialog("Start FXML File Error", "Error: " + e.getMessage());
            Platform.exit();
            //e.printStackTrace();
        }
    }

    private boolean wasDisconnected = false;

    private void checkConnection() {
        Timer timer = new Timer(3000, e -> {

            boolean isConnected = InternetUtility.isConnectedToInternet();

            if (!isConnected) {
                    JOptionPane.showMessageDialog(
                            null,
                            "No Internet Connection Detected!\nPlease check your connection",
                            "Connection Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    wasDisconnected = true;
            } else {
                if (wasDisconnected) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Internet Connection Restored!",
                            "Connection Restored",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    wasDisconnected = false;
                }
            }
        });

        timer.start();
    }

    private void checkFirebase() {
        boolean isFirebaseInitialized = FirebaseService.initializeFirebase();

        if (!isFirebaseInitialized) {
            showMessageDialog("Firebase Error", "Failed to connect to Firebase.");
        }
    }

    private Parent loadFXML() {
        try {
            return FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml-view/SignInAndSignUp-View.fxml")));
        } catch (Exception e) {
            showMessageDialog("Load FXML Error", "Error: " + e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    private void setApplicationIcon(Stage primaryStage) {
        try (InputStream imageStream = getClass().getResourceAsStream("/images/sporting-event-ticket-vector.png")) {
            if (imageStream != null) {
                primaryStage.getIcons().add(new Image(imageStream));
            }
        } catch (Exception e) {
            showMessageDialog("Application Icon Error", "Error: " + e.getMessage());
            //e.printStackTrace();
        }
    }

    private void startFadeOutTransition(Stage primaryStage, Scene initialScene) {
        try {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), initialScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                Parent newLoginForm = loadFXML();
                if (newLoginForm != null) {
                    primaryStage.setScene(new Scene(newLoginForm));
                    primaryStage.setTitle("EMwF Login");
                    startFadeInTransition(newLoginForm);
                }
            });
            fadeOut.play();
        } catch (Exception e) {
            showMessageDialog("FadeOut Transition Error", "Error: " + e.getMessage() + "\n\n" + "Error loading FXML file.");
        }
    }

    private void startFadeInTransition(Parent root) {
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void showMessageDialog(String title, String content) {
        JOptionPane.showMessageDialog(
                null,
                content,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }
}
