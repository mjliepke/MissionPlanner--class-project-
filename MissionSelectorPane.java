import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class MissionSelectorPane extends Pane {
	private Text descMission;
	private ComboBox<String> missionOptions;
	private ComboBox<Integer> avoidRadiusOptions;
	private String missionDesc = "Select a Mission to run";
	private ObservableList<String> missions = FXCollections.observableArrayList("LoiterAround");
	public Button missionGoButton;
	// ******** LoiterAround
	private TextField xPos, yPos, distGet, timeGet;
	private Text posDesc, distDesc, timeDesc, avoidRadDesc;

	private int time;

	public MissionSelectorPane() {
		createOptions();
		makeGoButton();
	}

	private void createOptions() {
		// makeBooleanMissionProperties();
		descMission = new Text(missionDesc);
		descMission.setLayoutY(30);
		missionOptions = new ComboBox<String>(missions);
		missionOptions.setLayoutY(45);
		missionOptions.valueProperty().addListener((observable, oldVal, newVal) -> {
			displayMission(newVal);
		});
		getChildren().add(descMission);
		getChildren().add(missionOptions);
	}

	private void makeGoButton() {
		missionGoButton = new Button("RUN");
		missionGoButton.setLayoutY(300);
		missionGoButton.setLayoutX(150);
		missionGoButton.setDisable(true);
		getChildren().add(missionGoButton);
	}

	private void displayMission(String mission) {
		switch (mission) {
		case "LoiterAround":
			showLoiterAroundOptions();
		}
	}

	private void updateLoiterGo() {
		try {
			boolean xgood, ygood, distgood, timegood, allgood;
			xgood = (Double.valueOf(xPos.getText()) != null);
			ygood = (Double.valueOf(yPos.getText()) != null);
			distgood = (Double.valueOf(distGet.getText()) > 0);
			timegood = (Double.valueOf(timeGet.getText()) > 0);
			allgood = (xgood && ygood && distgood && timegood);
			if (allgood) {
				missionGoButton.setDisable(false);
			}
		} catch (NullPointerException e) {
			missionGoButton.setDisable(true);
		} catch (NumberFormatException e) {
			missionGoButton.setDisable(true);
		}
	}

	private void showLoiterAroundOptions() {
		// ***********POSITION OPTIONS*****
		// Text
		posDesc = new Text("Center of Loiter? (X,Y)");
		posDesc.setY(100);
		getChildren().add(posDesc);
		// TextFields
		xPos = new TextField();
		yPos = new TextField();
		xPos.setMaxWidth(50);
		yPos.setMaxWidth(50);
		xPos.setLayoutX(100);
		xPos.setLayoutY(105);
		yPos.setLayoutY(105);
		xPos.textProperty().addListener((obser, oldV, newW) -> {
			updateLoiterGo();
		});
		yPos.textProperty().addListener((obser, oldV, newW) -> {
			updateLoiterGo();
		});
		getChildren().add(xPos);
		getChildren().add(yPos);

		// **********DISTANCE OPTION********
		distDesc = new Text("Closest Radius allowed?");
		distDesc.setLayoutY(160);
		distGet = new TextField();
		distGet.setMaxWidth(50);
		distGet.setLayoutX(175);
		distGet.setLayoutY(138);
		distGet.textProperty().addListener((obser, oldV, newW) -> {
			updateLoiterGo();
		});
		getChildren().add(distGet);
		getChildren().add(distDesc);
		// ********TIME OPTION***********
		timeDesc = new Text("Time Span?(Time Flies, try 100+)");
		timeDesc.setLayoutY(210);
		timeGet = new TextField();
		timeGet.setMaxWidth(50);
		timeGet.setLayoutY(215);
		timeGet.setLayoutX(150);
		timeGet.textProperty().addListener((obser, oldV, newW) -> {
			updateLoiterGo();
		});
		getChildren().add(timeDesc);
		getChildren().add(timeGet);
		// **********AVOIDANCE RADIUS OPTIONS
		avoidRadDesc = new Text("How close can the drones get to eachother?");
		avoidRadDesc.setLayoutY(260);
		getChildren().add(avoidRadDesc);
		ObservableList<Integer> radOptions = FXCollections.observableArrayList();
		for (int i = 4; i < 50; i += 2) {
			radOptions.add(i);
		}
		avoidRadiusOptions = new ComboBox<Integer>(radOptions);
		avoidRadiusOptions.setLayoutY(280);
		getChildren().add(avoidRadiusOptions);
	}

//***********LOITER GETTERS**************
	public String getMission() throws NullPointerException {
		return missionOptions.getValue();
	}

	public double[] getCenter() throws NullPointerException {
		return new double[] { Double.valueOf(xPos.getText()), Double.valueOf(yPos.getText()) };
	}

	public double getRadius() throws NullPointerException {
		return Double.valueOf(distGet.getText());
	}

	public double getTime() throws NullPointerException {
		return Double.valueOf(timeGet.getText());
	}
	public int getDroneSeparation() throws NullPointerException{
		return avoidRadiusOptions.getValue();
	}

}
