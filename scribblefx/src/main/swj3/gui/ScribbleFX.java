package swj3.gui;

// import java.util.logging.LogManager; // (optional)

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;

public class ScribbleFX extends Application implements EventHandler<KeyEvent> {

  private Button leftButton, rightButton, upButton, downButton; // with quick fix added
  private ScribbleCanvas canvas;
  private ListView<String> messageList; // with quick fix added

  private MenuBar createMenuBar(Stage stage) {
    PreferencesDialog dialog = new PreferencesDialog(stage);

    MenuItem prefItem = new MenuItem("Preferences ...");
    prefItem.setOnAction(e -> dialog.show());

    Menu settingsMenu = new Menu("Settings");
    settingsMenu.getItems().add(prefItem);

    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().add(settingsMenu);

    return menuBar;
  }

  private Button createTextButton(String id, String caption) {
    Button button = new Button(caption);
    button.setId(id); // NOTE: used later for styling
    // TODO: import for javafx.geometry
    button.setPadding(new Insets(5));
    button.setPrefSize(50, 40);
    button.setMinSize(50, 50);
    return button;
  }

  private Button createIconButton(String id, String iconFile) {
    Button button = new Button();
    button.setId(id);
    return button;
  }

  private ColorPicker colorPicker;

  @SuppressWarnings("unused")
  private Pane createControlPane() {
    // TODO: create field for each button
    // leftButton = createTextButton("left-button", "Left");
    // rightButton = createTextButton("right-button", "Right");
    // upButton = createTextButton("up-button", "Up");
    // downButton = createTextButton("down-button", "Down");
    leftButton = createIconButton("left-button", "css/button-left.png");
    rightButton = createIconButton("right-button", "css/button-right.png");
    upButton = createIconButton("up-button", "css/button-up.png");
    downButton = createIconButton("down-button", "css/button-down.png");

    GridPane buttonPane = new GridPane();
    buttonPane.setId("button-pane");
    buttonPane.add(leftButton, 0, 1);
    buttonPane.add(rightButton, 2, 1);
    buttonPane.add(upButton, 1, 0);
    buttonPane.add(downButton, 1, 2);


    EventHandler<ActionEvent> handler = new ButtonEventHandler();
    // TODO: import javafx.events.EventHandler and ActionEvent
    // TODO: create private inner class (non static) [X] enclosing type
    // NOTE: handler implemented a little later
    final int version = 5;
    messages.add("Version = " + version);
    if (version == 1) { // direct action
      leftButton.setOnAction(handler);
      rightButton.setOnAction(handler);
      upButton.setOnAction(handler);
      downButton.setOnAction(handler);
    } else if(version == 2) { // register as one of many handlers
      leftButton.addEventHandler(ActionEvent.ACTION, handler);
      rightButton.addEventHandler(ActionEvent.ACTION, handler);
      upButton.addEventHandler(ActionEvent.ACTION, handler);
      downButton.addEventHandler(ActionEvent.ACTION, handler);
    } else if (version == 3) { // handle actions inside button pane
      buttonPane.addEventHandler(ActionEvent.ACTION, handler);
    } else if (version == 4) {
      buttonPane.addEventHandler(ActionEvent.ACTION,
              new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                  ScribbleFX.this.handleButtonEvent(event);
                }
              });
    } else if (version == 5) {
      buttonPane.addEventHandler(
              ActionEvent.ACTION,
              e -> handleButtonEvent(e));
    }

    Label colorLabel = new Label("Color: ");
    colorPicker = new ColorPicker(Color.BLACK); // TODO: create field
    colorPicker.setOnAction(e -> {
      appendMessage(String.format("Color '%s' selected", colorPicker.getValue()));
      canvas.setLineColor(colorPicker.getValue());
    });

    HBox colorPane = new HBox(colorLabel, colorPicker);
    colorPane.setId("color-pane");

    VBox controlPane = new VBox();
    controlPane.setId("control-pane");
    controlPane.getChildren().addAll(buttonPane, colorPane);
    return controlPane;
  }

  private static final Border DEFAULT_BORDER = new Border(
          new BorderStroke(Color.DIMGREY, BorderStrokeStyle.SOLID, null, null));

  private ObservableList<String> messages = FXCollections.observableArrayList();

  private ListView<String> createMessageList() {
    ListView<String> messageList = new ListView<>();
    messageList.setId("message-list");
    // NOTE: messages does not yet exists
    // and will be defined further below
    messageList.setItems(messages);
    messageList.setFixedCellSize(25); // don't warn on scroll
    HBox.setHgrow(messageList, Priority.ALWAYS);
    return messageList;
  }

  // NOTE: ScribbleCanavas is provided and will be examined
  // in more detail later
  private ScribbleCanvas createCanvas() {
    ScribbleCanvas canvas = new ScribbleCanvas();
    canvas.setId("canvas");
    canvas.setOnMouseClicked(e -> {
      Point2D p = new Point2D(e.getX(), e.getY());
      // NOTE: import javafx.geometry
      appendMessage(String.format("Mouse clicked at '%s'", p));
      canvas.addLineSegment(p);
    });
    VBox.setVgrow(canvas, Priority.ALWAYS);
    VBox.setMargin(canvas, new Insets(0, 10, 10, 10));
    return canvas;
  }

  private static final Background CANVAS_BACKGROUND = new Background(
          new BackgroundFill(Color.CORNSILK, null, null));

  @Override // lifecycle method, called by launch
  public void start(Stage primaryStage) throws Exception {
    Pane controlPane = createControlPane();
    messageList = createMessageList(); // TODO: create field
    canvas = createCanvas(); // TODO: create field

    HBox topPane = new HBox(controlPane, messageList);
    topPane.setId("top-pane");

    VBox rootPane = new VBox(createMenuBar(primaryStage), topPane, canvas);
    rootPane.setId("root-pane");
    // rootPane.setOnKeyPressed(e -> {
    //     appendMessage(String.format("Key '%s' pressed", e.getCode()));
    // });
    rootPane.setOnKeyPressed(this);

    Scene scene = new Scene(rootPane, 500, 500);
    scene.getStylesheets().add(getClass().getResource("css/scribble-fx.css").toString());
    primaryStage.setScene(scene);


    primaryStage.setMinWidth(400);
    primaryStage.setMinHeight(400);
    primaryStage.setTitle("ScribbleFX");
    primaryStage.show();
  }

  public static void main(String[] args) {
    // NOTE: optional, can suppress some irrelevant error messages
    // LogManager.getLogManager().reset();
    launch(args);
  }

  public class ButtonEventHandler implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent event) {
      ScribbleFX.this.handleButtonEvent(event);
    }
  }

  private void handleButtonEvent(ActionEvent event) {
    if (event.getTarget() instanceof Button) {
      Button button = (Button)event.getTarget();
      appendMessage(String.format("Button '%s' pressed", button.getId()));

      if (button == leftButton)
        canvas.addLineSegment(Direction.LEFT);
      else if (button == rightButton)
        canvas.addLineSegment(Direction.RIGHT);
      else if (button == upButton)
        canvas.addLineSegment(Direction.UP);
      else if (button == downButton)
        canvas.addLineSegment(Direction.DOWN);
    }
  }

  public void appendMessage(String message) {
    messages.add(message); // propagated to view (observable)
    messageList.scrollTo(messages.size());
  }

  @Override
  public void handle(KeyEvent event) {
    appendMessage(String.format("Key '%s' pressed", event.getCode()));

    switch(event.getCode()) {
      case LEFT:
        canvas.addLineSegment(Direction.LEFT);
        event.consume();
        // NOTE: don't process further actions, e.g. focus change
        break;
      case RIGHT:
        canvas.addLineSegment(Direction.RIGHT);
        event.consume();
        break;
      case UP:
        canvas.addLineSegment(Direction.UP);
        event.consume();
        break;
      case DOWN:
        canvas.addLineSegment(Direction.DOWN);
        event.consume();
        break;
      default:
        // ignore
    }
  }
}
