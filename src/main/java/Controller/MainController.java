package Controller;

import Model.GraphPoint;
import Model.MainModel;
import View.CampaignTab;
import View.MainView;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;


public class MainController {
    public static final int SLIDER_DAY = 0;
    public static final int SLIDER_WEEK = 1;
    public static final int SLIDER_MONTH = 2;
    public static final int SLIDER_YEAR = 3;


    private MainView view;
    private MainModel model;

    private String chartType = "Standard";
    private NewChartWindowDialogController newChartWindowDialogController;

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
    Tab defaultTab;

    @FXML
    Slider timeGranulationSlider;

    @FXML
    Button filterButton;
    @FXML
    Button filterRemoveButton;

    @FXML
    ListView filterListView;

    @FXML
    Button defineBounceButton;

    @FXML
    Button displayHistogramButton;

    @FXML
    CheckBox customBounceCheckBox;

    @FXML
    ComboBox chartTypeComboBox;

    @FXML Button loadClickLogButton;
    @FXML Button loadImpressionLogButton;
    @FXML Button loadServerLogButton;
    @FXML Button addCampaignButton;

    public void initialize(){
        disableCampaignFunctionalityButtons();
        chartTypeComboBox.getItems().addAll("Standard","Per Hour of Day","Per Day of Week");
        chartTypeComboBox.getSelectionModel().select(0);
        tabPane.getSelectionModel().selectedItemProperty().addListener(
                (ov, t, t1) -> {
                    if(t1 == defaultTab){
                        disableCampaignFunctionalityButtons();
                    }
                    else {
                        enableCampaignFunctionalityButtons();
                    }
                });

        setFileButtonBorder(loadClickLogButton,Color.RED);
        setFileButtonBorder(loadImpressionLogButton,Color.RED);
        setFileButtonBorder(loadServerLogButton,Color.RED);
        addCampaignButton.setDisable(true);
    }

    private void disableCampaignFunctionalityButtons(){
        customBounceCheckBox.setDisable(true);
        chartTypeComboBox.setDisable(true);
        timeGranulationSlider.setDisable(true);
        displayHistogramButton.setDisable(true);
        filterButton.setDisable(true);
        filterRemoveButton.setDisable(true);
    }

    private void enableCampaignFunctionalityButtons(){
        customBounceCheckBox.setDisable(false);
        chartTypeComboBox.setDisable(false);
        timeGranulationSlider.setDisable(false);
        displayHistogramButton.setDisable(false);
        filterButton.setDisable(false);
        filterRemoveButton.setDisable(false);
    }

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

        XYChart.Series<Number, Number> series = createSeries(timeGranularityValue);
        lineChart.getData().add(series);
        addToolTips(series);
        xAxis.setTickLabelFormatter(null);
        if(model.getGraphType().equals("Per Hour of Day")){
            xAxis.setUpperBound(24);
            xAxis.setTickUnit(1);
            xAxis.setLabel("Hour of Day");
            lineChart.autosize();
            return;
        }
        if(model.getGraphType().equals("Per Day of Week")){
            xAxis.setUpperBound(7);
            xAxis.setTickUnit(1);
            xAxis.setLabel("Day of Week");
            lineChart.autosize();
            xAxis.setTickLabelFormatter(new StringConverter<Number>() {
                @Override
                public String toString(Number number) {
                    String[] days = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
                    return days[number.intValue()-1];
                }

                @Override
                public Number fromString(String s) {
                    return null;
                }
            });
            return;
        }
        switch (timeGranularityValue){
            case SLIDER_DAY:
                xAxis.setUpperBound(graphData.size());
                xAxis.setTickUnit(1);
                xAxis.setLabel("Day of Campaign");
                break;
            case SLIDER_WEEK:
                xAxis.setUpperBound(Math.round(graphData.size()/7.0));
                xAxis.setTickUnit(1);
                xAxis.setLabel("Week of Campaign");
                break;
            case SLIDER_MONTH:
                xAxis.setUpperBound(Math.round(graphData.size()/30.0));
                xAxis.setTickUnit(1);
                xAxis.setLabel("Month of Campaign");
                break;
            case SLIDER_YEAR:
                xAxis.setUpperBound(Math.round(graphData.size()/365.0));
                xAxis.setTickUnit(1);
                xAxis.setLabel("Year of Campaign");
        }
        lineChart.autosize();
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
        if(!model.getGraphType().equals("Standard")){
            divider = 1;
        }

        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        int previousX = 0;
        int nextX;
        double holdTotal;
        double total = 0;
        double totalDenom = 0;
        XYChart.Data<Number,Number> graphElement;

