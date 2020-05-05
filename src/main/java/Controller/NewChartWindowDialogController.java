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

    public void setChartAttributes(NumberAxis xAxis, NumberAxis yAxis, XYChart.Series<Number, Number> series){
        NumberAxis thisXAxis = (NumberAxis) lineChart.getXAxis();
        NumberAxis thisYAxis = (NumberAxis) lineChart.getYAxis();

        thisXAxis.setLabel(xAxis.getLabel());
        thisYAxis.setLabel(yAxis.getLabel());
        thisXAxis.setLowerBound(1);
        thisXAxis.setUpperBound(xAxis.getUpperBound());
        thisXAxis.setTickUnit(1);

       lineChart.getData().addAll(series);
    }


}
