import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

public class TestFinalFileIOComplexMission extends Application {
	FleetPane mainPane;
	Slider timeSlider;
	int iterations = 2000;
	int fleetCount = 15;
	LoiterAround loiterMission;

	public TestFinalFileIOComplexMission() {

		loiterMission = new LoiterAround(new double[] { 540, 360 }, 30, new Fleet(fleetCount), iterations);
		mainPane = new FleetPane(loiterMission.getFleetPosition(0));
		createTimeSlider(iterations);
		loiterMission.armMission();
		loiterMission.runMission();
		loiterMission.saveLogs("TestComplexMissionFileIO1");
		for (int i = 0; i < iterations; i++) {
			mainPane.updatePositions(loiterMission.getFleetPosition(i));
//			for (int j = 0; j < fleetCount; j++) {
//				System.out.println("xPos of"+j+" drone on "+i+" iterations:\t"+loiterMission.getFleetPosition(i)[j][0]);
//				System.out.println("YPos of"+j+" drone on "+i+" iterations:\t"+loiterMission.getFleetPosition(i)[j][1]);
//				
//			}
		}
	}

	private void createTimeSlider(int maxTime) {
		timeSlider = new Slider(0, maxTime, 0);
		timeSlider.setLayoutX(50);
		timeSlider.setLayoutY(500);
		timeSlider.setMinWidth(450);
		// timeSlider.setBlockIncrement(30);
		// timeSlider.setMaxWidth(500);
		timeSlider.valueProperty().addListener((observable, oldvalue, newvalue) -> {
			mainPane.updatePositions(loiterMission.getFleetPosition((int) timeSlider.getValue()));
		});
		mainPane.getChildren().add(timeSlider);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene1 = new Scene(mainPane, 1080, 720);
		primaryStage.setScene(scene1);
		primaryStage.setTitle("loiterMission");
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);

	}
}
