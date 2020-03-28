package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


public class AddFilterDialogController<string> {

    //protected ListProperty<String> listProperty = new SimpleListProperty<>();

    MainController controller;

    Map<String,String> mymap = new HashMap<String, String>();

    @FXML
    DatePicker dateBefore;
    @FXML
    DatePicker dateAfter;
    @FXML
    private CheckBox lowIncome;
    @FXML
    private CheckBox mediumIncome;
    @FXML
    private CheckBox highIncome;
    @FXML
    private CheckBox newsContext;
    @FXML
    private CheckBox blogContext;
    @FXML
    private CheckBox shoppingContext;
    @FXML
    private CheckBox hobbiesContext;
    @FXML
    private CheckBox socialContext;
    @FXML
    private CheckBox travelContext;
    @FXML
    private CheckBox Age25;
    @FXML
    private CheckBox Age45;
    @FXML
    private CheckBox Age25To34;
    @FXML
    private CheckBox Age55;
    @FXML
    private CheckBox Age35;
    @FXML
    private CheckBox maleGender;
    @FXML
    private CheckBox femaleGender;

    static String message= "";



    ObservableList<String> data = FXCollections.observableArrayList();


    Set<String> hashSet = new HashSet<String>();

    public AddFilterDialogController(){
    }






    public HashMap<String, List<String>> CheckBoxes() {
        HashMap<String, List<String>> map = new HashMap<>();
        List<String> list = new ArrayList<String>();
        List<String> list1 = new ArrayList<String>();
        List<String> list2 = new ArrayList<String>();
        List<String> list3 = new ArrayList<String>();
        List<String> list4 = new ArrayList<String>();
        List<String> list5 = new ArrayList<String>();

        LocalDate before = dateBefore.getValue();
        LocalDate after  = dateAfter.getValue();

        map.put("Income", list);
        map.put("Context", list1);
        map.put("Gender", list2);
        map.put("Age", list3);
        map.put("Date Before:", list4);
        map.put("Date After:", list5);

        System.out.println("-------------");

        //lowIncome
        if (lowIncome.isSelected()) {
            message += lowIncome.getText() + "\n";
            if (list.contains(lowIncome.getText())) {
                System.out.println("Already added");
            }
            else{
                list.add(lowIncome.getText());
            }
            System.out.println("Elements of the list:" );
            for(Object element: list){
                System.out.println("" + element);

            }
        }
        if (mediumIncome.isSelected()) {
            message += mediumIncome.getText() + "\n";
            if (!list.contains(mediumIncome.getText())) {
                list.add(mediumIncome.getText());
            }
        }
        if (highIncome.isSelected()) {
            message += highIncome.getText() + "\n";
            if (!list.contains(highIncome.getText())) {
                list.add(highIncome.getText());
            }
        }
        if (newsContext.isSelected()) {
            message += newsContext.getText() + "\n";
            map.get("Context").add(newsContext.getText());
        }
        if (blogContext.isSelected()) {
            message += blogContext.getText() + "\n";
            map.get("Context").add(blogContext.getText());
        }
        if (shoppingContext.isSelected()) {
            message += shoppingContext.getText() + "\n";
            map.get("Context").add(shoppingContext.getText());
        }
        if (hobbiesContext.isSelected()) {
            message += hobbiesContext.getText() + "\n";
            map.get("Context").add(hobbiesContext.getText());
        }
        if (socialContext.isSelected()) {
            message += socialContext.getText() + "\n";
            map.get("Context").add(socialContext.getText());
        }
        if (travelContext.isSelected()) {
            message += travelContext.getText() + "\n";
            map.get("Context").add(travelContext.getText());
        }
        if (maleGender.isSelected()) {
            message += maleGender.getText() + "\n";
            map.get("Gender").add(maleGender.getText());
        }
        if (femaleGender.isSelected()) {
            message += femaleGender.getText() + "\n";
            map.get("Gender").add(femaleGender.getText());
        }
        if (Age25.isSelected()) {
            message += Age25.getText() + "\n";
            map.get("Age").add("<25");
        }
        if (Age25To34.isSelected()) {
            message += Age25To34.getText() + "\n";
            map.get("Age").add("25-34");
        }
        if (Age35.isSelected()) {
            message += Age35.getText() + "\n";
            map.get("Age").add("35-44");
        }
        if (Age45.isSelected()) {
            message += Age45.getText() + "\n";
            map.get("Age").add("45-54");
        }
        if (Age55.isSelected()) {
            message += Age55.getText() + "\n";
            map.get("Age").add(">55");
        }
        if (before != null){
            System.out.println("Selected date: " + before);
            String beforeDate = ("") + before;
            System.out.println(beforeDate);
            map.get("Date Before").add(beforeDate);

        }
        if (after != null){
            System.out.println("Selected date: " + after);
            String afterDate = ("") + after;
            System.out.println(afterDate);
            map.get("Date After").add(afterDate);
        }

        System.out.println(map);

        return map;
    }







    }










