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
            ShowAlert.showAlert(Alert.AlertType.ERROR, "Error", "Firebase connection failed\nPlease check your connection", ownerPane);
            return true;
        }
        return false;
    }

    public static void registerUser(String username, String email, String password, String role, Pane ownerPane) {
        if (checkFirebaseConnection(ownerPane)) return;

        try {
            Firestore db = FirestoreClient.getFirestore();

//            if (isUsernameInCollections(db, username, ownerPane)) {
//                ShowAlert.showAlert(Alert.AlertType.ERROR, "Error", "Username already exists in the system\nPlease choose a different username", ownerPane, () -> {
//
//                    System.out.println("aaaaaaaaaaaaaaaaaa");
//                    ls.registerSuccess(false);
//                });
//                return;
//            }
//
//            if (isEmailInCollections(db, email, ownerPane)) {
//                ShowAlert.showAlert(Alert.AlertType.ERROR, "Error", "Email already exists in the system\nPlease choose a different email", ownerPane, () -> {
//
//                    System.out.println("aaaaaaaaaaaaaaaaaa");
//                    ls.registerSuccess(false);
//                });
//                return;
//            }

            if (!isCollectionAvailable(db, role + "s", ownerPane)) {
                ShowAlert.showAlert(Alert.AlertType.WARNING, "Warning", "Collection '" + role + "s' does not exist\nCreating a new one", ownerPane);
            }

            CollectionReference usersRef = db.collection(role + "s");
            UserSignInSignUp newUser = new UserSignInSignUp(username, email, password, role);
            usersRef.document(username).set(newUser).get();

            ShowAlert.showAlert(Alert.AlertType.CONFIRMATION, "Success", role.toUpperCase() + " registered successfully", ownerPane, () -> {
                ls.registerSuccess(true);
            });

        } catch (InterruptedException | ExecutionException e) {
            ShowAlert.showAlert(Alert.AlertType.ERROR, "Error", "Error during the registration process\nPlease try again later", ownerPane);
        }
    }

    public static boolean isCheckUsernameAndEmailAvailable(String username, String email, Pane ownerPane) {
        if (isUsernameInCollections(username, ownerPane)) {
            ShowAlert.showAlert(Alert.AlertType.ERROR, "Error", "Username already exists in the system\nPlease choose a different username", ownerPane);
            return false;
        }

        if (isEmailInCollections(email, ownerPane)) {
            ShowAlert.showAlert(Alert.AlertType.ERROR, "Error", "Email already exists in the system\nPlease choose a different email", ownerPane);
            return false;
        }

        return true;
    }

    private static boolean isUsernameInCollections(String username, Pane ownerPane) {
        try {
            Firestore dbUsername = FirestoreClient.getFirestore();

            DocumentReference adminDocRef = dbUsername.collection("admins").document(username);
            if (adminDocRef.get().get().exists()) {
                return true;
            }

            DocumentReference userDocRef = dbUsername.collection("users").document(username);
            if (userDocRef.get().get().exists()) {
                return true;
            }

        } catch (InterruptedException | ExecutionException e) {
            ShowAlert.showAlert(Alert.AlertType.ERROR, "Error", "Error checking username availability\nPlease try again later", ownerPane);
        }

        return false;
    }

    private static boolean isEmailInCollections(String email, Pane ownerPane) {
        try {
            Firestore dbEmail = FirestoreClient.getFirestore();

            DocumentReference adminDocRef = dbEmail.collection("admins").document(email);
            if (adminDocRef.get().get().exists()) {
                return true;
            }

            DocumentReference userDocRef = dbEmail.collection("users").document(email);
            if (userDocRef.get().get().exists()) {
                return true;
            }

        } catch (InterruptedException | ExecutionException e) {
            ShowAlert.showAlert(Alert.AlertType.ERROR, "Error", "Error checking email availability\nPlease try again later", ownerPane);
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
            ShowAlert.showAlert(Alert.AlertType.ERROR, "Error", "Error checking collections\nPlease try again later", ownerPane);
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
                    ShowAlert.showAlert(Alert.AlertType.CONFIRMATION, "Welcome", "Welcome, " + username + "!\nYou are logged in as Admin", ownerPane, () -> {
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
                    ShowAlert.showAlert(Alert.AlertType.CONFIRMATION, "Welcome", "Welcome, " + username + "!\nYou are logged in as Regular User", ownerPane, () -> {
                        ls.loginSuccess(true);
                    });
                    return;
                }
            }

            ShowAlert.showAlert(Alert.AlertType.ERROR, "Error", "Incorrect username or password\nPlease try again", ownerPane, () -> {
                ls.loginSuccess(false);
            });

        } catch (InterruptedException | ExecutionException e) {
            ShowAlert.showAlert(Alert.AlertType.ERROR, "Error", "Error during login process\nPlease try again later", ownerPane);
        }
    }

    public static boolean findUserEmail(String forgotPassword_Username, String forgotPassword_Email, Pane ownerPane) {
        if (checkFirebaseConnection(ownerPane)) {
            return false;
        }

        try {
            Firestore db = FirestoreClient.getFirestore();

            DocumentReference adminDocRef = db.collection("admins").document(forgotPassword_Username);
            DocumentSnapshot adminDoc = adminDocRef.get().get();

            if (adminDoc.exists()) {
                String storedEmail = adminDoc.getString("email");
                if (forgotPassword_Email.equals(storedEmail)) {
                    return true;
                }
            }

            DocumentReference userDocRef = db.collection("users").document(forgotPassword_Username);
            DocumentSnapshot userDoc = userDocRef.get().get();

            if (userDoc.exists()) {
                String storedEmail = userDoc.getString("email");
                if (forgotPassword_Email.equals(storedEmail)) {
                    return true;
                }
            }

            ShowAlert.showAlert(Alert.AlertType.ERROR, "Error", "Username or Email does not match\nOTP code was not sent to the email address", ownerPane);
            return false;

        } catch (InterruptedException | ExecutionException e) {
            ShowAlert.showAlert(Alert.AlertType.ERROR, "Error", "Error during login process\nPlease try again later", ownerPane);
            return false;
        }
    }

    public static boolean resetUserPassword(String forgotPassword_Username, String forgotPassword_NewPassword, Pane ownerPane) {
        if (checkFirebaseConnection(ownerPane)) {
            return false;
        }

        try {
            Firestore db = FirestoreClient.getFirestore();

            // Check if the user exists in the "admins" collection
            DocumentReference adminDocRef = db.collection("admins").document(forgotPassword_Username);
            DocumentSnapshot adminDoc = adminDocRef.get().get();

            if (adminDoc.exists()) {
                adminDocRef.update("password", forgotPassword_NewPassword);
                ShowAlert.showAlert(Alert.AlertType.CONFIRMATION, "Success", "Password has been successfully updated", ownerPane);
                return true;
            }

            // Check if the user exists in the "users" collection
            DocumentReference userDocRef = db.collection("users").document(forgotPassword_Username);
            DocumentSnapshot userDoc = userDocRef.get().get();

            if (userDoc.exists()) {
                userDocRef.update("password", forgotPassword_NewPassword);
                ShowAlert.showAlert(Alert.AlertType.CONFIRMATION, "Success", "Password has been successfully updated", ownerPane);
                return true;
            }

            ShowAlert.showAlert(Alert.AlertType.ERROR, "Error", "Username does not exist\nPassword was not updated", ownerPane);
            return false;

        } catch (InterruptedException | ExecutionException e) {
            ShowAlert.showAlert(Alert.AlertType.ERROR, "Error", "Error during password reset process\nPlease try again later", ownerPane);
            return false;
        }
    }

}
