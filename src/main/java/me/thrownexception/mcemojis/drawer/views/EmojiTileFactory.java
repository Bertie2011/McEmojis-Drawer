package me.thrownexception.mcemojis.drawer.views;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.robot.Robot;

public class EmojiTileFactory {

    private static final String EMOJI_PATH = "/assets/emoji/";
    private static final String MODIFIERS_KEY = "modifiers";
    private static final String FILE_NAME_KEY = "fileName";
    private static final String MC_CODE_KEY = "mcCode";
    private static final String DESCRIPTION_KEY = "description";

    public static void createTiles(Pane container, JsonArray data, Labeled descriptionContainer, DoubleProperty size, TextWriter writer) {
        for (JsonElement el : data) {
            Platform.runLater(() -> {
                JsonObject base = (JsonObject) el;
                JsonObject modifier = base.getAsJsonArray(MODIFIERS_KEY).get(0).getAsJsonObject();
                String filename = modifier.get(FILE_NAME_KEY).getAsString();
                String description = modifier.get(DESCRIPTION_KEY).getAsString();
                Image image = new Image(EmojiTileFactory.class.getResourceAsStream(EMOJI_PATH + filename));
                ImageView imageView = new ImageView(image);
                imageView.fitWidthProperty().bind(size);
                imageView.fitHeightProperty().bind(size);
                imageView.setCursor(Cursor.HAND);
                imageView.setUserData(modifier);
                imageView.setOnMouseClicked(e -> processClick(e, imageView, writer));
                imageView.setOnMouseEntered(e -> descriptionContainer.setText(description));
                imageView.setOnMouseExited(e -> descriptionContainer.setText(""));
                imageView.setPickOnBounds(true);
                imageView.managedProperty().bind(imageView.visibleProperty());
                container.getChildren().add(imageView);
            });
        }
    }

    private static void processClick(MouseEvent e, ImageView imageView, TextWriter writer) {
        JsonObject modifier = (JsonObject) imageView.getUserData();
        String mcCode = modifier.get(MC_CODE_KEY).getAsString();
        if (e.getButton().equals(MouseButton.PRIMARY)) {
            String code = new String(Character.toChars(Integer.parseInt(mcCode.substring(2), 16)));
            writer.writeText(imageView.getScene().getWindow(), code);
        } else if (e.getButton().equals(MouseButton.SECONDARY)) {
            writer.writeText(imageView.getScene().getWindow(), mcCode);
        }
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
