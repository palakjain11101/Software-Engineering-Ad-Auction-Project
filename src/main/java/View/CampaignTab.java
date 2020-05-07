package View;

import Controller.MainController;
import Model.GraphPoint;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class CampaignTab extends Tab {

    private MainController controller;

    private String campaignID;

    private ArrayList<CampaignDataPackage> basicMetrics;

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
        setText("Campaign 1");
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
    }

    public void updateData(ArrayList<CampaignDataPackage> newBasicMetrics){
        int index = table.getSelectionModel().getSelectedIndex();
        this.basicMetrics = newBasicMetrics;
        listenerEnabled = false;
        addItems();
        table.getSelectionModel().select(index);
        listenerEnabled = true;

    }

    private void setItems(){
        int i = 0;
        for(CampaignDataPackage data : basicMetrics){
            table.getItems().set(i,data);
            i++;
        }

        //table.getSelectionModel().selectFirst();

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
                    controller.updateGraphData(v.getID(),campaignID);
                    return null;
                }
            };

            task = controller.setBasicLoadingTaskMethods(task);

            new Thread(task).start();
            //controller.updateGraphData(v.getID(),"test");
        });
    }

    public String getSelected(){
        return ((CampaignDataPackage) table.getSelectionModel().getSelectedItem()).getID();
    }

    public String getDatabaseID(){
        return campaignID;
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
