import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public class DronePane extends Pane {
	private double[][] fleet;
	private Circle[] circleFleet;
	private double radius;
	
	public DronePane(double[][] fleet) {
		//the second dimension shall hold the x/y
		radius=2;
		this.fleet = fleet;
		drawFleet();
	}
	private void drawFleet() {
		circleFleet=new Circle[fleet.length];
		for(int i=0;i<fleet.length;i++) {
			circleFleet[i]=new Circle(fleet[i][0],fleet[i][1],radius);
			getChildren().add(circleFleet[i]);
		}
	}
}
