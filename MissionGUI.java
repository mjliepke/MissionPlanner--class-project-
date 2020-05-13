import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MissionGUI extends Application {
	private GridPane mainPane;
	private FleetPane fleetPane;
	private FleetConstructorPane fleetMakerPane;
	private MissionSelectorPane missionMakerPane;
	private Button saveButton;
	private SaveScene saveScene;
//	private SavePane savePane;

	private Mission myMission;

	public MissionGUI() {
		mainPane = new GridPane();
		makeSaveButton();
		makeFleetPane();
		makeFleetMakerPane();
		changeMainPaneGrids();
		// makeSaveScene();

		// makeMissionMakerPane();
	}

	private void makeFleetPane() {
		fleetPane = new FleetPane();
		mainPane.add(fleetPane, 0, 1);
	}

	private void makeFleetMakerPane() {
		fleetMakerPane = new FleetConstructorPane();
		fleetMakerPane.fleetConstructedProperty.getValue();
		fleetMakerPane.fleetConstructedProperty.addListener((observable, oldVal, newVal) -> {
			if (newVal) {
				Fleet temp = fleetMakerPane.getFleet();
				fleetMakerPane.setEditable(false);
				makeMissionMakerPane();
				System.out.println("The fleet has been gitten");
			}
		});
		mainPane.add(fleetMakerPane, 1, 1);
	}

	private void makeMissionMakerPane() {
		missionMakerPane = new MissionSelectorPane();
		mainPane.add(missionMakerPane, 1, 2);
		missionMakerPane.missionGoButton.setOnAction(e -> {
			switch (missionMakerPane.getMission()) {
			case "LoiterAround":
				myMission = new LoiterAround(missionMakerPane.getCenter(), missionMakerPane.getRadius(),
						fleetMakerPane.getFleet(), missionMakerPane.getTime(), missionMakerPane.getDroneSeparation());
			}
			myMission.armMission();
			myMission.runMission();
			finishFleetPane();
		});

	}

	private void finishFleetPane() {
		fleetPane.setRadius(missionMakerPane.getDroneSeparation());
		fleetPane.initializeFleetPane(myMission.getFleetPosition(0));
		for (int i = 1; i < myMission.getIterations() - 1; i++) {
			fleetPane.updatePositions(myMission.getFleetPosition(i));
		}
		fleetPane.timeSlider = new Slider(0, myMission.getIterations(), 0);
		fleetPane.timeSlider.setLayoutX(50);
		fleetPane.timeSlider.setLayoutY(500);
		fleetPane.timeSlider.setMinWidth(450);
		fleetPane.timeSlider.valueProperty().addListener((observable, oldvalue, newvalue) -> {
			fleetPane.updatePositions(myMission.getFleetPosition((int) fleetPane.timeSlider.getValue()));
		});
		fleetPane.getChildren().add(fleetPane.timeSlider);
	}

	private void makeSaveButton() {

		Menu save = new Menu("File");
		save.getItems().add(new MenuItem("SaveLog"));
		Menu reset = new Menu("Reset");
		Menu preload = new Menu("PreLoad");

		save.setOnAction(e -> {
			try {
			if (myMission.hasRun()) {
				makeSaveScene();
			}
			}catch(NullPointerException err) {//Mission might not be initialized
				Pane warningPane=new Pane();
				Text temptext=new Text("Initialize a Mission");
				temptext.setLayoutY(30);
				warningPane.getChildren().add(temptext);
				Stage tempWarning=new Stage();
				Scene tempscene=new Scene(warningPane, 100,50);
				tempWarning.setScene(tempscene);
				tempWarning.show();
				
			}
		});
		MenuBar menuBar = new MenuBar();
		menuBar.setMaxWidth(250);
		menuBar.getMenus().addAll(save, reset, preload);
		mainPane.getChildren().add(menuBar);
	}

	private void makeSaveScene() {
		saveScene = new SaveScene();
		saveScene.saveButton.setOnAction(e -> {
			myMission.saveLogs(saveScene.getFileName());
			saveScene.hide();
		});
	}

	private void changeMainPaneGrids() {
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(70);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(30);
		mainPane.getColumnConstraints().addAll(column1, column2);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene1 = new Scene(mainPane, 1080, 720);
		primaryStage.setScene(scene1);
		primaryStage.setTitle("MissionGUI");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
