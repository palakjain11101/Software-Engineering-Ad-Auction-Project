package View;

import Controller.MainController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.util.ArrayList;

public class CampaignTab extends Tab {

    private MainController controller;

    private ArrayList<CampaignDataPackage> basicMetrics;

    private VBox pane;
    private TableView table;

    public CampaignTab(MainController controller, ArrayList<CampaignDataPackage> basicMetrics){
        this.controller = controller;
        this.basicMetrics = basicMetrics;


        initCampaignTab();
    }

    private void initCampaignTab() {
        setText("Campaign 1");
        pane = new VBox();
        pane.getStylesheets().add("View/styles.css");
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

        pane.getChildren().add(table);
    }

    private void addItems(){
        for(CampaignDataPackage data : basicMetrics){
            table.getItems().add(data);
        }

        table.getSelectionModel().selectFirst();
        table.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            controller.metricSelectedOnCampaignTab((CampaignDataPackage) newValue,"test");
        });
    }

    public static class CampaignDataPackage{
        private final String a;
        private final Double b;
        private final ArrayList<Point> c;
        public CampaignDataPackage(String a, Double b, ArrayList<Point> c){
            this.a = a;
            this.b = b;
            this.c = c;
        }
        public String getID(){
            return a;
        }
        public Double getOverallMetric(){
            return b;
        }
        public ArrayList<Point> getMetricOverTimePoints(){
            return c;
        }
    }
}
