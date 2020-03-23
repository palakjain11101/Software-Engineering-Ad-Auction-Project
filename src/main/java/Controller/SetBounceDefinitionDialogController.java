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

    private boolean isConfirmPressed = false;

    public void initialize(){
        secondsAfterEntryInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                secondsAfterEntryInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public void confirmButtonPressed(){
        isConfirmPressed = true;
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }

    public int getSecondsAfterEntry(){
        try {
            return Integer.parseInt(secondsAfterEntryInput.getText());
        }
        catch (NumberFormatException e){
            return Integer.MAX_VALUE;
        }
    }

    public boolean getNeedToConvert(){
        return needToConverseCheckBox.isSelected();
    }

    public boolean getIsConfirmPressed(){
        return isConfirmPressed;
    }
}
