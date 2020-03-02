package View;

import Controller.MainController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;

public class CampaignTab extends Tab {

    private MainController controller;

    private ArrayList<Tuple> basicMetrics = new ArrayList<>();

    private VBox pane;
    private TableView table;

    public CampaignTab(MainController controller, ArrayList<Tuple> basicMetrics){
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

        metric.setCellValueFactory(new PropertyValueFactory<>("A"));
        value.setCellValueFactory(new PropertyValueFactory<>("B"));

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
        for(Tuple tuple : basicMetrics){
            table.getItems().add(tuple);
        }

        table.getSelectionModel().selectFirst();
        table.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            System.out.println(newValue);
        });
    }

    public static class Tuple<A,B>{
        private final A a;
        private final B b;
        public Tuple(A a,B b){
            this.a = a;
            this.b = b;
        }
        public A getA(){
            return a;
        }
        public B getB(){
            return b;
        }
    }
}
