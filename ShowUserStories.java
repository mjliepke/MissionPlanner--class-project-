import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ShowUserStories extends Application {
	private FleetPane mainPane;
	private Fleet myfleet;
	private final int iterations = 50;
	private final int time = 50;

	public ShowUserStories() {
		makeFleet();
		sendMovement();
		mainPane = new FleetPane(myfleet.getPositions());

		for (int i = 1; i < iterations; i++) {
			for (int j = 0; j < myfleet.getLength(); j++) {
				myfleet.get(j).move(time / iterations);
			}
			mainPane.updatePositions(myfleet.getPositions());
		}

	}

	private void makeFleet() {
		Drone[] temp = new Drone[6];
		for (int i = 0; i < temp.length; i++) {
			temp[i] = new Drone(2);
		}
		myfleet = new Fleet(temp);
	}

	private void sendMovement() {
		myfleet.get(0).setWayPoint(new double[] { 100, 200 });
		myfleet.get(1).setWayPoint(new double[] { 200, 100 });
		myfleet.get(2).setLoiterTurn(20, new double[] { 300, 300 });
		myfleet.get(3).setLoiterTurn(50, new double[] { 300, 150 });
		myfleet.get(4).setCounterClockwiseLoiter();
		myfleet.get(4).setLoiterTurn(50, new double[] { 300, 150 });
		myfleet.get(5).setHover();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		Scene scene = new Scene(mainPane, 600, 600);
		primaryStage.setScene(scene);
		primaryStage.setTitle("whatev, userStories");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
