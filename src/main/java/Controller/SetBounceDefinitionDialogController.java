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
    private TextField secondsAfterEntryInput;

    @FXML
    private TextField pagesVisitedInput;

    @FXML
    private CheckBox secondsAfterEntryCheckbox;

    @FXML
    private CheckBox pagesVisitedCheckbox;

    @FXML
    private Button confirmButton;

    private boolean isConfirmPressed = false;

    public void initialize(){
        secondsAfterEntryInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                secondsAfterEntryInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        pagesVisitedInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                pagesVisitedInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    @FXML
    public void secondsAfterEntryCheckboxSelected(){
        if(secondsAfterEntryCheckbox.isSelected()){
            secondsAfterEntryInput.setDisable(false);
        }
        else {
            secondsAfterEntryInput.setDisable(true);
            secondsAfterEntryInput.clear();
        }
    }

    @FXML
    public void pagesVisitedCheckboxSelected(){
        if(pagesVisitedCheckbox.isSelected()){
            pagesVisitedInput.setDisable(false);
        }
        else {
            pagesVisitedInput.setDisable(true);
            pagesVisitedInput.clear();
        }
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

    public int getMaxPagesVisited(){
        try {
            return Integer.parseInt(pagesVisitedInput.getText());
        }
        catch (NumberFormatException e){
            return Integer.MAX_VALUE;
        }
    }

    public boolean getIsConfirmPressed(){
        return isConfirmPressed;
    }
}
