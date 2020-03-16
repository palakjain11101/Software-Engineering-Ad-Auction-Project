package Controller;

import Model.BounceDefinition;
import Model.GraphPoint;
import Model.MainModel;
import View.CampaignTab;
import View.MainView;
import com.sun.javafx.charts.Legend;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;


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

    @FXML
    Button defineBounceButton;
    @FXML
    CheckBox customBounceCheckBox;



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
        double holdTotal;
        double total = 0;
        double totalDenom = 0;

        for(GraphPoint point : graphData){
            nextX = (int) Math.floor(point.getX()/divider);
            if(previousX < nextX){
                holdTotal = shouldGraphAvg ? (totalDenom == 0 ? 0 : total/totalDenom) : (total);
                series.getData().add(new XYChart.Data<>(previousX+1, holdTotal));
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
                        return model.queryCampaign(new HashMap<>());
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
        shouldGraphAvg = !id.equals("Number of Impressions") && !id.equals("Number of Clicks") && !id.equals("Number of Uniques") && !id.equals("Number of Bounces") && !id.equals("Number of Conversions") && !id.equals("Total Cost");

        lineChart.getYAxis().setLabel(id);
        lineChart.setTitle(id + " Over Time");

        recreateGraph(timeGranulationValue);
    }

    @FXML public void addFilterButtonPressed() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/addFilterDialog.fxml"));
        //fxmlLoader.setLocation(AddFilterDialogController.class.getResource("target/classes/addFilterDialog.fxml"));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent, 300, 200);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        AddFilterDialogController dialogController = fxmlLoader.getController();
        dialogController.setUpDialogController();
        stage.showAndWait();
        AddFilterDialogController controller = fxmlLoader.getController();

        Map<String, List<String>>  map = controller.CheckBoxes();

        Set<String> keys = map.keySet();

        for(Object key: keys){
            List<String> keyList = map.get(key);
            for(Object o: keyList){
                filterListView.getItems().addAll(""+key + ":" + o);
            }
        }




        //get the data
        //change the list view
        //pass data to the Model

        //Register the filter for another event type
    }

    @FXML public void removeFilterButtonPressed(){
        filterListView.getItems().remove(filterListView.getSelectionModel().getSelectedItem());
    }

    @FXML public void onDisplayHistogramPressed(){
        System.out.println("Put histogram code here");
    }

    @FXML public void saveOrPrintSelected(){
        WritableImage image = lineChart.snapshot(new SnapshotParameters(), null);
        BufferedImage awtImage = SwingFXUtils.fromFXImage(image, null);
//        PDDocument doc = new PDDocument();
//        doc.addPage(new PDPage());
//        PDImageXObject pdImageXObject = null;
//        try {
//            pdImageXObject = LosslessFactory.createFromImage(doc, awtImage);
//            PDPageContentStream contentStream = new PDPageContentStream(doc, doc.getPage(0), PDPageContentStream.AppendMode.APPEND, true, true);
//            contentStream.drawImage(pdImageXObject, 100, 160, awtImage.getWidth() / 2, awtImage.getHeight() / 2);
//            contentStream.close();
//            doc.save("test.pdf");
//            doc.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Printer printer = Printer.getDefaultPrinter();
        PrinterJob job = PrinterJob.createPrinterJob(printer);
        if (job.showPrintDialog(this.view.getWindow())) {
            //PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, 0, 0, 0, 0);
            PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.HARDWARE_MINIMUM);
//            double scaleX = pageLayout.getPrintableWidth() / lineChart.getBoundsInParent().getWidth();
//            double scaleY = pageLayout.getPrintableHeight() / lineChart.getBoundsInParent().getHeight();
//            lineChart.getTransforms().add(new Scale(scaleX,scaleY));
            if(job.printPage(pageLayout,lineChart)){
                job.endJob();
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

        BounceDefinition bounceDefinition = new BounceDefinition(controller.getSecondsAfterEntry(),controller.getNeedToConvert());
        System.out.println(controller.getSecondsAfterEntry());
        System.out.println(controller.getNeedToConvert());
    }

    //TEST BUTTON ONLY
    public void onTestButtonPressed(){
        HashMap map = new HashMap();
        map.put("gender","Male");
        ArrayList<CampaignTab.CampaignDataPackage> list = model.queryCampaign(map);
        CampaignTab tab = (CampaignTab) tabPane.getTabs().get(1);
        tab.updateData(list);
    }


}
