package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.stream.Collectors;

public class NewChartWindowDialogController {

    @FXML
    LineChart<Number,Number> lineChart;

    public void setChartAttributes(NumberAxis xAxis, NumberAxis yAxis, String title){
        NumberAxis thisXAxis = (NumberAxis) lineChart.getXAxis();
        NumberAxis thisYAxis = (NumberAxis) lineChart.getYAxis();

        thisXAxis.setLabel(xAxis.getLabel());
        thisYAxis.setLabel(yAxis.getLabel());
        thisXAxis.setLowerBound(1);
        thisXAxis.setUpperBound(xAxis.getUpperBound());
        thisXAxis.setTickUnit(1);

       //lineChart.setTitle(title);
    }

    public void addSeries(XYChart.Series<Number, Number> series){
        try {
            lineChart.getData().add(series);
            series.getNode().lookup(".chart-series-line").setStyle("");
        }
        catch (Exception ignored){ }
    }



}
