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
    LineChart<Number, Number> lineChart;

    public void setView(MainView view){
        this.view = view;
    }

    public void setModel(MainModel model){
        this.model = model;
    }

    @FXML public void btn(ActionEvent event){
        lineChart.getData().clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>(1, 200));
        series.getData().add(new XYChart.Data<>(2, 100));
        series.getData().add(new XYChart.Data<>(3, 300));
        series.getData().add(new XYChart.Data<>(4, 230));
        series.getData().add(new XYChart.Data<>(5, 230));
        series.getData().add(new XYChart.Data<>(6, 230));
        series.getData().add(new XYChart.Data<>(7, 230));
        series.getData().add(new XYChart.Data<>(8, 230));
        series.getData().add(new XYChart.Data<>(9, 230));
        series.getData().add(new XYChart.Data<>(10, 230));
        series.getData().add(new XYChart.Data<>(11, 230));
        series.getData().add(new XYChart.Data<>(12, 230));
        series.setName("Month Pay");
        lineChart.getData().add(series);
    }

    @FXML public void loadClickLogPressed(){

    }

    @FXML public void loadImpressionLogPressed(){

    }

    @FXML public void loadServerLogPressed(){

    }
}
