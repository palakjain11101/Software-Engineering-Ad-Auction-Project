package Controller;

import Model.MainModel;
import View.CampaignTab;
import View.MainView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;

import java.io.File;

public class MainController {
    private MainView view;
    private MainModel model;

    private File clickLogCSV;
    private File impressionLogCSV;
    private File serverLogCSV;

    @FXML
    LineChart<Number, Number> lineChart;

    @FXML
    TabPane tabPane;

    public void setView(MainView view){
        this.view = view;
    }

    public void setModel(MainModel model){
        this.model = model;
    }

    @FXML public void btn(ActionEvent event){
        lineChart.getData().clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>(1, 200));
        series.getData().add(new XYChart.Data<>(2, 100));
        series.getData().add(new XYChart.Data<>(3, 300));
        series.getData().add(new XYChart.Data<>(4, 230));
        series.getData().add(new XYChart.Data<>(5, 230));
        series.getData().add(new XYChart.Data<>(6, 230));
        series.getData().add(new XYChart.Data<>(7, 230));
        series.getData().add(new XYChart.Data<>(8, 230));
        series.getData().add(new XYChart.Data<>(9, 230));
        series.getData().add(new XYChart.Data<>(10, 230));
        series.getData().add(new XYChart.Data<>(11, 230));
        series.getData().add(new XYChart.Data<>(12, 230));
        series.setName("Month Pay");
        lineChart.getData().add(series);
    }

    @FXML public void loadClickLogPressed(){
        clickLogCSV = view.showFileChooser();
    }

    @FXML public void loadImpressionLogPressed(){
        impressionLogCSV = view.showFileChooser();

    }

    @FXML public void loadServerLogPressed(){
        serverLogCSV = view.showFileChooser();
    }

    @FXML public void loadCampaignPressed(){
        CampaignTab tab = new CampaignTab();
        tabPane.getTabs().add(tab);

        if(clickLogCSV == null){
            view.showErrorMessage("Click Log file needed");
            return;
        }
        if(impressionLogCSV == null){
            view.showErrorMessage("Impression Log file needed");
            return;
        }
        if(serverLogCSV == null){
            view.showErrorMessage("Server Log file needed");
            return;
        }

        //Pass data to model here


    }
}
