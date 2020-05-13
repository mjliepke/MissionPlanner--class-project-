import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class TestDroneFlightLogMission extends Application{
	Drone drone = new Drone(2);

	private TabPane mainPane = new TabPane();
	private Pane dronePane = new Pane();
	private Tab droneTab = new Tab();
	private Circle[] droneCircle;
	private Circle loiterCircle = new Circle(300, 300, 50);
	private double time = 50;
	private int iterations = 50;

	
	public TestDroneFlightLogMission() {// Constructor
		//drone.setLoiterTurn(50,new double[] {300,300});
		drone.setLoiterTurn(30,new double[] {201,200});
		
		droneCircle = new Circle[iterations];
		loiterCircle.setVisible(false);
		loiterCircle.setOnMousePressed(e -> {
			loiterCircle.setVisible(true);
		});
		loiterCircle.setOnMouseReleased(e -> {
			loiterCircle.setVisible(false);
		});
		for (int i = 0; i < iterations; i++) {
			droneCircle[i] = new Circle(drone.getXPosition(), drone.getYPosition(), 3);
			dronePane.getChildren().add(droneCircle[i]);
			drone.move(time / iterations);
//			if(i==36) {
//				System.out.println("*********Changing the LoiterRadius***********");
//				drone.setLoiterRadius(60);
//			}
		}
		System.out.println(drone.getLoiterRadius());
		dronePane.getChildren().add(loiterCircle);
		droneTab.setText("drone test");
		droneTab.setContent(dronePane);

		mainPane.getTabs().add(droneTab);
		
		///THIS IS THE NEW STUFF
		drone.saveLog("firstTestLog");

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene1 = new Scene(mainPane, 600, 600);
		primaryStage.setScene(scene1);
		primaryStage.setTitle("testing the drone");
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}
}
