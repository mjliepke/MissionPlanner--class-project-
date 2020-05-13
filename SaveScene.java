import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SaveScene extends Stage {
	protected SavePane mainPane;
	protected Button saveButton;

	public SaveScene() {
		mainPane = new SavePane();
		setScene(new Scene(mainPane, 250, 175));
		setTitle("SaveFlightLogs");

		show();
	}

	public String getFileName() throws NullPointerException {
		String fileName = null;
		if (mainPane.folderInput.getText().isEmpty()) {
			throw new NullPointerException("FileName Not Initialized");
		}
		fileName = mainPane.folderInput.getText();
		return fileName;

	}

	protected class SavePane extends Pane {
		private Text folderDesc;
		protected TextField folderInput;

		public SavePane() {
			makeText();
			makeButton();

		}

		private void makeText() {
			folderDesc = new Text("FolderName:");
			folderDesc.setLayoutY(30);
			folderDesc.setLayoutX(80);
			getChildren().add(folderDesc);
			folderInput = new TextField();
			getChildren().add(folderInput);
			folderInput.textProperty().setValue("Log_1");
			folderInput.setMaxWidth(175);
			folderInput.setLayoutY(70);
			folderInput.setLayoutX(40);
			folderInput.textProperty().addListener((observ, oldV, newV) -> {
				if (!newV.isEmpty() && Character.isLetter(newV.charAt(0))) {// Is the text input file-able?
					showSaveButton(true);
				} else {
					showSaveButton(false);
				}
			});
		}

		private void makeButton() {
			saveButton = new Button("SaveLog");
			saveButton.setLayoutY(130);
			// saveButton.setDisable(false);//uncomment to always show button enabled(not
			// recommended)
			saveButton.setLayoutX(80);
			getChildren().add(saveButton);

		}

		private void showSaveButton(boolean val) {
			saveButton.setDisable(!val);
		}
	}
}
