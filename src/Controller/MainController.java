package Controller;

import Model.GraphPoint;
import Model.MainModel;
import View.CampaignTab;
import View.MainView;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
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
                xAxis.setLabel("Days Passed");
                return;
            case SLIDER_WEEK:
                xAxis.setUpperBound(Math.round(graphData.size()/7.0));
                xAxis.setTickUnit(1);
                xAxis.setLabel("Weeks Passed");
                return;
            case SLIDER_MONTH:
                xAxis.setUpperBound(Math.round(graphData.size()/30.0));
                xAxis.setTickUnit(1);
                xAxis.setLabel("Months Passed");
                return;
            case SLIDER_YEAR:
                xAxis.setUpperBound(Math.round(graphData.size()/365.0));
                xAxis.setTickUnit(1);
                xAxis.setLabel("Years Passed");
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
        if(clickLogCSV == null){
            view.showErrorMessage("Click Log file needed");
            return;
        }
        else if(impressionLogCSV == null){
            view.showErrorMessage("Impression Log file needed");
            return;
        }
        else if(serverLogCSV == null){
            view.showErrorMessage("Server Log file needed");
            return;
        }
        else{
            Task task = new Task<CampaignTab>() {
                @Override
                protected CampaignTab call() {
                    return loadCampaign();
                }
            };

            task.setOnRunning((e) -> view.showLoadingDialog());
            task.setOnSucceeded((e) -> {
                view.hideLoadingDialog();
                try {
                    if(task.get() != null) {
                        CampaignTab tab = ((Task<CampaignTab>) task).getValue();
                        tabPane.getTabs().add(tab);
                        tabPane.getSelectionModel().select(tab);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });

            new Thread(task).start();
        }
    }

    private CampaignTab loadCampaign(){
        ArrayList<CampaignTab.CampaignDataPackage> basicMetrics = new ArrayList<>();
        CampaignTab tab;
        String error;

        error = model.createNewCampaign(clickLogCSV,impressionLogCSV,serverLogCSV);
        if(error == null){
            double impressions = model.getData("SELECT COUNT(*) FROM impressions;");
            double clicks = model.getData("SELECT COUNT(*) FROM click;");
            double uniques = model.getData("SELECT COUNT(DISTINCT id) FROM click;");
            double bounces = model.getData("SELECT COUNT(case when strftime('%s',exitDate) - strftime('%s',entryDate) < 30 then 1 else null end) FROM server");
            double conversions = model.getData("SELECT COUNT(case when conversion = 'Yes' then 1 else null end) FROM server");
            double totalCostClick = model.getData("SELECT SUM(cost) FROM click");
            double totalCostImpressions = model.getData("SELECT SUM(cost) FROM impressions");
            double totalCost = totalCostClick + totalCostImpressions;

            ArrayList<GraphPoint> impressionsOverTime = model.getDataOverTimePoints("SELECT DATE(date), count(*) from impressions group by DATE(date);");
            ArrayList<GraphPoint> clicksOverTime = model.getDataOverTimePoints("SELECT DATE(date), count(*) from click group by DATE(date);");
            ArrayList<GraphPoint> uniquesOverTime = model.getDataOverTimePoints("SELECT DATE(date), count(distinct id) from click group by DATE(date);");
            ArrayList<GraphPoint> bouncesOverTime = model.getDataOverTimePoints("SELECT DATE(entryDate), count(*) from server where strftime('%s',exitDate) - strftime('%s',entryDate) < 30 group by DATE (entryDate);");
            ArrayList<GraphPoint> conversionsOverTime = model.getDataOverTimePoints("SELECT DATE(entryDate), count(*) from server where conversion = 'Yes' group by DATE(entryDate);");
            ArrayList<GraphPoint> totalCostOverTime = model.getDataOverTimePoints("SELECT d1, c+i from (SELECT DATE(date) as d1, SUM(cost) as c from click group by DATE(date)) LEFT OUTER JOIN (SELECT DATE(date) as d2, SUM(cost) as i from impressions group by DATE(date)) ON d1=d2 group by DATE(d1);");
            ArrayList<GraphPoint> CTROverTime = model.getDataOverTimePoints("SELECT d1, CAST(c as float)/CAST(i as float) from (SELECT date(date) as d1, count(*) as c from click group by DATE(date)) LEFT OUTER JOIN (SELECT date(date) as d2, count(*) as i from impressions group by DATE(date)) ON d1=d2 group by d1;");
            ArrayList<GraphPoint> CPAOverTime = model.getDataOverTimePoints("SELECT d1, CAST(c2 as float)/CAST(i as float) from (SELECT d1, c+i as c2 from (SELECT DATE(date) as d1, SUM(cost) as c from click group by DATE(date)) LEFT OUTER JOIN (SELECT DATE(date) as d2, SUM(cost) as i from impressions group by DATE(date)) ON d1=d2 group by DATE(d1)) LEFT OUTER JOIN (SELECT date(entryDate) as d2, count(*) as i from server where conversion='Yes' group by DATE(entryDate)) ON d1=d2 group by d1;");
            ArrayList<GraphPoint> CPCOverTime = model.getDataOverTimePoints("SELECT DATE(date), avg(cost) from click group by DATE(date);");
            ArrayList<GraphPoint> CPMOverTime = model.getDataOverTimePoints("SELECT DATE(date), avg(cost)*1000 from impressions group by DATE(date);");
            ArrayList<GraphPoint> bounceRateOverTime = model.getDataOverTimePoints("SELECT d1, CAST(i as float)/CAST(c as float) from (SELECT DATE(date) as d1, count(*) as c from click group by DATE(date)) LEFT OUTER JOIN (SELECT DATE(entryDate) as d2, count(*) as i from server where strftime('%s',exitDate) - strftime('%s',entryDate) < 30 group by DATE (entryDate)) ON d1=d2 GROUP BY DATE(d1);");

            basicMetrics.add(new CampaignTab.CampaignDataPackage("Number of Impressions", impressions, impressionsOverTime));
            basicMetrics.add(new CampaignTab.CampaignDataPackage("Number of Clicks", clicks, clicksOverTime));
            basicMetrics.add(new CampaignTab.CampaignDataPackage("Number of Uniques", uniques, uniquesOverTime));
            basicMetrics.add(new CampaignTab.CampaignDataPackage("Number of Bounces", bounces, bouncesOverTime));
            basicMetrics.add(new CampaignTab.CampaignDataPackage("Number of Conversions", conversions, conversionsOverTime));
            basicMetrics.add(new CampaignTab.CampaignDataPackage("Total Cost", totalCost, totalCostOverTime)); //May need to be changed, not sure whether it should be per impression or click
            basicMetrics.add(new CampaignTab.CampaignDataPackage("CTR", clicks/impressions, CTROverTime));
            basicMetrics.add(new CampaignTab.CampaignDataPackage("CPA", totalCost/conversions, CPAOverTime)); //I don't understand this one will talk about it tomorrow
            basicMetrics.add(new CampaignTab.CampaignDataPackage("CPC",  totalCostClick/clicks, CPCOverTime));
            basicMetrics.add(new CampaignTab.CampaignDataPackage("CPM", (totalCostImpressions/impressions)*1000, CPMOverTime));
            basicMetrics.add(new CampaignTab.CampaignDataPackage("Bounce Rate",bounces/clicks, bounceRateOverTime));

            tab = new CampaignTab(this,basicMetrics);
            //tabPane.getTabs().add(tab);
            return tab;
        }
        else {
            view.showErrorMessage(error);
        }
        return null;
    }

    public void metricSelectedOnCampaignTab(CampaignTab.CampaignDataPackage metricSelected, String database){
        graphData = metricSelected.getMetricOverTimePoints();

        String id = metricSelected.getID();
        shouldGraphAvg = !id.equals("Number of Impressions") && !id.equals("Number of Clicks") && !id.equals("Number of Uniques") && !id.equals("Number of Bounces") && !id.equals("Number of Conversions") && !id.equals("Number of Cost");

        lineChart.getYAxis().setLabel(id);
        lineChart.setTitle(id + " Over Time");

        recreateGraph(timeGranulationValue);
    }
}
