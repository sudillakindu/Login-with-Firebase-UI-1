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
import project.unit.ShowAlert;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;

public class SignInAndSignUpController {

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private Pane mainPane, leftPane, signInPane, signUpPane;

    @FXML
    private ImageView leftPaneExitImage, leftPaneImage, signInImage, stellixorImage, signUpImage;

    @FXML
    private Button signInShowPasswordButton, signInForgotPasswordButton, signInButton, signUpAccountButton;
    @FXML
    private Button signUpButton, signInAccountButton;

    @FXML
    private Label stellixorLabel;

    @FXML
    private TextField signInUsernameTextField, signInPasswordTextField;
    @FXML
    private PasswordField signInPasswordField;

    @FXML
    private TextField signUpUsernameTextField, signUpEmailTextField;
    @FXML
    private PasswordField signUpPasswordTextField;

    @FXML
    private ComboBox<String> signUpSelectRoleComboBox;

    private double xOffset = 0, yOffset = 0;

    @FXML
    private void initialize() {
        signInPane.setVisible(true);
        signUpPane.setVisible(false);

        setImage(leftPaneExitImage, "/images/icons8-exit-50.png");
        setImage(leftPaneImage, "/images/website-designing.png");
        setImage(signInImage, "/images/login-access-vector.png");
        setImage(stellixorImage, "/images/Stellixor-Technologies.png");
        setImage(signUpImage, "/images/online-registration.png");

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

    @FXML
    private void onSignUpButtonClick(ActionEvent event) {
        String username = signUpUsernameTextField.getText();
        String email = signUpEmailTextField.getText();
        String password = signUpPasswordTextField.getText();
        String role = signUpSelectRoleComboBox.getValue();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            ShowAlert.showAlert(Alert.AlertType.WARNING, "Input Error",
                    "All fields are required", signUpPane);
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Root Password");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter root password: ");
        dialog.initOwner(signUpPane.getScene().getWindow());
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String rootpassword = result.get();
            if (rootpassword.equals("aaa")) {
                SignInAndSignUpModel.registerUser(username, email, password, role, signUpPane);

                signUpSelectRoleComboBox.setValue("user");
                signUpUsernameTextField.clear();
                signUpEmailTextField.clear();
                signUpPasswordTextField.clear();
            } else {
                ShowAlert.showAlert(Alert.AlertType.WARNING, "Root Error",
                        "Invalid root password", signUpPane);
            }
        } else {
            ShowAlert.showAlert(Alert.AlertType.WARNING, "Root Error",
                    "Root password input was cancelled", signUpPane);
        }
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

    ////////////////////////////////////////////////////////////////////
}
