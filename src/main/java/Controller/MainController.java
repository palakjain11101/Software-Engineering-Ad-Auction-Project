package Controller;

import Model.GraphPoint;
import Model.MainModel;
import View.CampaignTab;
import View.MainView;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
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
import java.util.stream.Collectors;


public class MainController {
    public static final int SLIDER_DAY = 0;
    public static final int SLIDER_WEEK = 1;
    public static final int SLIDER_MONTH = 2;
    public static final int SLIDER_YEAR = 3;


    private MainView view;
    private MainModel model;

    private HashMap<String,XYChart.Series<Number,Number>> allSeries;

    private HashMap<String, ArrayList<GraphPoint>> graphPoints = new HashMap<>();
    //private boolean shouldGraphAvg = true; //Otherwise just sum
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
    Spinner<Double> outlierStrictnessSpinner;

    @FXML
    CheckBox customBounceCheckBox;

    @FXML
    ComboBox chartTypeComboBox;

    @FXML Button loadClickLogButton;
    @FXML Button loadImpressionLogButton;
    @FXML Button loadServerLogButton;
    @FXML TextField campaignIDInput;
    @FXML Button addCampaignButton;

    //Initialises some listeners, selections and node property's
    public void initialize(){
        disableCampaignFunctionalityButtons();
        chartTypeComboBox.getItems().addAll("Standard","Per Hour of Day","Per Day of Week");
        chartTypeComboBox.getSelectionModel().select(0);
        tabPane.getSelectionModel().selectedItemProperty().addListener(
                (ov, t, t1) -> {
                    if(t1 == defaultTab){
                        disableCampaignFunctionalityButtons();
                        filterListView.getItems().clear();
                    }
                    else {
                        enableCampaignFunctionalityButtons();
                        fillFilterListView(model.getFilters(getCurrentTab().getDatabaseID()));
                    }
                });

        setFileButtonBorder(loadClickLogButton,Color.RED);
        setFileButtonBorder(loadImpressionLogButton,Color.RED);
        setFileButtonBorder(loadServerLogButton,Color.RED);
        addCampaignButton.setDisable(true);

        campaignIDInput.textProperty().addListener((observable, oldValue, newValue) -> shouldEnableLoadCampaignButton());

        outlierStrictnessSpinner.setValueFactory(new SpinnerValueFactory<Double>() {
            @Override
            public void decrement(int i) {
                if(getValue() > 0.5){
                    setValue(getValue()-0.5);
                }
            }

            @Override
            public void increment(int i) {
                if(getValue() < 10){
                    setValue(getValue()+0.5);
                }
            }
        });
        outlierStrictnessSpinner.getValueFactory().setValue(2.0);
        outlierStrictnessSpinner.getValueFactory().valueProperty().addListener((observableValue, aDouble, t1) -> {
            recreateGraph();
        });
    }

    /*
    Loads the data from any campaigns automatically detected in the same directory.
    These campaigns have already been detected in the model.
     */
    public HashMap<String, List<CampaignTab.CampaignDataPackage>> loadAllDataFromEarlierCampaigns(){
        HashMap<String, List<CampaignTab.CampaignDataPackage>> campaignData = new HashMap<>();
        for(String campaignID : model.getAllCampaigns()){
            campaignData.put(campaignID,model.queryOverallMetrics(campaignID));
        }
        return campaignData;
    }

    /*
    Creates a new campaign tab and sets the relevant properties
     */
    public CampaignTab createAndAddTab(String campaignID, ArrayList<CampaignTab.CampaignDataPackage> metrics){
        CampaignTab tab = new CampaignTab(this, metrics, campaignID);
        tab.setOnClosed(arg0 -> {
            String id = ((CampaignTab) arg0.getTarget()).getDatabaseID();
            model.deleteCampaign(id);
            graphPoints.remove(id);
            recreateGraph(timeGranulationValue);
        });
        tabPane.getTabs().add(tab);
        return tab;
    }

    /*
    Disables all buttons that should only work when a campaign is active
     */
    private void disableCampaignFunctionalityButtons(){
        customBounceCheckBox.setDisable(true);
        chartTypeComboBox.setDisable(true);
        timeGranulationSlider.setDisable(true);
        displayHistogramButton.setDisable(true);
        filterButton.setDisable(true);
        filterRemoveButton.setDisable(true);
    }

