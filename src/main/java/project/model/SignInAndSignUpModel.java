package project.model;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.CollectionReference;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;

import project.controller.SignInAndSignUpController;
import project.service.FirebaseService;
import project.unit.UserSignInSignUp;
import project.unit.ShowAlert;

import java.util.concurrent.ExecutionException;

public class SignInAndSignUpModel {

    static SignInAndSignUpController ls = new SignInAndSignUpController();

    private static boolean checkFirebaseConnection(Pane ownerPane) {
        if (!FirebaseService.initializeFirebase()) {
            showErrorAlert("Firebase connection failed\nPlease check your connection", ownerPane);
            return true;
        }
        return false;
    }

    public static void registerUser(String username, String email, String password, String role, Pane ownerPane) {
        if (checkFirebaseConnection(ownerPane)) return;

        try {
            Firestore db = FirestoreClient.getFirestore();

            if (isUsernameInCollections(db, username, ownerPane)) {
                showErrorAlert("Username already exists in the system\nPlease choose a different username", ownerPane);
                return;
            }

            if (!isCollectionAvailable(db, role + "s", ownerPane)) {
                showWarningAlert("Collection '" + role + "s' does not exist\nCreating a new one", ownerPane);
            }

            CollectionReference usersRef = db.collection(role + "s");
            UserSignInSignUp newUser = new UserSignInSignUp(username, email, password, role);
            usersRef.document(username).set(newUser).get();

            showInfoAlert(role.toUpperCase() + " registered successfully", ownerPane);

        } catch (InterruptedException | ExecutionException e) {
            showErrorAlert("Error during the registration process\nPlease try again later", ownerPane);
        }
    }

    private static boolean isUsernameInCollections(Firestore db, String username, Pane ownerPane) {
        try {
            DocumentReference adminDocRef = db.collection("admins").document(username);
            if (adminDocRef.get().get().exists()) {
                return true;
            }

            DocumentReference userDocRef = db.collection("users").document(username);
            if (userDocRef.get().get().exists()) {
                return true;
            }

        } catch (InterruptedException | ExecutionException e) {
            showErrorAlert("Error checking username availability\nPlease try again later", ownerPane);
        }
        return false;
    }

    private static boolean isCollectionAvailable(Firestore db, String collectionName, Pane ownerPane) {
        try {
            for (CollectionReference collection : db.listCollections()) {
                if (collection.getId().equalsIgnoreCase(collectionName)) {
                    return true;
                }
            }

        } catch (Exception e) {
            showErrorAlert("Error checking collections\nPlease try again later", ownerPane);
        }
        return false;
    }

    public static void loginUser(String username, String password, Pane ownerPane) {


        if (checkFirebaseConnection(ownerPane)) return;

        try {
            Firestore db = FirestoreClient.getFirestore();

            // Check the "admins" collection
            DocumentReference adminDocRef = db.collection("admins").document(username);
            DocumentSnapshot adminDoc = adminDocRef.get().get();

            if (adminDoc.exists()) {
                String storedPassword = adminDoc.getString("password");
                if (password.equals(storedPassword)) {
                    ShowAlert.showAlert(Alert.AlertType.INFORMATION, "Welcome", "Welcome, " + username + "!\nYou are logged in as Admin", ownerPane, () -> {
                        ls.loginSuccess(true);
                    });
                    return;
                }
            }

            // Check the "users" collection
            DocumentReference userDocRef = db.collection("users").document(username);
            DocumentSnapshot userDoc = userDocRef.get().get();

            if (userDoc.exists()) {
                String storedPassword = userDoc.getString("password");
                if (password.equals(storedPassword)) {
                    ShowAlert.showAlert(Alert.AlertType.INFORMATION, "Welcome", "Welcome, " + username + "!\nYou are logged in as Regular User", ownerPane, () -> {
                        ls.loginSuccess(true);
                    });
                    return;
                }
            }

            ShowAlert.showAlert(Alert.AlertType.ERROR, "Error", "Incorrect username or password\nPlease try again", ownerPane, () -> {
                ls.loginSuccess(false);
            });

        } catch (InterruptedException | ExecutionException e) {
            showErrorAlert("Error during login process\nPlease try again later", ownerPane);
        }
    }

    private static void showErrorAlert(String message, Pane ownerPane) {
        ShowAlert.showAlert(Alert.AlertType.ERROR, "Error", message, ownerPane);
    }

    private static void showWarningAlert(String message, Pane ownerPane) {
        ShowAlert.showAlert(Alert.AlertType.WARNING, "Warning", message, ownerPane);
    }

    private static void showInfoAlert(String message, Pane ownerPane) {
        ShowAlert.showAlert(Alert.AlertType.INFORMATION, "Success", message, ownerPane);
    }

}
