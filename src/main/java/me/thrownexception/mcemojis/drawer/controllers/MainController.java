package me.thrownexception.mcemojis.drawer.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.stage.Window;
import me.thrownexception.mcemojis.drawer.models.HintProvider;
import me.thrownexception.mcemojis.drawer.views.EmojiTileFactory;
import java.io.InputStreamReader;

public class MainController {
    @FXML
    private ImageView mainImage;
    @FXML
    private ImageView drag;
    @FXML
    private ImageView resize;
    @FXML
    private ImageView close;
    @FXML
    private ImageView send;
    @FXML
    private VBox drawer;
    @FXML
    private TilePane tiles;
    @FXML
    private HBox buttonBar;
    @FXML
    private Label hint;
    @FXML
    private Label hoverDesc;
    @FXML
    private TextField search;
    private final double MAX_SIZE = 64;
    private final double MIN_SIZE = 12;
    private final int DRAWER_TILES_HEIGHT = 4;
    private final int DRAWER_TILES_WIDTH = 12;
    private DoubleProperty emojiSize = new SimpleDoubleProperty(24);
    private double dragX;
    private double dragY;
    private double resizeY;
    private double resizeSize;
    private double oldStageHeight = 0;
    private final Robot ROBOT = new Robot();

    public MainController() {
        Platform.runLater(() -> {
            mainImage.fitHeightProperty().bind(emojiSize);
            mainImage.fitWidthProperty().bind(emojiSize);
            drag.fitHeightProperty().bind(emojiSize);
            drag.fitWidthProperty().bind(emojiSize);
            resize.fitHeightProperty().bind(emojiSize);
            resize.fitWidthProperty().bind(emojiSize);
            close.fitHeightProperty().bind(emojiSize);
            close.fitWidthProperty().bind(emojiSize);
            send.fitHeightProperty().bind(emojiSize);
            send.fitWidthProperty().bind(emojiSize);
            drawer.managedProperty().bind(drawer.visibleProperty());
            updateWindowSize();
            hint.setText(HintProvider.next());

            close.setOnMouseClicked(e -> ((Stage)close.getScene().getWindow()).close());
            drag.setOnMousePressed(e -> {
                dragX = e.getSceneX();
                dragY = e.getSceneY();
            });
            drag.setOnMouseDragged(e -> {
                Stage stage = (Stage)drag.getScene().getWindow();
                stage.setX(e.getScreenX() - dragX);
                stage.setY(e.getScreenY() - dragY);
            });
            resize.setOnMousePressed(e -> {
                resizeY = e.getScreenY();
                resizeSize = emojiSize.get();
            });
            resize.setOnMouseDragged(e -> {
                double sizeChange = resizeY - e.getScreenY();
                if (resizeSize + sizeChange > MAX_SIZE || resizeSize + sizeChange < MIN_SIZE) return;
                emojiSize.set(resizeSize + sizeChange);
                updateWindowSize();
            });
            mainImage.setOnMouseClicked(e -> toggleDrawer());
            send.setOnMouseClicked(e -> {
                sendEnter();
                toggleDrawer();
            });
            search.setOnKeyTyped(e -> EmojiTileFactory.filter(tiles, search.getText()));
            Gson gson = new GsonBuilder().create();
            JsonArray data = gson.fromJson(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("assets/drawer-data.json")), JsonArray.class);

            new Thread(() -> EmojiTileFactory.createTiles(tiles, data, hoverDesc, emojiSize)).start();
        });
    }

    private void sendEnter() {
        Window window = send.getScene().getWindow();
        //Move Below application
        Point2D oldPos = ROBOT.getMousePosition();
        ROBOT.mouseMove(window.getX() + window.getWidth() + 5, window.getY());
        ROBOT.mouseClick(MouseButton.PRIMARY);
        ROBOT.keyType(KeyCode.ENTER);
    }

    private void toggleDrawer() {
        drawer.setVisible(!drawer.isVisible());
        buttonBar.setVisible(!buttonBar.isVisible());
        updateWindowSize();
        if (drawer.isVisible()) {
            hint.setText(HintProvider.next());
        }
    }

    private void updateWindowSize() {
        double spacing = 8;
        double margin = 4; // Around entire window
        double scrollbarWidth = 24;
        double otherHeight = 82;
        double height = emojiSize.get() + (2 * margin);
        if (drawer.isVisible()) height += (emojiSize.get() + spacing) * DRAWER_TILES_HEIGHT + otherHeight;

        double width = emojiSize.get() + (margin * 2);
        if (drawer.isVisible()) {
            width += (emojiSize.get() + spacing) * (DRAWER_TILES_WIDTH-1);
            width += scrollbarWidth + (2*margin);
        }
        Stage stage = (Stage) buttonBar.getScene().getWindow();
        if (oldStageHeight != 0) {
            double increase = height - oldStageHeight;
            stage.setY(stage.getY() - increase);
        }
        stage.setHeight(height);
        stage.setWidth(width);
        oldStageHeight = height;
    }
}
