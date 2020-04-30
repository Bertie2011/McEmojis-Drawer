package me.thrownexception.mcemojis.drawer.views;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class ErrorHandler {
    public static void handle(Exception e) {
        e.printStackTrace();
        Platform.runLater(() -> {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        });
    }
}
