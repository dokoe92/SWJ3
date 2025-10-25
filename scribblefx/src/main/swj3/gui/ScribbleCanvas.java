package swj3.gui;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class ScribbleCanvas extends Pane {

  private GraphicsContext gc;
  private Canvas canvas = new Canvas();

  private Point2D currentPos = new Point2D(1, 1);
  private List<Point2D> positionList = new ArrayList<>();
  private List<Color> colorList = new ArrayList<>();

  public ScribbleCanvas() {
    positionList.add(currentPos);

    gc = canvas.getGraphicsContext2D();
    gc.setLineWidth(2);

    this.getChildren().add(canvas);
    canvas.widthProperty().bind(this.widthProperty());
    canvas.heightProperty().bind(this.heightProperty());

    // register for resize events
    canvas.widthProperty().addListener(event -> redraw());
    canvas.heightProperty().addListener(event -> redraw());
  }

  public void addLineSegment(Point2D newPosition) {
    gc.strokeLine(currentPos.getX(), currentPos.getY(), newPosition.getX(), newPosition.getY());
    currentPos = newPosition;
    positionList.add(currentPos);
    colorList.add(getLineColor());
  }

	public void addLineSegment(Direction direction) {
		int len = Preferences.getInstance().getSegmentLength();
		Point2D newPos = null;
		switch (direction) {
		  case UP:    newPos = createRelativePoint(0, -len); break;
		  case DOWN:  newPos = createRelativePoint(0, +len); break;
		  case LEFT:  newPos = createRelativePoint(-len, 0); break;
		  case RIGHT: newPos = createRelativePoint(+len, 0); break;
		}
		addLineSegment(newPos);
	}

	private Point2D createRelativePoint(double dx, double dy) {
	  return createBoundedPoint(
	      currentPos.getX() + dx,
	      currentPos.getY() + dy);
	}

	private Point2D createBoundedPoint(double x, double y) {
	  return new Point2D(
	      bound(x, 1, canvas.getWidth()-1),
	      bound(y, 1, canvas.getHeight()-1));
	}

	private static double bound(double value, double min, double max) {
	  return Math.max(min, Math.min(max, value));
	}

  private void redraw() {
    Color currColor = getLineColor();

    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    for (int i=0; i<positionList.size()-1; i++) {
      Point2D from = positionList.get(i);
      Point2D to = positionList.get(i+1);
      setLineColor(colorList.get(i));
      gc.strokeLine(from.getX(), from.getY(), to.getX(), to.getY());
    }

    setLineColor(currColor);
  }

  public void setLineColor(Color color) {
    gc.setStroke(color);
  }

  public Color getLineColor() {
    return (Color)gc.getStroke();
  }

}