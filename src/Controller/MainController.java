package Controller;

import Model.MainModel;
import View.MainView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class MainController {
    private MainView view;
    private MainModel model;
    @FXML
    LineChart<String, Number> linechart;

    public void setView(MainView view){
        this.view = view;
    }

    public void setModel(MainModel model){
        this.model = model;
    }

    @FXML public void btn(ActionEvent event){
        linechart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
        series.getData().add(new XYChart.Data<String, Number>("Jan", 200));
        series.getData().add(new XYChart.Data<String, Number>("Feb", 100));
        series.getData().add(new XYChart.Data<String, Number>("Mar", 300));
        series.getData().add(new XYChart.Data<String, Number>("Apr", 230));
        series.setName("Month Pay");
        linechart.getData().add(series);
    }
}
