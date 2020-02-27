package Controller;

import Model.MainModel;
import View.CampaignTab;
import View.MainView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;

import java.io.File;

public class MainController {
    public static final int SLIDER_DAY = 0;
    public static final int SLIDER_WEEK = 1;
    public static final int SLIDER_MONTH = 2;
    public static final int SLIDER_YEAR = 3;


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

    public void onTimeGranulationSliderChanged(int value){
        NumberAxis axis = (NumberAxis) lineChart.getXAxis();
        switch (value){
            case SLIDER_DAY:
                axis.setUpperBound(365);
                axis.setTickUnit(5);
                return;
            case SLIDER_WEEK:
                axis.setUpperBound(52);
                axis.setTickUnit(4);
                return;
            case SLIDER_MONTH:
                axis.setUpperBound(12);
                axis.setTickUnit(1);
                return;
            case SLIDER_YEAR:
                axis.setUpperBound(1);
                axis.setTickUnit(1);
                return;
        }
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
