package Controller;

import Model.MainModel;
import View.CampaignTab;
import View.MainView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    public static final int SLIDER_DAY = 0;
    public static final int SLIDER_WEEK = 1;
    public static final int SLIDER_MONTH = 2;
    public static final int SLIDER_YEAR = 3;


    private MainView view;
    private MainModel model;

    private ArrayList<Point> graphData = new ArrayList<>();

    private File clickLogCSV;
    private File impressionLogCSV;
    private File serverLogCSV;

    @FXML
    LineChart<Number, Number> lineChart;

    @FXML
    TabPane tabPane;

    @FXML
    Slider timeGranulationSlider;

    public MainController(){
        graphData.add(new Point(122,200));
        graphData.add(new Point(45,21));
        graphData.add(new Point(231,210));
        graphData.add(new Point(77,89));
        graphData.add(new Point(300,341));
        graphData.add(new Point(244,90));
        graphData.add(new Point(188,100));
        graphData.add(new Point(157,117));
        graphData.add(new Point(90,165));
        graphData.add(new Point(100,169));
        graphData.add(new Point(362,198));
    }

    public void setView(MainView view){
        this.view = view;
    }

    public void setModel(MainModel model){
        this.model = model;
    }

    public void onTimeGranulationSliderChanged(int newValue){
        NumberAxis axis = (NumberAxis) lineChart.getXAxis();
        lineChart.getData().clear();
        lineChart.getData().add(createSeries(newValue));
        switch (newValue){
            case SLIDER_DAY:
                axis.setUpperBound(365);
                axis.setTickUnit(15);
                axis.setLabel("Days Passed");
                return;
            case SLIDER_WEEK:
                axis.setUpperBound(52);
                axis.setTickUnit(4);
                axis.setLabel("Weeks Passed");
                return;
            case SLIDER_MONTH:
                axis.setUpperBound(12);
                axis.setTickUnit(1);
                axis.setLabel("Months Passed");
                return;
            case SLIDER_YEAR:
                axis.setUpperBound(1);
                axis.setTickUnit(1);
                axis.setLabel("Years Passed");
                return;
        }
    }

    private XYChart.Series<Number, Number> createSeries(int value){
        double divider = 1;
        switch (value){
            case SLIDER_DAY:
                divider = 1;
                break;
            case SLIDER_WEEK:
                divider = 7;
                break;
            case SLIDER_MONTH:
                divider = 30;
                break;
            case SLIDER_YEAR:
                divider = 365;
                break;
        }

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for(Point point : graphData){
            series.getData().add(new XYChart.Data<>(Math.round(point.x/divider), point.y));
        }
        series.setName("Month Pay");
        return series;
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
