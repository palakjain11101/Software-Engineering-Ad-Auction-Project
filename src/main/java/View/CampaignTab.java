package View;

import Controller.MainController;
import Model.GraphPoint;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class CampaignTab extends Tab {

    private MainController controller;

    private String campaignID;

    private ArrayList<CampaignDataPackage> basicMetrics;

    private boolean shouldShowCampaign = true;

    private volatile boolean isLoadingSelection = false;

    private VBox pane;
    private TableView table;
    private boolean listenerEnabled = true;

    public CampaignTab(MainController controller, ArrayList<CampaignDataPackage> basicMetrics, String campaignID){
        this.controller = controller;
        this.basicMetrics = basicMetrics;
        this.campaignID = campaignID;
        initCampaignTab();
    }

    private void initCampaignTab() {
        setText(campaignID);
        pane = new VBox();
        pane.getStylesheets().add("styles.css");
        setContent(pane);

        table = new TableView();
        TableColumn mainColumn = new TableColumn("Click to View Graph");
        TableColumn metric = new TableColumn("Metric");
        TableColumn value = new TableColumn("Value");

        metric.setCellValueFactory(new PropertyValueFactory<>("ID"));
        value.setCellValueFactory(new PropertyValueFactory<>("OverallMetric"));

        metric.setSortable(false);
        value.setSortable(false);
        metric.setReorderable(false);
        mainColumn.setReorderable(false);
        value.setReorderable(false);
        mainColumn.setReorderable(false);
        mainColumn.getColumns().addAll(metric, value);
        table.getColumns().addAll(mainColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setEditable(false);


        addItems();
        setSelectionModel();

        pane.getChildren().add(table);

        CheckBox hideBox = new CheckBox();
        hideBox.paddingProperty().setValue(new Insets(0,10,10,0));
        Text text = new Text("Hide Series");
        hideBox.setSelected(false);
        hideBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            shouldShowCampaign = !shouldShowCampaign;
            controller.recreateGraph();
        });

        HBox container = new HBox(hideBox,text);
        container.paddingProperty().setValue(new Insets(10,10,10,10));
        pane.getChildren().add(container);

    }

    public void updateData(ArrayList<CampaignDataPackage> newBasicMetrics){
        int index = table.getSelectionModel().getSelectedIndex();
        this.basicMetrics = newBasicMetrics;
        listenerEnabled = false;
        addItems();
        table.getSelectionModel().select(index);
        listenerEnabled = true;

    }

    public void selectFirst(){
        table.getSelectionModel().select(0);
    }

    public void awaitCompletionOfSelection(){
        while(isLoadingSelection){

        }
    }

    private void addItems(){
        table.getItems().clear();
        for(CampaignDataPackage data : basicMetrics){
            table.getItems().add(data);
        }
    }

    private void setSelectionModel(){

        table.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            CampaignDataPackage v = (CampaignDataPackage) newValue;
            if(v==null || !listenerEnabled){
                return;
            }

            Task task = new Task<Void>() {
                @Override
                protected Void call() {
                    isLoadingSelection = true;
                    controller.updateGraphData(v.getID(),campaignID);
                    isLoadingSelection = false;
                    return null;
                }
            };

            task = controller.setBasicLoadingTaskMethods(task);

            new Thread(task).start();
        });
    }

    public String getSelected(){
        if(table.getSelectionModel().getSelectedItem() == null){
            return null;
        }
        return ((CampaignDataPackage) table.getSelectionModel().getSelectedItem()).getID();
    }

    public String getDatabaseID(){
        return campaignID;
    }

    public boolean getShouldShowCampaign(){
        return shouldShowCampaign;
    }

    public static class CampaignDataPackage{
        private final String a;
        private final Double b;

        public CampaignDataPackage(String metricType, Double overallMetric){
            this.a = metricType;
            this.b = overallMetric;
        }
        public String getID(){
            return a;
        }
        public Double getOverallMetric(){
            return b;
        }

    }
}
