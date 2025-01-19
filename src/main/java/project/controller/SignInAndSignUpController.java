package project.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import project.model.SignInAndSignUpModel;
import project.service.EmailOTPSender;
import project.unit.OTPGenerator;
import project.unit.ShowAlert;
import project.unit.UserSignInSignUp;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInAndSignUpController {

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private Pane mainPane, leftPane, signInPane, signUpPane, verificationCenterPane;

    @FXML
    private ImageView leftPaneExitImage, leftPaneImage, signInImage, stellixorImage, signUpImage, verificationCenterImage, verificationCenterBackImage;

    @FXML
    private Button signInShowPasswordButton, signInForgotPasswordButton, signInButton, signUpAccountButton;
    @FXML
    private Button signUpButton, signInAccountButton;
    @FXML
    private Button sendOTPButton, submitButton;

    @FXML
    private Label stellixorLabel;

    @FXML
    private TextField signInUsernameTextField, signInPasswordTextField;
    @FXML
    private PasswordField signInPasswordField;

    @FXML
    private TextField signUpUsernameTextField, signUpEmailTextField, signUpPasswordTextField;

    @FXML
    private TextField verificationCenterRootPasswordTextField, verificationCenterYourEmailTextField, verificationCenterOTPCodeTextField;

    @FXML
    private ComboBox<String> signUpSelectRoleComboBox;

    private double xOffset = 0, yOffset = 0;

    @FXML
    private void initialize() {
        mainPane.setVisible(true);
        leftPane.setVisible(true);
        signInPane.setVisible(true);
        signUpPane.setVisible(false);
        verificationCenterPane.setVisible(false);

        setImage(leftPaneExitImage, "/images/icons8-exit-50.png");
        setImage(leftPaneImage, "/images/website-designing.png");
        setImage(signInImage, "/images/login-access-vector.png");
        setImage(stellixorImage, "/images/Stellixor-Technologies.png");
        setImage(signUpImage, "/images/online-registration.png");
        setImage(verificationCenterBackImage, "/images/icons-back-50.png");
        setImage(verificationCenterImage, "/images/a-code-verification-vector.png");

        signUpSelectRoleComboBox.getItems().addAll("admin", "user");

        addDragFunctionality();
        AutomaticallyConvert_Uppercase_LowerCase();
    }

    private void setImage(ImageView imageView, String imagePath) {
        try {
            imageView.setImage(new Image(Objects.requireNonNull(getClass().getResource(imagePath)).toExternalForm()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error loading image:\n" + imagePath,
                    "Image Loading Error",
                    JOptionPane.WARNING_MESSAGE
            );
            //e.printStackTrace();
        }
    }

    private void addDragFunctionality() {
        mainPane.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        mainPane.setOnMouseDragged(event -> {
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    @FXML
    private void onLeftPaneExitImageMouseClicked(MouseEvent mouseEvent) {
        onLeftPaneExitImageClick();
    }

    private void onLeftPaneExitImageClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to exit?");
        alert.getDialogPane().setGraphic(null);

        alert.initOwner(mainPane.getScene().getWindow());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Platform.exit();
            }
        });
    }

    private void AutomaticallyConvert_Uppercase_LowerCase() {
        signInUsernameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            signInUsernameTextField.setText(newValue.toLowerCase());
        });

        signUpUsernameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            signUpUsernameTextField.setText(newValue.toLowerCase());
        });
    }

    ////////////////////////////// Sign In //////////////////////////////

    @FXML
    private void onSignInShowPasswordButtonClick(ActionEvent event) {
        if (signInPasswordField.isVisible()) {
            signInPasswordTextField.setText(signInPasswordField.getText());
            signInPasswordTextField.setVisible(true);
            signInPasswordTextField.setManaged(true);
            signInPasswordField.setVisible(false);
            signInPasswordField.setManaged(false);
            signInShowPasswordButton.setText("Hide");
        } else {
            signInPasswordField.setText(signInPasswordTextField.getText());
            signInPasswordField.setVisible(true);
            signInPasswordField.setManaged(true);
            signInPasswordTextField.setVisible(false);
            signInPasswordTextField.setManaged(false);
            signInShowPasswordButton.setText("Show");
        }
    }

    @FXML
    private void onSignInMouseClicked(MouseEvent event) {
        onSignInButtonClick();
    }

    private void onSignInButtonClick() {
        String username = signInUsernameTextField.getText();
        String password = signInPasswordField.isVisible() ? signInPasswordField.getText() : signInPasswordTextField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            ShowAlert.showAlert(Alert.AlertType.WARNING, "Input Error",
                    "Username and password cannot be empty", signInPane);
        } else {
            SignInAndSignUpModel.loginUser(username, password, signInPane);

            signInUsernameTextField.clear();
            signInPasswordTextField.clear();
            signInPasswordField.clear();
        }
    }

    public void loginSuccess(boolean isLoginSuccess) {
        if (isLoginSuccess) {
            System.out.println("Handle success login here");
        } else {
            System.out.println("Handle failed login here");
        }
    }

    @FXML
    private void onSignUpAccountButtonClick(ActionEvent event) {
        signUpSelectRoleComboBox.setValue("user");

        signInPane.setVisible(false);
        signUpPane.setVisible(true);
    }

    ////////////////////////////// Sign Up //////////////////////////////

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private String username = null;
    private String email = null;
    private String password = null;
    private String role = null;

    @FXML
    private void onSignUpButtonClick(ActionEvent event) {
        username = signUpUsernameTextField.getText();
        email = signUpEmailTextField.getText();
        password = signUpPasswordTextField.getText();
        role = signUpSelectRoleComboBox.getValue();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            ShowAlert.showAlert(Alert.AlertType.WARNING, "Input Error",
                    "All fields are required", signUpPane);
            return;
        }

        // Validate email format
        /*if (!isValidEmail(email)) {
            ShowAlert.showAlert(Alert.AlertType.WARNING, "Invalid Email",
                    "Please enter a valid email address", signUpPane);
            return;
        }*/

        verificationCenterRootPasswordTextField.setDisable(false);
        verificationCenterYourEmailTextField.setDisable(true);
        verificationCenterOTPCodeTextField.clear();

        verificationCenterYourEmailTextField.setText(email);

        sendOTPButton.setDisable(false);
        submitButton.setDisable(true);

        signUpPane.setVisible(false);
        verificationCenterPane.setVisible(true);
    }

    @FXML
    private void onSignInAccountButtonClick(ActionEvent event) {
        signUpPane.setVisible(false);
        signInPane.setVisible(true);
    }

    @FXML
    private void onStellixorImageMouseClicked(MouseEvent event) {
        try {
            URI uri = new URI("https://www.facebook.com/stellixortechnologies");

            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(uri);
            }
        } catch (Exception e) {
            ShowAlert.showAlert(Alert.AlertType.WARNING, "Root Error",
                    "Root password input was cancelled", signUpPane);
            //e.printStackTrace();
        }
    }

    ////////////////////////////// Verification Center //////////////////////////////

    @FXML
    private void onVerificationCenterBackImageImageMouseClicked(MouseEvent mouseEvent) {
        verificationCenterPane.setVisible(false);
        signUpPane.setVisible(true);
        verificationCenterRootPasswordTextField.clear();
        verificationCenterOTPCodeTextField.clear();
    }

    private String rootPassword = null;
    private String recipientEmail = null;
    private String generateOTP = null;

    @FXML
    private void onSendOTPButtonClick(ActionEvent event) {
        rootPassword = verificationCenterRootPasswordTextField.getText();
        recipientEmail = verificationCenterYourEmailTextField.getText();

        if (rootPassword.isEmpty() || recipientEmail.isEmpty()) {
            ShowAlert.showAlert(Alert.AlertType.WARNING, "Input Error",
                    "Root password field is required", verificationCenterPane); // verificationCenterPane
            return;
        }

        if (rootPassword.equals("aaa")) {
            generateOTP = OTPGenerator.generateNumericOTP(6);
            System.out.println("Generated OTP: " + generateOTP);

            boolean isOTPSent = EmailOTPSender.sendEmail(recipientEmail, generateOTP);

            if (isOTPSent) {
                sendOTPButton.setDisable(true);
                submitButton.setDisable(false);
                verificationCenterRootPasswordTextField.setDisable(true);
            } else {
            ShowAlert.showAlert(Alert.AlertType.ERROR, "Email Error",
                    "Failed to send OTP\nPlease try again later", verificationCenterPane);
            }

        } else {
            ShowAlert.showAlert(Alert.AlertType.WARNING, "Root Error",
                    "Invalid root password", verificationCenterPane);
        }
    }

    @FXML
    private void onSubmitButtonClick(ActionEvent event) {
        String enteredOTP = verificationCenterOTPCodeTextField.getText();

        if (rootPassword.isEmpty() || recipientEmail.isEmpty() || enteredOTP.isEmpty()) {
            ShowAlert.showAlert(Alert.AlertType.WARNING, "Input Error",
                    "OTP code field is required", verificationCenterPane); // verificationCenterPane
            return;
        }

        System.out.println("Generated OTP SubmitButton: " + generateOTP);
        if (enteredOTP.equals(generateOTP)) {

            System.out.println("Generated OTP Eq : " + generateOTP);
            System.out.println(username + " || " + email + " || " + password + " || " + role);

            submitButton.setDisable(true);

            SignInAndSignUpModel.registerUser(username, email, password, role, signUpPane);

            signUpSelectRoleComboBox.setValue("user");
            signUpUsernameTextField.clear();
            signUpEmailTextField.clear();
            signUpPasswordTextField.clear();

            verificationCenterRootPasswordTextField.clear();
            verificationCenterYourEmailTextField.clear();
            verificationCenterOTPCodeTextField.clear();

            verificationCenterPane.setVisible(false);
            signUpPane.setVisible(true);
        } else {
            ShowAlert.showAlert(Alert.AlertType.WARNING, "OTP Error",
                    "Incorrect OTP. Please try again", verificationCenterPane);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////
}
