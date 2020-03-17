package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

import java.io.IOException;
import java.util.*;


public class AddFilterDialogController<string> {

    //protected ListProperty<String> listProperty = new SimpleListProperty<>();

    MainController controller;

    Map<String,String> mymap = new HashMap<String, String>();

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



    ObservableList<String> data = FXCollections.observableArrayList();


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


    public HashMap<String, List<String>> CheckBoxes() {
        HashMap<String, List<String>> map = new HashMap<>();
        List<String> list = new ArrayList<String>();
        List<String> list1 = new ArrayList<String>();
        List<String> list2 = new ArrayList<String>();
        List<String> list3 = new ArrayList<String>();


        map.put("Income", list);
        map.put("Context", list1);
        map.put("Gender", list2);
        map.put("Age", list3);

        System.out.println("-------------");


        if (cb1.isSelected()) {
            int i = 0;
            message += cb1.getText() + "\n";
            map.get("Income").add(cb1.getText());
        }
        if (cb2.isSelected()) {
            message += cb2.getText() + "\n";
            map.get("Income").add(cb2.getText());
        }
        if (cb3.isSelected()) {
            message += cb3.getText() + "\n";
            map.get("Income").add(cb3.getText());
        }
        if (c1.isSelected()) {
            message += c1.getText() + "\n";
            map.get("Income").add(c1.getText());
        }
        if (c2.isSelected()) {
            message += c2.getText() + "\n";
            map.get("Context").add(c2.getText());
        }
        if (c3.isSelected()) {
            message += c3.getText() + "\n";
            map.get("Context").add(c3.getText());
        }
        if (c4.isSelected()) {
            message += c4.getText() + "\n";
            map.get("Context").add(c4.getText());
        }
        if (c5.isSelected()) {
            message += c5.getText() + "\n";
            map.get("Context").add(c5.getText());
        }
        if (c6.isSelected()) {
            message += c6.getText() + "\n";
            map.get("Context").add(c6.getText());
        }
        if (cbb1.isSelected()) {
            message += cbb1.getText() + "\n";
            map.get("Gender").add(cbb1.getText());
        }
        if (cbb2.isSelected()) {
            message += cbb2.getText() + "\n";
            map.get("Gender").add(cbb2.getText());
        }
        if (cbbb1.isSelected()) {
            message += cbbb1.getText() + "\n";
            map.get("Age").add("<25");
        }
        if (cbbb2.isSelected()) {
            message += cbbb2.getText() + "\n";
            map.get("Age").add("25-34");
        }
        if (cbbb3.isSelected()) {
            message += cbbb3.getText() + "\n";
            map.get("Age").add("35-44");
        }
        if (cbbb4.isSelected()) {
            message += cbbb4.getText() + "\n";
            map.get("Age").add("45-54");
        }
        if (cbbb5.isSelected()) {
            message += cbbb5.getText() + "\n";
            map.get("Age").add(">55");
        }



        return map;
    }







    }


        /*
        string[] row = { "Hello" };
        var listViewItem = new ListViewItem(row);
        Then you need to add that row into listview like below-

        listView1.Items.Add(listViewItem);
         */










