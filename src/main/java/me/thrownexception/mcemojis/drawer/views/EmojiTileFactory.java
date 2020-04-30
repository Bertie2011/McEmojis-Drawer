package me.thrownexception.mcemojis.drawer.views;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.robot.Robot;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class EmojiTileFactory {

    private static final String EMOJI_PATH = "assets/emoji/";
    private static final String MODIFIERS_KEY = "modifiers";
    private static final String FILE_NAME_KEY = "fileName";
    private static final String MC_CODE_KEY = "mcCode";
    private static final String DESCRIPTION_KEY = "description";
    private static final int THREAD_DELAY = 100;
    private static final ExecutorService THREAD_RUNNER = Executors.newSingleThreadExecutor();

    public static void createTiles(Pane container, JsonArray data, Labeled descriptionContainer, DoubleProperty size) {
        for (JsonElement el : data) {
            Platform.runLater(() -> {
                Robot robot = new Robot();
                JsonObject base = (JsonObject) el;
                JsonObject modifier = base.getAsJsonArray(MODIFIERS_KEY).get(0).getAsJsonObject();
                String filename = modifier.get(FILE_NAME_KEY).getAsString();
                String description = modifier.get(DESCRIPTION_KEY).getAsString();
                Image image = new Image(EmojiTileFactory.class.getClassLoader().getResourceAsStream(EMOJI_PATH + filename));
                ImageView imageView = new ImageView(image);
                imageView.fitWidthProperty().bind(size);
                imageView.fitHeightProperty().bind(size);
                imageView.setCursor(Cursor.HAND);
                imageView.setUserData(modifier);
                imageView.setOnMouseClicked(e -> processClick(e, imageView, robot));
                imageView.setOnMouseEntered(e -> descriptionContainer.setText(description));
                imageView.setOnMouseExited(e -> descriptionContainer.setText(""));
                imageView.setPickOnBounds(true);
                imageView.managedProperty().bind(imageView.visibleProperty());
                container.getChildren().add(imageView);
            });
        }
    }

    private static void processClick(MouseEvent e, ImageView imageView, Robot robot) {
        JsonObject modifier = (JsonObject) imageView.getUserData();
        String mcCode = modifier.get(MC_CODE_KEY).getAsString();
        if (e.getButton().equals(MouseButton.PRIMARY)) {
            String code = new String(Character.toChars(Integer.parseInt(mcCode.substring(2), 16)));
            writeText(imageView.getScene().getWindow(), code, robot);
        } else if (e.getButton().equals(MouseButton.SECONDARY)) {
            writeText(imageView.getScene().getWindow(), mcCode, robot);
        }
    }

    private static void writeText(Window window, String txt, Robot robot) {
        List<Point2D> oldPos = new ArrayList<>(1);
        List<HashMap<DataFormat, Object>> oldContent = new ArrayList<>(1);
        oldContent.add(null);
        THREAD_RUNNER.submit(() -> {
            try {
                Platform.runLater(() -> {
                    //Move Below application
                    oldPos.add(robot.getMousePosition());
                    robot.mouseMove(window.getX() + window.getWidth() + 15, window.getY());
                    oldPos.add(robot.getMousePosition());
                    robot.mouseClick(MouseButton.PRIMARY);
                    //Set Clipboard
                    oldContent.set(0, new HashMap<>());
                    for (DataFormat format : Clipboard.getSystemClipboard().getContentTypes()) {
                        oldContent.get(0).put(format, Clipboard.getSystemClipboard().getContent(format));
                    }
                    ClipboardContent content = new ClipboardContent();
                    content.putString(txt);
                    Clipboard.getSystemClipboard().setContent(content);
                });
                Thread.sleep(THREAD_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        THREAD_RUNNER.submit(() -> {
            try {
                Platform.runLater(() -> {
                    //Paste
                    robot.keyPress(KeyCode.COMMAND);
                    robot.keyPress(KeyCode.CONTROL);
                    robot.keyPress(KeyCode.V);
                    robot.keyRelease(KeyCode.V);
                    robot.keyRelease(KeyCode.COMMAND);
                    robot.keyRelease(KeyCode.CONTROL);
                    Point2D newPos = robot.getMousePosition();
                    robot.mouseMove(oldPos.get(0).add(newPos).subtract(oldPos.get(1))); // start + (newPos - alt)
                });
                Thread.sleep(THREAD_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        THREAD_RUNNER.submit(() -> {
            try {
                Platform.runLater(() -> {
                    //Restore
                    Clipboard.getSystemClipboard().setContent(oldContent.get(0));
                });
                Thread.sleep(THREAD_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public static void filter(TilePane tiles, String text) {
        for (Node node : tiles.getChildren()) {
            ImageView image = (ImageView) node;
            JsonObject modifier = (JsonObject) image.getUserData();
            String description = modifier.get(DESCRIPTION_KEY).getAsString();
            node.setVisible(description.toLowerCase().contains(text.toLowerCase()));
        }
    }
}
