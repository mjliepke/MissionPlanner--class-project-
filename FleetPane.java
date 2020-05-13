import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class FleetPane extends Pane {
	private Circle[] drones;
	private int defaultRadius = 25;
	private Color[] colors;
	protected Slider timeSlider;

	public FleetPane(double[][] positions) {
		System.out.println("FleetPane has been constructed");
		newColors(positions.length);
		makeDrones(positions);
		newCircles(positions);
	}

	public FleetPane() {
		System.out.println("FleetPane has been constructed...please initialize fleet positions");
	}

	public void initializeFleetPane(double[][] positions) {
		newColors(positions.length);
		makeDrones(positions);
		newCircles(positions);
	}

	private void makeDrones(double[][] positions) {
		drones = new Circle[positions.length];
		for (int i = 0; i < drones.length; i++) {
			drones[i] = new Circle(positions[i][0], positions[i][1], defaultRadius);
			drones[i].setFill(colors[i]);
			drones[i].setOpacity(.8);
			getChildren().add(drones[i]);
		}
	}

	public void updatePositions(double[][] newPositions) {
		// System.out.println("printing the drone"+drones[0].getCenterX());
		// System.out.println("newPositions :"+newPositions[0][0]);
		newCircles(newPositions);
	}

	private void newCircles(double[][] positions) {
		for (int i = 0; i < drones.length; i++) {
			// System.out.println("drones[" + i + "]");
			// System.out.println("Positions: " + positions[i][0] + ", " + positions[i][1]);
			drones[i].setCenterX(positions[i][0]);
			drones[i].setCenterY(positions[i][1]);
		}
	}

	private void newColors(int length) {
		colors = new Color[length];
		for (int i = 0; i < length; i++) {
			int[] rgb = new int[3];
			for (int j = 0; j < rgb.length; j++) {
				rgb[j] = (int) (Math.random() * 255) + 1;
			}
			colors[i] = Color.rgb(rgb[0], rgb[1], rgb[2]);
		}
	}
	//SETTERS/GETTERS
	public void setRadius(int rad) {
		defaultRadius=rad;
	}

}
