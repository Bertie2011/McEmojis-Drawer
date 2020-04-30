package me.thrownexception.mcemojis.drawer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.thrownexception.mcemojis.drawer.controllers.MainController;

import java.io.IOException;

public class MainApp extends Application {
    private MainController controller;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/Main.fxml"));
        Scene scene = new Scene(loader.load(), 112, 28);
        controller = loader.getController();
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setWidth(200);
    }
}
