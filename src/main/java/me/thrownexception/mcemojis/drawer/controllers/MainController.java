package me.thrownexception.mcemojis.drawer.controllers;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    private HBox buttonBar;
    @FXML
    private VBox mainContainer;
    @FXML
    private ScrollPane drawer;
    private final double MAX_SIZE = 64;
    private final double MIN_SIZE = 12;
    private final int DRAWER_TILES_HEIGHT = 4;
    private final int DRAWER_TILES_WIDTH = 8;
    private DoubleProperty emojiSize = new SimpleDoubleProperty(24);
    private double dragX;
    private double dragY;
    private double resizeY;
    private double resizeSize;
    private double resizeStageY;

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
            drawer.managedProperty().bind(drawer.visibleProperty());
            buttonBar.prefHeightProperty().bind(mainContainer.heightProperty().subtract(4));
            updateWindowSize();

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
                resizeStageY = drag.getScene().getWindow().getY();
                resizeSize = emojiSize.get();
            });
            resize.setOnMouseDragged(e -> {
                Stage stage = (Stage)drag.getScene().getWindow();
                double sizeChange = resizeY - e.getScreenY();
                if (resizeSize + sizeChange > MAX_SIZE || resizeSize + sizeChange < MIN_SIZE) return;
                emojiSize.set(resizeSize + sizeChange);
                updateWindowSize();
            });
            mainImage.setOnMouseClicked(e -> {
                drawer.setVisible(!drawer.isVisible());
                updateWindowSize();
            });
        });
    }

    private void updateWindowSize() {
        double spacing = 8;
        double margin = 4; // Around entire window
        double minimizedTilesWidth = 4;
        double height = emojiSize.get() + (2 * margin);
        if (drawer.isVisible()) {
            height += (emojiSize.get() + spacing) * DRAWER_TILES_HEIGHT;
        }
        double width = (emojiSize.get() * minimizedTilesWidth) + (margin * 2) + spacing * (minimizedTilesWidth-1);
        if (drawer.isVisible()) {
            width += (emojiSize.get() + margin) * (DRAWER_TILES_WIDTH-minimizedTilesWidth);
        }
        Stage stage = (Stage) mainContainer.getScene().getWindow();
        double increase = height - stage.getHeight();
        stage.setY(stage.getY() - increase);
        stage.setHeight(height);
        stage.setWidth(width);
    }
}
