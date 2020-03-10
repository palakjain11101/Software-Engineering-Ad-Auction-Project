package Controller;

import Model.GraphPoint;
import Model.MainModel;
import View.CampaignTab;
import View.MainView;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MainController {
    public static final int SLIDER_DAY = 0;
    public static final int SLIDER_WEEK = 1;
    public static final int SLIDER_MONTH = 2;
    public static final int SLIDER_YEAR = 3;

    private MainView view;
    private MainModel model;

    private ArrayList<GraphPoint> graphData = new ArrayList<>();
    private boolean shouldGraphAvg = true; //Otherwise just sum
    private int timeGranulationValue = SLIDER_DAY;

    private File clickLogCSV;
    private File impressionLogCSV;
    private File serverLogCSV;

    @FXML
    LineChart<Number, Number> lineChart;

    @FXML
    TabPane tabPane;

    @FXML
    Slider timeGranulationSlider;

    @FXML
    ListView filterListView;

    public void setView(MainView view){
        this.view = view;
    }

    public void setModel(MainModel model){
        this.model = model;
    }

    public void recreateGraph(int timeGranularityValue){
        timeGranulationValue = timeGranularityValue;
        NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
        xAxis.setLowerBound(1);
        lineChart.getData().clear();
        lineChart.getData().add(createSeries(timeGranularityValue));
        switch (timeGranularityValue){
            case SLIDER_DAY:
                xAxis.setUpperBound(graphData.size());
                xAxis.setTickUnit(1);
                xAxis.setLabel("Day of Campaign");
                return;
            case SLIDER_WEEK:
                xAxis.setUpperBound(Math.round(graphData.size()/7.0));
                xAxis.setTickUnit(1);
                xAxis.setLabel("Week of Campaign");
                return;
            case SLIDER_MONTH:
                xAxis.setUpperBound(Math.round(graphData.size()/30.0));
                xAxis.setTickUnit(1);
                xAxis.setLabel("Month of Campaign");
                return;
            case SLIDER_YEAR:
                xAxis.setUpperBound(Math.round(graphData.size()/365.0));
                xAxis.setTickUnit(1);
                xAxis.setLabel("Year of Campaign");
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
        int previousX = 0;
        int nextX;
        int count = 0;
        double total = 0;
        for(GraphPoint point : graphData){
            nextX = (int) Math.floor(point.getX()/divider);
            if(previousX < nextX){
                if(shouldGraphAvg){
                    total /= count;
                }
                series.getData().add(new XYChart.Data<>(previousX+1, total));
                total = point.getY();
                count = 1;
                previousX = nextX;
            }
            else {
                total += point.getY();
                count++;
            }
        }
        if(shouldGraphAvg){
            total /= count;
        }
        series.getData().add(new XYChart.Data<>(previousX+1, total));
        series.setName("Data");
        return series;
    }

    @FXML public void loadClickLogPressed(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
        clickLogCSV = fileChooser.showOpenDialog(view.getWindow());
    }

    @FXML public void loadImpressionLogPressed(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
        impressionLogCSV = fileChooser.showOpenDialog(view.getWindow());

    }

    @FXML public void loadServerLogPressed(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
        serverLogCSV = fileChooser.showOpenDialog(view.getWindow());
    }

    @FXML public void loadCampaignPressed(){
        final String[] error = new String[1];
        if(clickLogCSV == null){
            view.showErrorMessage("Click Log file needed");
        }
        else if(impressionLogCSV == null){
            view.showErrorMessage("Impression Log file needed");
        }
        else if(serverLogCSV == null){
            view.showErrorMessage("Server Log file needed");
        }
        else if(!clickLogCSV.getName().endsWith(".csv")){
            view.showErrorMessage("Click Log file must be a CSV file");
        }
        else if(!impressionLogCSV.getName().endsWith(".csv")){
            view.showErrorMessage("Impression Log file must be a CSV file");
        }
        else if(!serverLogCSV.getName().endsWith(".csv")){
            view.showErrorMessage("Server Log file must be a CSV file");
        }
        else{
            Task task = new Task<ArrayList<CampaignTab.CampaignDataPackage>>() {
                @Override
                protected ArrayList<CampaignTab.CampaignDataPackage> call() {
                    error[0] = model.createNewCampaign(clickLogCSV,impressionLogCSV,serverLogCSV);
                    if(error[0] == null){
                        return model.loadCampaign();
                    }
                    else {
                        return null;
                    }
                }
            };

            task.setOnRunning((e) -> {
                view.showLoadingDialog();
            });
            task.setOnSucceeded((e) -> {
                view.hideLoadingDialog();
                try {
                    if(error[0] == null) {
                        CampaignTab tab = new CampaignTab(this,((Task<ArrayList<CampaignTab.CampaignDataPackage>>) task).getValue());
                        clickLogCSV = null;
                        impressionLogCSV = null;
                        serverLogCSV = null;
                        tabPane.getTabs().add(tab);
                        tabPane.getSelectionModel().select(tab);
                    }
                    else {
                        view.showErrorMessage(error[0]);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });

            new Thread(task).start();
        }
    }

    public void metricSelectedOnCampaignTab(CampaignTab.CampaignDataPackage metricSelected, String database){
        if(metricSelected == null){return;}
        graphData = metricSelected.getMetricOverTimePoints();

        String id = metricSelected.getID();
        shouldGraphAvg = !id.equals("Number of Impressions") && !id.equals("Number of Clicks") && !id.equals("Number of Uniques") && !id.equals("Number of Bounces") && !id.equals("Number of Conversions") && !id.equals("Number of Cost");

        lineChart.getYAxis().setLabel(id);
        lineChart.setTitle(id + " Over Time");

        recreateGraph(timeGranulationValue);
    }

    @FXML public void addFilterButtonPressed() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/addFilterDialog.fxml"));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent, 300, 200);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        AddFilterDialogController dialogController = fxmlLoader.getController();
        dialogController.setUpDialogController();
        stage.showAndWait();

        filterListView.getItems().add("hello world!");
        // Register the filter for another event type
    }

    @FXML public void removeFilterButtonPressed(){
        filterListView.getItems().remove(filterListView.getSelectionModel().getSelectedItem());
    }


    //TEST BUTTON ONLY
    public void onTestButtonPressed(){
        HashMap map = new HashMap();
        map.put("gender","Male");
        ArrayList<CampaignTab.CampaignDataPackage> list = model.updateFilters(map);
        CampaignTab tab = (CampaignTab) tabPane.getTabs().get(1);
        tab.updateData(list);
    }
}