        for(GraphPoint point : graphData){
            nextX = (int) Math.floor(point.getX()/divider);
            if(previousX < nextX){
                holdTotal = shouldGraphAvg ? (totalDenom == 0 ? 0 : total/totalDenom) : (total);

                graphElement = new XYChart.Data<>(previousX+1, holdTotal);
                series.getData().add(graphElement);

                total = point.getYnum();
                totalDenom = point.getYdenom();
                previousX = nextX;
            }
            else {
                total += point.getYnum();
                totalDenom += point.getYdenom();
            }
        }
        holdTotal = shouldGraphAvg ? (totalDenom == 0 ? 0 : total/totalDenom) : (total);
        series.getData().add(new XYChart.Data<>(previousX+1, holdTotal));
        series.setName(lineChart.getTitle());
        return series;
    }

    private void addToolTips(XYChart.Series<Number, Number> series){
        Tooltip tooltip;
        for (XYChart.Data<Number, Number> entry : series.getData()) {
            tooltip = new Tooltip(entry.getYValue().toString());
            tooltip.setShowDelay(Duration.millis(50));
            Tooltip.install(entry.getNode(), tooltip);
        }
    }

    @FXML public void loadClickLogPressed(){
        clickLogCSV = openFileChooser();
        setFileButtonBorder(loadClickLogButton, clickLogCSV == null ? Color.RED : Color.GREEN);
        shouldEnableLoadCampaignButton();
    }

    @FXML public void loadImpressionLogPressed(){
        impressionLogCSV = openFileChooser();
        setFileButtonBorder(loadImpressionLogButton, impressionLogCSV == null ? Color.RED : Color.GREEN);
        shouldEnableLoadCampaignButton();
    }

    @FXML public void loadServerLogPressed(){
        serverLogCSV = openFileChooser();
        setFileButtonBorder(loadServerLogButton, serverLogCSV == null ? Color.RED : Color.GREEN);
        shouldEnableLoadCampaignButton();
    }

    private File openFileChooser(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
        return fileChooser.showOpenDialog(view.getWindow());
    }

    private void shouldEnableLoadCampaignButton(){
        if(clickLogCSV != null && impressionLogCSV != null && serverLogCSV != null){
            addCampaignButton.setDisable(false);
        }
        else {
            addCampaignButton.setDisable(true);
        }
    }

    private void setFileButtonBorder(Button button, Color color){
        button.setBorder(new Border(new BorderStroke(color,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
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
                    error[0] = model.createNewCampaign(clickLogCSV,impressionLogCSV,serverLogCSV,"test");
                    if(error[0] == null){
                        return model.queryOverallMetrics("test");
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

    public void metricSelectedOnCampaignTab(String metricSelected, String database){
        if(metricSelected == null){return;}

        shouldGraphAvg = !metricSelected.equals("Number of Impressions") && !metricSelected.equals("Number of Clicks") && !metricSelected.equals("Number of Uniques") && !metricSelected.equals("Number of Bounces") && !metricSelected.equals("Number of Conversions") && !metricSelected.equals("Total Cost");

        lineChart.getYAxis().setLabel(metricSelected);
        lineChart.setTitle(metricSelected + " Over Time");

        recreateGraph(timeGranulationValue);
    }

    public void updateGraphData(String metricSelected, String campaignId){
        if(metricSelected == null){return;}
        graphData = model.queryCampaign(metricSelected, campaignId);
    }

    @FXML public void addFilterButtonPressed() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/addFilterDialog.fxml"));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent, 300, 300);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setScene(scene);
        AddFilterDialogController dialogController = fxmlLoader.getController();
        dialogController.init(model.getFilters());
        stage.showAndWait();
        if(!dialogController.isConfirmPressed()){
            return;
        }
        HashMap<String, List<String>> map = dialogController.getFilters();
        fillFilterListView(map);
        testUpdateCampaign(map);
    }

    @FXML public void removeFilterButtonPressed(){
        filterListView.getItems().clear();
        testUpdateCampaign(new HashMap<>());
    }

    private void fillFilterListView(HashMap<String, List<String>> map){
        filterListView.getItems().clear();
        for(String metric : map.keySet()){
            for(String filter : map.get(metric)) {
                filterListView.getItems().add(metric + " : " + filter);
            }
        }
    }

    @FXML
    public void onDisplayHistogramPressed() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/histogram.fxml"));
        fxmlLoader.setController(new HistogramController(model));
//            HistogramController histogramController = (HistogramController) fxmlLoader.getController();
        try {

            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent, 550, 450);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @FXML
    public void openNewWindowForChartSelected(){

        if(newChartWindowDialogController == null) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/newChartWindowDialog.fxml"));

            try {
                Parent parent = fxmlLoader.load();
                Scene scene = new Scene(parent, 900, 400);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setOnCloseRequest(windowEvent -> newChartWindowDialogController = null);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }


            newChartWindowDialogController = fxmlLoader.getController();
            NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
            NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
            newChartWindowDialogController.setChartAttributes(xAxis, yAxis, lineChart.getTitle());

        }
        XYChart.Series<Number, Number> series = copySeries(lineChart.getData().get(0));
        newChartWindowDialogController.addSeries(series);
    }

    //Taken from https://stackoverflow.com/questions/53807176/javafx-clone-xychart-series-doesnt-dork
    public static XYChart.Series<Number, Number> copySeries(XYChart.Series<Number, Number> series) {
        XYChart.Series<Number, Number> copy = new XYChart.Series<>(series.getName(),
                FXCollections.observableArrayList(series.getData()));
        return copy;
    }

    @FXML public void saveOrPrintSelected(){
        WritableImage image = lineChart.snapshot(new SnapshotParameters(), null);
        BufferedImage awtImage = SwingFXUtils.fromFXImage(image, null);

        PrinterJob printJob = PrinterJob.getPrinterJob();

        printJob.setPrintable((graphics, pageFormat, pageIndex) -> {
            int x = (int) Math.ceil(pageFormat.getImageableX());
            int y = (int) Math.ceil(pageFormat.getImageableY());
            if (pageIndex != 0) {
                return Printable.NO_SUCH_PAGE;
            }

            double scaler = Math.ceil(pageFormat.getImageableWidth())/awtImage.getWidth();

            graphics.drawImage(awtImage, x, y, (int) Math.ceil(pageFormat.getImageableWidth()), (int) Math.ceil(awtImage.getHeight()*scaler), null);
            return Printable.PAGE_EXISTS;
        });

        if (printJob.printDialog()) {
            try {
                printJob.print();
            } catch (PrinterException prt) {
                prt.printStackTrace();
            }
        }
    }

    @FXML
    public void customBounceCheckBoxSelected(){
        if(customBounceCheckBox.isSelected()){
            defineBounceButton.setDisable(false);
        }
        else {
            defineBounceButton.setDisable(true);
            model.setBounceAttributes(30,10);
            testUpdateCampaign(new HashMap<>());
        }
    }

    @FXML
    public void defineCustomBounceClicked() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/setBounceDefinition.fxml"));
        Parent parent = fxmlLoader.load();
        SetBounceDefinitionDialogController controller = fxmlLoader.getController();
        Scene scene = new Scene(parent, 300, 150);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.showAndWait();

        if(controller.getIsConfirmPressed()) {
            model.setBounceAttributes(controller.getSecondsAfterEntry(), controller.getMaxPagesVisited());
            testUpdateCampaign(model.getFilters());
        }
    }

    @FXML
    public void onChartTypeComboBoxChanges(){
        String selected = (String) chartTypeComboBox.getSelectionModel().getSelectedItem();
        model.setChartType(selected);

        timeGranulationSlider.setDisable(!selected.equals("Standard"));

        CampaignTab tab = (CampaignTab) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex());

        Task task = new Task<Void>() {
            @Override
            protected Void call() {
                updateGraphData(tab.getSelected(),tab.getDatabaseID());
                return null;
            }
        };



        task = setBasicLoadingTaskMethods(task,tab);

        new Thread(task).start();
    }

    public void testUpdateCampaign(HashMap<String,List<String>> map){
        CampaignTab tab = (CampaignTab) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex());
        Task task = new Task<Void>() {
            @Override
            protected Void call() {
                model.setFilters(map);
                ArrayList<CampaignTab.CampaignDataPackage> list = model.queryOverallMetrics(tab.getDatabaseID());
                tab.updateData(list);
                updateGraphData(tab.getSelected(),tab.getDatabaseID());
                return null;
            }
        };

        task = setBasicLoadingTaskMethods(task,tab);

        new Thread(task).start();
    }

    public Task<Void> setBasicLoadingTaskMethods(Task<Void> task, CampaignTab tab){
        task.setOnRunning((e) -> {
            view.showLoadingDialog();
        });

        task.setOnSucceeded((e) -> {
            view.hideLoadingDialog();
            metricSelectedOnCampaignTab(tab.getSelected(),tab.getDatabaseID());
        });
        return task;
    }

    @FXML
    public void useCurrentDatabase(){
        model.openCurrentDatabase();
        CampaignTab tab = new CampaignTab(this,model.queryOverallMetrics("test"));
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }


}
