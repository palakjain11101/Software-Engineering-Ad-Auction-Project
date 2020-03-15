package Controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddFilterDialogController {
    List myList = new ArrayList();
    @FXML
    private CheckBox cb1;
    @FXML
    private CheckBox cb2;
    @FXML
    private CheckBox cb3;
    @FXML
    private CheckBox c1;
    @FXML
    private CheckBox c2;
    @FXML
    private CheckBox c3;
    @FXML
    private CheckBox c4;
    @FXML
    private CheckBox c5;
    @FXML
    private CheckBox c6;
    @FXML
    private CheckBox cbb1;
    @FXML
    private CheckBox cbb2;
    @FXML
    private CheckBox cbbb1;
    @FXML
    private CheckBox cbbb2;
    @FXML
    private CheckBox cbbb3;
    @FXML
    private CheckBox cbbb4;
    @FXML
    private CheckBox cbbb5;

    public AddFilterDialogController(){
    }

    public void setUpDialogController() {
    }

    /*public void setUpDialogController(){
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

    }*/





    @FXML public void AddFilterDialogListViewElement(ActionEvent actionEvent) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/AddFilterDialog.fxml"));
//        Parent parent = fxmlLoader.load();
//        Scene scene = new Scene(parent, 300, 200);
//        Stage stage = new Stage();
//        stage.initModality(Modality.APPLICATION_MODAL);
//        stage.setScene(scene);
//        AddFilterDialogController dialogController = fxmlLoader.getController();
//        dialogController.setUpDialogController();
//        stage.showAndWait();
//
//        addFilterDialogListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//        ObservableList selectedIndices = addFilterDialogListView.getSelectionModel().getSelectedIndices();
//
//        for(Object o : selectedIndices){
//            System.out.println("o = " + o + " (" + o.getClass() + ")");
//        }
//
//        // Register the filter for another event type
//

    }

    //3
    @FXML public void CheckBoxes(ActionEvent actionEvent) {
        String message = "";

        if(cb1.isSelected()){
            message += cb1.getText() + "\n";
        }
        else if(cb2.isSelected()){
            message += cb2.getText() + "\n";
        }
        else if(cb3.isSelected()){
            message += cb3.getText() + "\n";
        }

        System.out.println(message);

    }

    //6

    @FXML public void CheckBoxes1(ActionEvent actionEvent) {
        String message = "";

        if(c1.isSelected()){
            message += c1.getText() + "\n";
        }
        else if(c2.isSelected()){
            message += c2.getText() + "\n";
        }
        else if(c3.isSelected()){
            message += c3.getText() + "\n";
        }
        else if(c4.isSelected()){
            message += c4.getText() + "\n";
        }
        else if(c5.isSelected()){
            message += c5.getText() + "\n";
        }
        else if(c6.isSelected()){
            message += c6.getText() + "\n";
        }

        System.out.println(message);

    }

    //2
    @FXML public void CheckBoxes2(ActionEvent actionEvent) {
        String message = "";

        if(cb1.isSelected()){
            message += cbb1.getText() + "\n";
        }
        else if(cb2.isSelected()){
            message += cbb2.getText() + "\n";
        }
        System.out.println(message);

    }

    //5
    @FXML public void CheckBoxes3(ActionEvent actionEvent) {
        String message = "";

        if(cb1.isSelected()){
            message += cbbb1.getText() + "\n";
        }
        else if(cb2.isSelected()){
            message += cbbb2.getText() + "\n";
        }
        else if(cb3.isSelected()){
            message += cbbb3.getText() + "\n";
        }
        else if(cb2.isSelected()){
            message += cbbb4.getText() + "\n";
        }
        else if(cb2.isSelected()){
            message += cbbb5.getText() + "\n";
        }

        System.out.println(message);

    }







}
