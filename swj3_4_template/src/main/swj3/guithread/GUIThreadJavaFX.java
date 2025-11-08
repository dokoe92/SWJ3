package swj3.guithread;

import swj3.util.Util;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class GUIThreadJavaFX  extends Application {

    private Button startButton;
    private ProgressIndicator progressIndicator;

    public static void main(String[] args) {
        launch(args);
    }

    private class Worker implements Runnable {

        @Override
        public void run() {
            for (int i = 1; i <= 100; i++) {
                final double progress = i / 100.0;
                Platform.runLater( () -> progressIndicator.setProgress(progress) );
                Util.sleep(100);
            }
            Platform.runLater( () -> startButton.setDisable(false));
        }
    }

    @Override
    public void start(Stage primaryStage) {
        progressIndicator = new ProgressIndicator(0);
        progressIndicator.setPadding(new Insets(10));
        progressIndicator.setMaxWidth(Double.MAX_VALUE);
        progressIndicator.setMaxHeight(Double.MAX_VALUE);

        startButton = new Button("Start");

//        Runnable r = () -> {...};

        startButton.setOnAction(event -> {
            // while work is done here (event thread) the
            // GUI cannot respond to other events e.g. resizing
            startButton.setDisable(true);
//            Thread.ofVirtual().name("worker").start(new Worker());

            Thread.ofVirtual().name("worker").start( () -> {
                for (int i = 1; i <= 100; i++) {
                    final double progress = i / 100.0;
                    Platform.runLater( () -> progressIndicator.setProgress(progress) );
                    Util.sleep(100);
                }
                Platform.runLater( () -> startButton.setDisable(false));
            });
        });

        GridPane rootPane = new GridPane();
        rootPane.add(progressIndicator, 0, 0);
        rootPane.add(startButton, 0, 1);

        GridPane.setVgrow(progressIndicator, Priority.ALWAYS);
        GridPane.setHgrow(progressIndicator, Priority.ALWAYS);
        GridPane.setHalignment(startButton, HPos.CENTER);
        // GridPane.setMargin(startButton, new Insets(0, 10, 10, 0));
        GridPane.setMargin(startButton, new Insets(10, 0, 10, 0));

        Scene scene = new Scene(rootPane, 200, 200);

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(140);
        primaryStage.setMinHeight(160);
        primaryStage.setTitle("Thread Test");
        primaryStage.show();
    }
}