    /*
    Enables all buttons that should only work when a campaign is active
     */
    private void enableCampaignFunctionalityButtons(){
        customBounceCheckBox.setDisable(false);
        chartTypeComboBox.setDisable(false);
        timeGranulationSlider.setDisable(false);
        displayHistogramButton.setDisable(false);
        filterButton.setDisable(false);
        filterRemoveButton.setDisable(false);
    }

    /*
    Returns a list of all tabs, not including the default tab
     */
    private List<CampaignTab> getCampaignTabs(){
        List<CampaignTab> tabs = new ArrayList<>();
        for(Tab tab : tabPane.getTabs()){
            if(!(tab == defaultTab)){
                tabs.add((CampaignTab) tab);
            }
        }
        return tabs;
    }

    /*
    Sets the main view
     */
    public void setView(MainView view){
        this.view = view;
    }

    /*
    Sets the main model
     */
    public void setModel(MainModel model){
        this.model = model;
    }

    /*
    If no new time granularity value is selected, uses the current value
     */
    public void recreateGraph(){
        recreateGraph(timeGranulationValue);
    }

    /*
    Recreates the graph based on the current data and properties
     */
    public void recreateGraph(int timeGranularityValue){
        timeGranulationValue = timeGranularityValue;
        NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
        xAxis.setLowerBound(1);
        lineChart.getData().clear();

        boolean shouldGraphAvg;
        String metricSelected;

        allSeries = new HashMap<>();
        for(CampaignTab tab : getCampaignTabs()) {
            if(tab.getShouldShowCampaign()) {
                metricSelected = tab.getSelected();
                if(metricSelected==null) continue;
                shouldGraphAvg = !metricSelected.equals("Number of Impressions") && !metricSelected.equals("Number of Clicks") && !metricSelected.equals("Number of Uniques") && !metricSelected.equals("Number of Bounces") && !metricSelected.equals("Number of Conversions") && !metricSelected.equals("Total Cost");
                XYChart.Series<Number, Number> series = createSeries(timeGranularityValue, graphPoints.get(tab.getDatabaseID()), shouldGraphAvg, tab.getDatabaseID(), metricSelected);
                lineChart.getData().add(series);
                addToolTips(series);
                allSeries.put(tab.getDatabaseID(), series);
            }
        }

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
                xAxis.setUpperBound(getMostDaysInACampaign());
                xAxis.setTickUnit(1);
                xAxis.setLabel("Day of Campaign");
                break;
            case SLIDER_WEEK:
                xAxis.setUpperBound(Math.round(getMostDaysInACampaign()/7.0));
                xAxis.setTickUnit(1);
                xAxis.setLabel("Week of Campaign");
                break;
            case SLIDER_MONTH:
                xAxis.setUpperBound(Math.round(getMostDaysInACampaign()/30.0));
                xAxis.setTickUnit(1);
                xAxis.setLabel("Month of Campaign");
                break;
            case SLIDER_YEAR:
                xAxis.setUpperBound(Math.round(getMostDaysInACampaign()/365.0));
                xAxis.setTickUnit(1);
                xAxis.setLabel("Year of Campaign");
        }
        lineChart.autosize();
    }

    /*
    Gets the length in days of teh campaign with the greatest length
     */
    private int getMostDaysInACampaign(){
        int days = 0;
        for(ArrayList<GraphPoint> list : graphPoints.values()){
            days = list.size() > days ? list.size() : days;
        }
        return days;
    }

    /*
    Creates a new series for a campaign based on the time granularity value, graph type and point data
     */
    private XYChart.Series<Number, Number> createSeries(int graphType, ArrayList<GraphPoint> graphData, boolean shouldGraphAvg, String campaignID, String selected){
        double divider = 1;
        switch (graphType){
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

        ArrayList<GraphPoint> newPoints = new ArrayList<>();

        for(GraphPoint point : graphData){
            nextX = (int) Math.floor(point.getX()/divider);
            if(previousX < nextX){
                holdTotal = shouldGraphAvg ? (totalDenom == 0 ? 0 : total/totalDenom) : (total);

                newPoints.add(new GraphPoint(previousX+1,holdTotal));

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
        newPoints.add(new GraphPoint(previousX+1,holdTotal));

        newPoints = model.setOutliers(newPoints, outlierStrictnessSpinner.getValue());
        for(GraphPoint point : newPoints){
            graphElement = new XYChart.Data<>(point.getX(),point.getY());
            series.getData().add(graphElement);
            isOutlier(point,graphElement);
        }

        series.setName(campaignID + " : " + selected);
        return series;
    }

    /*
    Checks if a given point on a graph is an outlier and alters it accordingly
     */
    private void isOutlier(GraphPoint point,  XYChart.Data<Number,Number> graphElement){
        if(point.getOutlier()){
            graphElement.nodeProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    newValue.setStyle("-fx-background-color: RED");
                }
            });
        }
    }

    /*
    Adds tool tips to every point in a series
     */
    public void addToolTips(XYChart.Series<Number, Number> series){
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
        if(clickLogCSV != null && impressionLogCSV != null && serverLogCSV != null && !campaignIDInput.getText().equals("")){
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

    /*
    Called when load campaign pressed. Creates a new task to load the base data.
     */
    @FXML public void loadCampaignPressed(){
        final String[] error = new String[1];
        if(shouldLoadCampaign()) {

            String campaignID = campaignIDInput.getText();

            Task task = new Task<ArrayList<CampaignTab.CampaignDataPackage>>() {
                @Override
                protected ArrayList<CampaignTab.CampaignDataPackage> call() {
                    error[0] = model.createNewCampaign(clickLogCSV,impressionLogCSV,serverLogCSV,campaignID);
                    if(error[0] == null){
                        return model.queryOverallMetrics(campaignID);
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
                        CampaignTab tab = createAndAddTab(campaignID,((Task<ArrayList<CampaignTab.CampaignDataPackage>>) task).getValue());
                        tab.selectFirst();
                        clickLogCSV = null;
                        impressionLogCSV = null;
                        serverLogCSV = null;
                        campaignIDInput.setText("");
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

            setFileButtonBorder(loadClickLogButton,Color.RED);
            setFileButtonBorder(loadImpressionLogButton,Color.RED);
            setFileButtonBorder(loadServerLogButton,Color.RED);
        }
    }

    /*
    Checks various conditions to see if the campaign should be loaded or not.
     */
    private boolean shouldLoadCampaign(){
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
        else if(doesCampaignExist(campaignIDInput.getText())){
            view.showErrorMessage("That campaign ID is already in use");
        }
        else {
            return true;
        }
        return false;
    }

    /*
    Checks to see if a given campaign exists
     */
    private boolean doesCampaignExist(String campaignID){
        return graphPoints.keySet().contains(campaignID);
    }

    /*
    Updates the graph point data for a campaign.
     */
    public void updateGraphData(String metricSelected, String campaignId){
        if(metricSelected == null){return;}
        graphPoints.put(campaignId,model.queryCampaign(metricSelected, campaignId));
    }

    /*
    Returns the currently selected tab.
     */
    private CampaignTab getCurrentTab(){
        return (CampaignTab) tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex());
    }

    /*
    When add filter is selected, creates a new dialog.
     */
    @FXML public void addFilterButtonPressed() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/addFilterDialog.fxml"));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent, 300, 300);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setScene(scene);
        AddFilterDialogController dialogController = fxmlLoader.getController();
        dialogController.init(model.getFilters(getCurrentTab().getDatabaseID()));
        stage.showAndWait();
        if(!dialogController.isConfirmPressed()){
            return;
        }
        HashMap<String, List<String>> map = dialogController.getFilters();
        fillFilterListView(map);
        updateCampaign(map);
    }

    /*
    When clear filters pressed, clears all the filters.
     */
    @FXML public void removeFilterButtonPressed(){
        filterListView.getItems().clear();
        updateCampaign(new HashMap<>());
    }

    /*
    Fills the filters added box for a given campaigns filters
     */
    private void fillFilterListView(HashMap<String, List<String>> map){
        filterListView.getItems().clear();
        for(String metric : map.keySet()){
            for(String filter : map.get(metric)) {
                filterListView.getItems().add(metric + " : " + filter);
            }
        }
    }

    /*
    Displays a histogram of the currently selected campaigns click cost.
     */
    @FXML
    public void onDisplayHistogramPressed() {
        CampaignTab tab = getCurrentTab();

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/histogram.fxml"));
        fxmlLoader.setController(new HistogramController(model, this, tab.getDatabaseID()));
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

    /*
    Opens a new window which displays all the currently visible series
     */
    @FXML
    public void openNewWindowForChartSelected(){

        NewChartWindowDialogController newChartWindowDialogController;

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/newChartWindowDialog.fxml"));

        try {
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent, 900, 400);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }


        newChartWindowDialogController = fxmlLoader.getController();
        NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
        newChartWindowDialogController.setChartAttributes(xAxis, yAxis, lineChart.getTitle());

//        XYChart.Series<Number, Number> series = allSeries.get(getCurrentTab().getDatabaseID());
//        XYChart.Series<Number, Number> copySeries = copySeries(series);
//        newChartWindowDialogController.addSeries(copySeries);

        for(XYChart.Series<Number,Number> series : lineChart.getData()){
            XYChart.Series<Number, Number> copySeries = copySeries(series);
            newChartWindowDialogController.addSeries(copySeries);
        }

    }

    //Taken from https://stackoverflow.com/questions/53807176/javafx-clone-xychart-series-doesnt-dork
    private static XYChart.Series<Number, Number> copySeries(XYChart.Series<Number, Number> series) {
        XYChart.Series<Number, Number> copy = new XYChart.Series<>(series.getName(),
                series.getData().stream()
                        .map(data -> new XYChart.Data<>(data.getXValue(), data.getYValue()))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)));

        return copy;
    }

    /*
    Called when the save or print button is selected.
     */
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

    /*
    Called when the custom bounce check box is selected
     */
    @FXML
    public void customBounceCheckBoxSelected(){
        if(customBounceCheckBox.isSelected()){
            defineBounceButton.setDisable(false);
        }
        else {
            defineBounceButton.setDisable(true);
            model.setBounceAttributes(30,10);
            updateCampaign(new HashMap<>());
        }
    }

    /*
    Called when the define custom bounce rate button is clicked
     */
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
            updateCampaign(model.getFilters(getCurrentTab().getDatabaseID()));
        }
    }

    /*
    Called when the chart type combo box is changed
     */
    @FXML
    public void onChartTypeComboBoxChanges(){
        String selected = (String) chartTypeComboBox.getSelectionModel().getSelectedItem();
        model.setChartType(selected);

        timeGranulationSlider.setDisable(!selected.equals("Standard"));


        Task task = new Task<Void>() {
            @Override
            protected Void call() {
                CampaignTab campaignTab;
                for(Tab tab : tabPane.getTabs()) {
                    if(!(tab == defaultTab)) {
                        campaignTab = (CampaignTab) tab;
                        updateGraphData(campaignTab.getSelected(), campaignTab.getDatabaseID());
                    }
                }
                return null;
            }
        };



        task = setBasicLoadingTaskMethods(task);

        new Thread(task).start();
    }

    /*
    Updates the currently selected campaign given new filters
     */
    private void updateCampaign(HashMap<String,List<String>> map){
        CampaignTab tab = getCurrentTab();
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                model.setFilters(map,tab.getDatabaseID());
                ArrayList<CampaignTab.CampaignDataPackage> list = model.queryOverallMetrics(tab.getDatabaseID());
                tab.updateData(list);
                updateGraphData(tab.getSelected(),tab.getDatabaseID());
                return null;
            }
        };

        task = setBasicLoadingTaskMethods(task);

        new Thread(task).start();
    }

    /*
    Sets basic events for some loading tasks which share common features
     */
    public Task<Void> setBasicLoadingTaskMethods(Task<Void> task){
        task.setOnRunning((e) -> {
            view.showLoadingDialog();
        });

        task.setOnSucceeded((e) -> {
            view.hideLoadingDialog();
            recreateGraph(timeGranulationValue);
        });
        return task;
    }
}
