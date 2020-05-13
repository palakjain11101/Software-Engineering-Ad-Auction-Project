package View;

import Controller.MainController;
import Model.MainModel;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Slider;
import javafx.stage.*;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainView extends Application {

    private Stage stage;
    private Parent root;
    private Alert waitingBox;

    private MainController controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        //root = FXMLLoader.load(getClass().getResource("/View/mainScene.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mainScene.fxml"));
        root = fxmlLoader.load();
        MainController controller = fxmlLoader.getController();
        controller.setView(this);
        controller.setModel(new MainModel());

        stage = primaryStage;
        setUpTimeGranulationSlider(controller);

        primaryStage.setTitle("Dashboard");
        root.getStylesheets().add("/styles.css");
        primaryStage.setScene(new Scene(root, 300, 275));

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        primaryStage.setMaximized(true);
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(900);

        primaryStage.show();

        Task task = new Task<HashMap<String, List<CampaignTab.CampaignDataPackage>>>() {
            @Override
            protected HashMap<String, List<CampaignTab.CampaignDataPackage>> call() {
                return controller.loadAllDataFromEarlierCampaigns();
            }
        };
        task.setOnRunning((e) -> showLoadingDialog());
        task.setOnSucceeded((e) -> {
            hideLoadingDialog();
            HashMap<String, List<CampaignTab.CampaignDataPackage>> campaignData = ((Task<HashMap<String, List<CampaignTab.CampaignDataPackage>>>) task).getValue();
            for(String campaignID : campaignData.keySet()){
                controller.createAndAddTab(campaignID, (ArrayList<CampaignTab.CampaignDataPackage>) campaignData.get(campaignID));
            }
        });

        new Thread(task).start();
    }

    private void setUpTimeGranulationSlider(MainController controller){
        Slider slider = (Slider) root.lookup("#timeGranulationSlider");
        slider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double d) {
                switch (d.intValue()){
                    case MainController.SLIDER_DAY:
                        return "Days";
                    case MainController.SLIDER_WEEK:
                        return "Weeks";
                    case MainController.SLIDER_MONTH:
                        return "Months";
                    case MainController.SLIDER_YEAR:
                        return "Years";
                }
                return "";
            }

            @Override
            public Double fromString(String s) {
                return null;
            }
        });
        slider.setOnMouseReleased(mouseEvent -> {
            controller.recreateGraph((int) slider.getValue());
        });
        //controller.recreateGraph(0);
    }

    public Window getWindow(){
        return root.getScene().getWindow();
    }

    public void showErrorMessage(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    public void showLoadingDialog(){
        waitingBox = new Alert(Alert.AlertType.NONE,"Please wait...");
        waitingBox.initOwner(this.getWindow());
        waitingBox.show();
    }

    public void hideLoadingDialog(){
        waitingBox.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
        waitingBox.hide();
    }

    /*
    Toggles between dark and light mode themes for the application window
     */
    public void darkModeToggle(boolean isSelected) {

        if (isSelected) {
            root.getStylesheets().removeAll(root.getStylesheets());
            root.getStylesheets().add("/styles_darkmode.css");

        }

        else {
            root.getStylesheets().removeAll(root.getStylesheets());
            root.getStylesheets().add("/styles.css");

        }

    }

    public void textSizeChange(Integer size, boolean isSelected) {

        StringBuilder sb = new StringBuilder("/styles.css");
        StringBuilder sbd = new StringBuilder("/styles_darkmode.css");

//        root.getStylesheets().removeAll(root.getStylesheets());
//        sb.insert(7, Integer.toString(size));
//        root.getStylesheets().add(sb.toString());

        if(isSelected) {
            root.getStylesheets().removeAll(root.getStylesheets());
            sbd.insert(16, Integer.toString(size));
            root.getStylesheets().add(sb.toString());
        }

        else {
            root.getStylesheets().removeAll(root.getStylesheets());
            sb.insert(7, Integer.toString(size));
            root.getStylesheets().add(sb.toString());
        }

    }

}
