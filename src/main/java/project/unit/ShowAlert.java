package project.unit;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ShowAlert {

    private static StackPane createAlertPane(Pane ownerPane) {
        StackPane alertPane = new StackPane();
        alertPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        alertPane.setPrefSize(ownerPane.getWidth(), ownerPane.getHeight());
        return alertPane;
    }

    private static VBox createAlertContent(String title, String content, Image icon) {
        VBox alertContent = new VBox(10);
        alertContent.setAlignment(Pos.CENTER);
        alertContent.setPadding(new Insets(20));
        alertContent.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 5px;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-font-size: 16px; -fx-alignment: center");

        ImageView iconView = new ImageView(icon);
        iconView.setFitWidth(50);
        iconView.setFitHeight(50);
        iconView.setPreserveRatio(true);

        alertContent.getChildren().addAll(iconView, titleLabel, contentLabel);
        return alertContent;
    }

    private static Button createOkButton(Pane ownerPane, StackPane alertPane, Runnable onOkAction) {
        Button okButton = new Button("OK");
        okButton.setStyle(
                "-fx-background-color: rgba(76,175,80,0.85); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 5px 50px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );
        okButton.setOnMouseEntered(e -> okButton.setStyle(
                "-fx-background-color: #45a049; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 5px 50px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        ));
        okButton.setOnMouseExited(e -> okButton.setStyle(
                "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 5px 50px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        ));

        okButton.setOnAction(event -> {
            ownerPane.getChildren().remove(alertPane);
            if (onOkAction != null) {
                onOkAction.run();  // Call the callback method after the alert is closed
            }
        });

        return okButton;
    }

    public static void showAlert(Alert.AlertType alertType, String title, String content, Pane ownerPane) {
        StackPane alertPane = createAlertPane(ownerPane);

        Image icon = getAlertIcon(alertType);
        VBox alertContent = createAlertContent(title, content, icon);

        Button okButton = createOkButton(ownerPane, alertPane, null);
        alertContent.getChildren().add(okButton);

        alertPane.getChildren().add(alertContent);
        StackPane.setAlignment(alertContent, Pos.CENTER);

        ownerPane.getChildren().add(alertPane);
    }

    public static void showAlert(Alert.AlertType alertType, String title, String content, Pane ownerPane, Runnable onOkAction) {
        StackPane alertPane = createAlertPane(ownerPane);

        Image icon = getAlertIcon(alertType);
        VBox alertContent = createAlertContent(title, content, icon);

        Button okButton = createOkButton(ownerPane, alertPane, onOkAction);
        alertContent.getChildren().add(okButton);

        alertPane.getChildren().add(alertContent);
        StackPane.setAlignment(alertContent, Pos.CENTER);

        ownerPane.getChildren().add(alertPane);
    }

    private static Image getAlertIcon(Alert.AlertType alertType) {
        String iconPath = null;
        switch (alertType) {
            case ERROR:
                iconPath = "/images/icons-error-50.png";
                break;
            case WARNING:
                iconPath = "/images/icons-warning-50.png";
                break;
            case INFORMATION:
                iconPath = "/images/icons-information-50.png";
                break;
            case CONFIRMATION:
                iconPath = "/images/icons-verified-account-50.png";
                break;
            default:
                return null;
        }
        return new Image(iconPath);
    }
}
