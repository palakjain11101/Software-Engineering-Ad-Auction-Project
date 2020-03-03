package Controller;

import Model.MainModel;
import View.CampaignTab;
import View.MainView;
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

public class MainController {
    public static final int SLIDER_DAY = 0;
    public static final int SLIDER_WEEK = 1;
    public static final int SLIDER_MONTH = 2;
    public static final int SLIDER_YEAR = 3;


    private MainView view;
    private MainModel model;

    private ArrayList<Point> graphData = new ArrayList<>();
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
        for(Point point : graphData){
            series.getData().add(new XYChart.Data<>(Math.round(point.x/divider), point.y));
        }
        series.setName("Month Pay");
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
        String error;
        CampaignTab tab;
        ArrayList<CampaignTab.CampaignDataPackage> basicMetrics = new ArrayList<>();


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
            error = model.createNewCampaign(clickLogCSV,impressionLogCSV,serverLogCSV);
            if(error == null){
                double impressions = model.getData("SELECT COUNT(*) FROM impressions;");
                double clicks = model.getData("SELECT COUNT(*) FROM click;");
                double uniques = model.getData("SELECT COUNT(DISTINCT id) FROM click;");
                double bounces = model.getData("SELECT COUNT(case when conversion = 'No' then 1 else null end) FROM server");
                double conversions = model.getData("SELECT COUNT(case when conversion = 'Yes' then 1 else null end) FROM server");
                double totalCostClick = model.getData("SELECT SUM(cost) FROM click");
                double totalCostImpressions = model.getData("SELECT SUM(cost) FROM impressions");
                double totalCost = totalCostClick + totalCostImpressions;

                ArrayList<Point> impressionsOverTime = model.getDataOverTimePoints("SELECT DATE(date), count(*) from impressions group by DATE(date);");
                ArrayList<Point> clicksOverTime = model.getDataOverTimePoints("SELECT DATE(date), count(*) from click group by DATE(date);");
                ArrayList<Point> uniquesOverTime = model.getDataOverTimePoints("SELECT DATE(date), count(distinct id) from click group by DATE(date);");
                ArrayList<Point> bouncesOverTime = model.getDataOverTimePoints("SELECT DATE(entryDate), count(*) from server where strftime('%s',exitDate) - strftime('%s',entryDate) < 30 group by DATE (entryDate);");
                ArrayList<Point> conversionsOverTime = model.getDataOverTimePoints("SELECT DATE(entryDate), count(*) from server where conversion = 'Yes' group by DATE(entryDate);");
                ArrayList<Point> totalCostOverTime = model.getDataOverTimePoints("SELECT DATE(date), sum(cost) from click group by DATE(date);");
                ArrayList<Point> CTROverTime = model.getDataOverTimePoints("");
                ArrayList<Point> CPAOverTime = model.getDataOverTimePoints("");
                ArrayList<Point> CPCOverTime = model.getDataOverTimePoints("");
                ArrayList<Point> CPMOverTime = model.getDataOverTimePoints("");
                ArrayList<Point> bounceRateOverTime = model.getDataOverTimePoints("");

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
                tabPane.getTabs().add(tab);
            }
            else {
                view.showErrorMessage(error);
            }
        }
    }

    public void metricSelectedOnCampaignTab(CampaignTab.CampaignDataPackage metricSelected, String database){
        graphData = metricSelected.getMetricOverTimePoints();
        lineChart.getYAxis().setLabel(metricSelected.getID());
        recreateGraph(timeGranulationValue);
    }
}
