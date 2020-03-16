package Controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.*;

public class SetBounceDefinitionDialogController {

    @FXML
    TextField secondsAfterEntryInput;

    @FXML
    CheckBox needToConverseCheckBox;

    @FXML
    Button confirmButton;

    public void initialize(){
        secondsAfterEntryInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                secondsAfterEntryInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public void confirmButtonPressed(){
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }

    public int getSecondsAfterEntry(){
        return Integer.parseInt(secondsAfterEntryInput.getText());
    }

    public boolean getNeedToConvert(){
        return needToConverseCheckBox.isSelected();
    }
}
