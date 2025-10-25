package swj3.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class PreferencesDialog implements EventHandler<ActionEvent> {

  private Stage     dialogStage = new Stage();
  private TextField segmentLengthField;

  public PreferencesDialog(Window owner) {
    segmentLengthField = new TextField();
    segmentLengthField.setPromptText("enter segment length");

    Button btnOk = new Button("Ok");
    btnOk.setId("ok-button");
    btnOk.setDefaultButton(true);
    // styling:
    // btnOk.setPrefWidth(70);
    btnOk.setOnAction(this);

    Button btnCancel = new Button("Cancel");
    btnCancel.setId("cancel-button");
    // styling:
    // btnCancel.setPrefWidth(70);
    btnCancel.setOnAction(this);

    HBox buttonBar = new HBox(20);
    buttonBar.setId("button-bar");
    // styling:
    // buttonBar.setPadding(new Insets(15, 0, 0, 0));
    // buttonBar.setAlignment(Pos.CENTER);
    buttonBar.getChildren().addAll(btnOk, btnCancel);

    GridPane prefPane = new GridPane();
    prefPane.setId("pref-pane");
    // styling:
    // prefGrid.setHgap(10);
    // prefGrid.setVgap(10);
    // prefGrid.setPadding(new Insets(20));
    prefPane.add(new Label("Segment Length:"), 0, 0);
    prefPane.add(segmentLengthField, 1, 0);
    prefPane.add(buttonBar, 0, 1, 2, 1);

    Scene dialogScene = new Scene(prefPane);
    dialogScene.getStylesheets()
               .add(getClass().getResource("css/preferences-dialog.css")
                              .toExternalForm());

    dialogStage.setScene(dialogScene);
    dialogStage.setTitle("Preferences Dialog");
    dialogStage.initModality(Modality.WINDOW_MODAL);
    dialogStage.initStyle(StageStyle.UTILITY);
    dialogStage.initOwner(owner);
    dialogStage.setResizable(false);
  }

  public void show() {
    segmentLengthField.setStyle("-fx-border-width: 0px;");
    segmentLengthField.setText(String.valueOf(Preferences.getInstance()
                                                         .getSegmentLength()));
    Platform.runLater (() -> segmentLengthField.requestFocus());
    dialogStage.show();
  }

  @Override
  public void handle(ActionEvent event) {
    try {
      if (((Button) event.getSource()).getId().equals("ok-button"))
        Preferences.getInstance().setSegmentLength(
            Integer.parseInt(segmentLengthField.getText()));
      dialogStage.hide();
    }
    catch (NumberFormatException ex) {
      segmentLengthField.setStyle("-fx-border-color: red; " +
                                  "-fx-border-width: 2px; " +
                                  "-fx-border-radius: 3px;");
    }
  }

}