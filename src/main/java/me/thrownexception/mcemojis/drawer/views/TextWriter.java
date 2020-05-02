package me.thrownexception.mcemojis.drawer.views;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.input.*;
import javafx.scene.robot.Robot;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TextWriter {
    private static final int THREAD_DELAY = 100;
    private static final ExecutorService THREAD_RUNNER = Executors.newSingleThreadExecutor(r -> {
        Thread th = new Thread(r);
        th.setDaemon(true);
        return th;
    });
    private Robot robot;

    public TextWriter(Robot robot) {
        this.robot = robot;
    }


    public void writeText(Window window, String txt) {
        submitMoveAndCopy(window, txt);
    }

    private void submitMoveAndCopy(Window window, String txt) {
        THREAD_RUNNER.submit(() -> {
            try {
                Thread.sleep(THREAD_DELAY);
                Platform.runLater(() -> {
                    //Move Below application
                    Point2D oldPos = robot.getMousePosition();
                    Point2D outPos = new Point2D(window.getX() + window.getWidth() + 25, window.getY());
                    robot.mouseMove(outPos);
                    robot.mouseClick(MouseButton.PRIMARY);
                    //Set Clipboard
                    HashMap<DataFormat, Object> oldContent = new HashMap<>();
                    for (DataFormat format : Clipboard.getSystemClipboard().getContentTypes()) {
                        oldContent.put(format, Clipboard.getSystemClipboard().getContent(format));
                    }
                    ClipboardContent content = new ClipboardContent();
                    content.putString(txt);
                    Clipboard.getSystemClipboard().setContent(content);
                    submitPasteAndMove(oldPos, outPos, oldContent);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void submitPasteAndMove(Point2D oldPos, Point2D outPos, HashMap<DataFormat, Object> oldContent) {
        THREAD_RUNNER.submit(() -> {
            try {
                Thread.sleep(THREAD_DELAY);
                Platform.runLater(() -> {
                    //Paste
                    robot.keyPress(KeyCode.COMMAND);
                    robot.keyPress(KeyCode.CONTROL);
                    robot.keyPress(KeyCode.V);
                    robot.keyRelease(KeyCode.V);
                    robot.keyRelease(KeyCode.COMMAND);
                    robot.keyRelease(KeyCode.CONTROL);
                    submitRestoreClipboard(oldPos, outPos, oldContent);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void submitRestoreClipboard(Point2D oldPos, Point2D outPos, HashMap<DataFormat, Object> oldContent) {
        THREAD_RUNNER.submit(() -> {
            try {
                Thread.sleep(THREAD_DELAY);
                Platform.runLater(() -> {
                    //Restore
                    Point2D newPos = robot.getMousePosition();
                    robot.mouseMove(oldPos.add(newPos).subtract(outPos)); // start + (newPos - alt)
                    Clipboard.getSystemClipboard().setContent(oldContent);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
