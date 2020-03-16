package Controller;

import javafx.beans.property.ListProperty;
import javafx.collections.FXCollections;
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
import javafx.scene.control.ListView;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AddFilterDialogController<string> {

    //protected ListProperty<String> listProperty = new SimpleListProperty<>();

    MainController controller;

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

    static String message= "";

    List<String> list = new ArrayList<String>();

    ObservableList<String> data = FXCollections.observableArrayList();
    ListView <String> listview = controller.filterListView;

    Set<String> hashSet = new HashSet<String>();

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
    /*
    public void CheckBoxes(CheckBox checkbox) {
        int month = 8;
        String monthString;
        switch (checkbox) {
            case 0:
                cb1.isSelected();
                message += cb1.getText() + "\n";
                list.add(cb1.getText());
            case 1:
                cb2.isSelected();
                message += cb2.getText() + "\n";
                list.add(cb2.getText());
            case 2:
                cb3.isSelected();
                message += cb3.getText() + "\n";
                list.add(cb3.getText());
                break;
        }

    }

    public void CheckBoxes1() {
        int month = 8;
        String monthString;
        switch (month) {
            case 1:
                c1.isSelected();
                message += c1.getText() + "\n";
                list.add(c1.getText());
            case 2:
                c2.isSelected();
                message += c2.getText() + "\n";
                list.add(c2.getText());
            case 3:
                c3.isSelected();
                message += c3.getText() + "\n";
                list.add(c3.getText());
            case 4:
                c4.isSelected();
                message += c4.getText() + "\n";
                list.add(c4.getText());
            case 5:
                c5.isSelected();
                message += c5.getText() + "\n";
                list.add(c5.getText());
            case 6:
                c6.isSelected();
                message += c6.getText() + "\n";
                list.add(c6.getText());
        }

    }


    public void CheckBoxes2() {
        int month = 8;
        String monthString;
        switch (month) {
            case 1:
                cbb1.isSelected();
                message += cbb1.getText() + "\n";
                list.add(cbb1.getText());
            case 2:
                cbb2.isSelected();
                message += cbb2.getText() + "\n";
                list.add(cbb2.getText());
        }

    }


    public void CheckBoxes3() {
        int month = 8;
        String monthString;
        switch (month) {
            case 1:
                cbbb1.isSelected();
                message += cbbb1.getText() + "\n";
                list.add(cbbb1.getText());
            case 2:
                cbbb2.isSelected();
                message += cbbb2.getText() + "\n";
                list.add(cbbb2.getText());
            case 3:
                cbbb3.isSelected();
                message += cbbb3.getText() + "\n";
                list.add(cbbb3.getText());
            case 4:
                cbbb4.isSelected();
                message += cbbb4.getText() + "\n";
                list.add(cbbb4.getText());
            case 5:
                cbbb5.isSelected();
                message += cbbb5.getText() + "\n";
                list.add(cbbb5.getText());

        }
    } */


    public void CheckBoxes() {
        hashSet.clear();
        System.out.println("-------------");
        ListView listview  = controller.filterListView;


        if (cb1.isSelected()) {
            int i = 0;
            message += cb1.getText() + "\n";
            hashSet.add(cb1.getText());
            listview.getItems().add(cb1.getText());
        }
        if (cb2.isSelected()) {
            message += cb2.getText() + "\n";
            hashSet.add(cb2.getText());
        }
        if (cb3.isSelected()) {
            message += cb3.getText() + "\n";
            hashSet.add(cb3.getText());
        }
        if (c1.isSelected()) {
            message += c1.getText() + "\n";
            hashSet.add(c1.getText());
        }
        if (c2.isSelected()) {
            message += c2.getText() + "\n";
            hashSet.add(c2.getText());
        }
        if (c3.isSelected()) {
            message += c3.getText() + "\n";
            hashSet.add(c3.getText());
        }
        if (c4.isSelected()) {
            message += c4.getText() + "\n";
            hashSet.add(c4.getText());
        }
        if (c5.isSelected()) {
            message += c5.getText() + "\n";
            hashSet.add(c5.getText());
        }
        if (c6.isSelected()) {
            message += c6.getText() + "\n";
            hashSet.add(c6.getText());
        }
        if (cbb1.isSelected()) {
            message += cbb1.getText() + "\n";
            hashSet.add(cbb1.getText());
        }
        if (cbb2.isSelected()) {
            message += cbb2.getText() + "\n";
            hashSet.add(cbb2.getText());
        }
        if (cbbb1.isSelected()) {
            message += cbbb1.getText() + "\n";
            hashSet.add(cbbb1.getText());
        }
        if (cbbb2.isSelected()) {
            message += cbbb2.getText() + "\n";
            hashSet.add(cbbb2.getText());
        }
        if (cbbb3.isSelected()) {
            message += cbbb3.getText() + "\n";
            hashSet.add(cbbb3.getText());
        }
        if (cbbb4.isSelected()) {
            message += cbbb4.getText() + "\n";
            hashSet.add(cbbb4.getText());
        }
        if (cbbb5.isSelected()) {
            message += cbbb5.getText() + "\n";
            hashSet.add(cbbb5.getText());
        }

        for (Object obj: hashSet){
            System.out.println(obj);
        }



    }


        /*
        string[] row = { "Hello" };
        var listViewItem = new ListViewItem(row);
        Then you need to add that row into listview like below-

        listView1.Items.Add(listViewItem);
         */









}
