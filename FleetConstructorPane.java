
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class FleetConstructorPane extends Pane {
	private ComboBox<Integer> fleetCount;
	private Text countDesc, posDesc, speedDesc;
	private TextField xPos, yPos;
	private ComboBox<Double> minSpeed, maxSpeed, maxAcceleration;
	private ObservableList<Double> placeHolders;
	private Button makeButton;
	private Fleet madeFleet;
	protected BooleanProperty fleetConstructedProperty;

	public FleetConstructorPane() {
		makeCountBox();
		makePosBox();
		makeSpeedComboBox();
		makeButton();
		fleetConstructedProperty = new SimpleBooleanProperty();
		fleetConstructedProperty.setValue(false);
		xPos.textProperty().setValue("0");//UNCOMMENT, FOR TESTING
		yPos.textProperty().setValue("0");//UNCOMMENT< FOR TESTING
		fleetCount.setValue(9);//COMMENT OUT< FOR TESTING
		maxSpeed.setValue((double) 8);
		minSpeed.setValue(8.);
		maxAcceleration.setValue(4.);
	}

	public Fleet getFleet() throws NullPointerException {
		Fleet tempFleet;
		if (madeFleet != null) {
			tempFleet = madeFleet.clone();
		} else {
			throw new NullPointerException("No Fleet has Been Created Yet");
		}
		return tempFleet;
	}

	public void setEditable(boolean val) {
		fleetCount.setDisable(!val);
		xPos.setEditable(val);
		yPos.setEditable(val);
		minSpeed.setDisable(!val);
		maxSpeed.setDisable(!val);
		maxAcceleration.setDisable(!val);
	}

	private void makeCountBox() {
		// Text
		countDesc = new Text("How many drones shall be in the fleet");
		countDesc.setLayoutY(18);
		getChildren().add(countDesc);

		// ComboBox
		ObservableList<Integer> list = FXCollections.observableArrayList();
		for (int i = 1; i < 10; i++) {
			list.add(i);
		}
		fleetCount = new ComboBox<Integer>(list);
		
		fleetCount.setLayoutY(25);
		fleetCount.setCenterShape(true);
		fleetCount.valueProperty().addListener((observ, oldVal, newVal) -> {
			updateButton();
		});
		getChildren().add(fleetCount);
	}

	private void makePosBox() {
		// Text
		posDesc = new Text("Starting Position of the Drones?");
		posDesc.setY(80);
		getChildren().add(posDesc);

		// TextFields
		xPos = new TextField();
		yPos = new TextField();
		
		
		xPos.setMaxWidth(50);
		yPos.setMaxWidth(50);
		xPos.setLayoutX(100);
		xPos.setLayoutY(90);
		yPos.setLayoutY(90);
		xPos.textProperty().addListener((observ, oldVal, newVal) -> {
			updateButton();
		});
		yPos.textProperty().addListener((observ, oldVal, newVal) -> {
			updateButton();
		});
		getChildren().add(xPos);
		getChildren().add(yPos);
	}
	
	private void makeSpeedComboBox() {
		// Text
		speedDesc = new Text("MinSpeed:      MaxSpeed:       MaxAccel:");
		speedDesc.setLayoutY(150);
		getChildren().add(speedDesc);

		// ComboBox
		placeHolders = FXCollections.observableArrayList();
		for (int i = 1; i < 20; i++) {
			placeHolders.add((double) i / 2);
		}
		minSpeed = new ComboBox<Double>(placeHolders);
		minSpeed.setLayoutY(155);
		minSpeed.valueProperty().addListener((observ, oldVal, newVal) -> {
			updateButton();
		});
		maxSpeed = new ComboBox<Double>(placeHolders);
		maxSpeed.setLayoutY(155);
		maxSpeed.setLayoutX(95);
		maxSpeed.valueProperty().addListener((observ, oldVal, newVal) -> {
			updateButton();
		});
		maxAcceleration = new ComboBox<Double>(placeHolders);
		maxAcceleration.setLayoutY(155);
		maxAcceleration.setLayoutX(190);
		maxAcceleration.valueProperty().addListener((observ, oldVal, newVal) -> {
			updateButton();
		});
		// maxAcceleration.

		getChildren().add(minSpeed);
		getChildren().add(maxSpeed);
		getChildren().add(maxAcceleration);
	}

	private void makeButton() {
		makeButton = new Button("MakeFleet");
		makeButton.setLayoutY(200);
		makeButton.setLayoutX(75);
		makeButton.setDisable(true);
		makeButton.setOnMouseClicked(e -> {

			Drone[] tempfleet = new Drone[fleetCount.getValue()];
			for (int i = 0; i < fleetCount.getValue(); i++) {
				Drone tempdrone = new Drone(new double[] { Double.valueOf(xPos.getText()), Double.valueOf(yPos.getText()) },
						minSpeed.getValue(), maxSpeed.getValue(), maxAcceleration.getValue());
				tempfleet[i]=tempdrone;
			}
			madeFleet = new Fleet(tempfleet);
			fleetConstructedProperty.setValue(true);
		});
		getChildren().add(makeButton);
	}

	private void updateButton() {
		boolean AllTrue, countFull, xPosFull, yPosFull, minFull, maxVelFull, maxAccFull;
		try {
			countFull = (fleetCount.getValue() > 0);
			yPosFull = (Double.valueOf(yPos.getText()) != null);
			xPosFull = (Double.valueOf(xPos.getText()) != null);
			minFull = (minSpeed.getValue() > 0);
			maxVelFull = (maxSpeed.getValue() > 0);
			maxAccFull = (maxAcceleration.getValue() > 0);

			AllTrue = (countFull && yPosFull && xPosFull && minFull && maxVelFull && maxAccFull);

			if (AllTrue) {
				makeButton.setDisable(false);
			}
		} catch (NullPointerException e) {
			makeButton.setDisable(true);
		} catch (NumberFormatException e) {
			makeButton.setDisable(true);
		}
	}

}
