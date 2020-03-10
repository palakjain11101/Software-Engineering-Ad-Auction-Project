package Controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.SelectionMode;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class AddFilterDialogController {

    @FXML
    ListView addFilterDialogListView;

    public AddFilterDialogController(){
    }

    public void setUpDialogController(){
        DatePicker dateBeforePicker = new DatePicker();
        DatePicker dateAfterPicker = new DatePicker();
        ComboBox ageComboBox = new ComboBox();
        ComboBox genderComboBox = new ComboBox();
        ComboBox contextComboBox = new ComboBox();
        ComboBox incomeComboBox = new ComboBox();

        addFilterDialogListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ageComboBox.getItems().addAll("<25","25-34","35-44","45-54",">55");
        genderComboBox.getItems().addAll("Male","Female");
        contextComboBox.getItems().addAll("News","Shopping","Social Media","Blog","Hobbies","Travel");
        incomeComboBox.getItems().addAll("Low","Medium","High");


        addFilterDialogListView.getItems().add(dateBeforePicker);
        addFilterDialogListView.getItems().add(dateAfterPicker);
        addFilterDialogListView.getItems().add(ageComboBox);
        addFilterDialogListView.getItems().add(genderComboBox);
        addFilterDialogListView.getItems().add(contextComboBox);
        addFilterDialogListView.getItems().add(incomeComboBox);

        addFilterDialogListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {

        });

    }





    @FXML public void AddFilterDialogListViewElement(ActionEvent actionEvent) throws IOException {
        /*FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/AddFilterDialog.fxml"));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent, 300, 200);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        AddFilterDialogController dialogController = fxmlLoader.getController();
        dialogController.setUpDialogController();
        stage.showAndWait(); */

        addFilterDialogListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ObservableList selectedIndices = addFilterDialogListView.getSelectionModel().getSelectedIndices();

        for(Object o : selectedIndices){
            System.out.println("o = " + o + " (" + o.getClass() + ")");
        }

        // Register the filter for another event type


    }

}
