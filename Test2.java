import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Test2 extends Application {
	Fleet fleet = new Fleet(4);
	private TabPane mainPane = new TabPane();
	private Pane dronePane = new Pane();
	private Tab droneTab = new Tab();
	private Circle[][] droneCircle;
	private Circle loiterCircle = new Circle(300, 300, 50);
	private double time = 100;
	private int iterations = 100;

	public Test2() {// Constructor
		droneCircle = new Circle[iterations][fleet.getLength()];
		loiterCircle.setVisible(false);
		loiterCircle.setOnMousePressed(e -> {
			loiterCircle.setVisible(false);
		});
		loiterCircle.setOnMouseReleased(e -> {
			loiterCircle.setVisible(true);
		});
		//initializing the fleet
		fleet.get(0).setLoiterTurn(100, new double[] {300,300});
		fleet.get(1).setHover();
		fleet.get(2).setHover();
		fleet.get(3).setHover();
//		fleet.get(1).setLoiterTurn(125,new  double[] {500,300});
//		fleet.get(0).setWayPoint(new double[] {100,100});
//		fleet.get(2).setCounterClockwiseLoiter();
//		fleet.get(2).setLoiterTurn(20, new double[] {200,300});
//		fleet.get(3).setWayPoint(fleet.get(2).getWayPoint());
//		System.out.println("fleet.get(2).getWayPoint(): "+fleet.get(2).getWayPoint());

		// initializing the circles to the fleet
		for (int j = 0; j < fleet.getLength(); j++) {
			for (int i = 0; i < iterations - 1; i++) {
				if(fleet.get(0).getStatus().equalsIgnoreCase("hover")) {
					fleet.get(0).setLoiterTurn(50, new double[] {300,300});
				}
				if(i==10) {
					fleet.get(0).setLoiterRadius(200);
				}
				System.out.println("this is i and j: " + i + ", " + j);
				droneCircle[i][j] = new Circle(fleet.get(j).getXPosition(), fleet.get(j).getYPosition(), 3);
				dronePane.getChildren().add(droneCircle[i][j]);
				fleet.get(j).move(time / iterations);
			}
		}
		dronePane.getChildren().add(loiterCircle);
		droneTab.setText("drone test");
		droneTab.setContent(dronePane);

		mainPane.getTabs().add(droneTab);

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
