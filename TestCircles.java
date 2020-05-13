import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class TestCircles extends Application {
	
	Pane mainPane;
	Circle myDrone;
	
	public TestCircles() {
		myDrone= new Circle(100,100,40);
		mainPane=new Pane();
		mainPane.getChildren().add(myDrone);
		myDrone.setCenterX(300);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene1 = new Scene(mainPane, 600, 600);
		primaryStage.setScene(scene1);
		primaryStage.setTitle("loiterMission");
		primaryStage.show();
	}
	public static void main(String[] args) {
		launch(args);
	}

}
