package Controller;

import Model.MainModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

public class HistogramController implements Initializable {

    private MainModel model;

    private ArrayList<Double> clickCostList;
    private int numberOfClasses;
    private Double classWidth;

    HistogramController(MainModel model, String campaignId) {
        this.model = model;
        this.clickCostList = model.getAllClickCosts(campaignId);
        this.classWidth = 2.5;
        double classes = ((Collections.max(clickCostList))/classWidth);
        this.numberOfClasses = (int) Math.ceil(classes);


    }

    @FXML
    private BarChart<?,?> clickCostHistogram;
    @FXML
    private CategoryAxis x;
    @FXML
    private NumberAxis y;

    private XYChart.Series histogramSeries = new XYChart.Series();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ArrayList<Integer> clickCostFrequencies = new ArrayList<>(Collections.nCopies(numberOfClasses+1, 0));
        ArrayList<Double> clickCostFreqDens = new ArrayList<>();

        for ( Double clickCost : clickCostList) {
            int classID = (int) Math.ceil(clickCost/classWidth);
            clickCostFrequencies.set(classID, (clickCostFrequencies.get(classID))+1);

        }

        for ( int freq : clickCostFrequencies) {
            clickCostFreqDens.add(freq/classWidth);

        }

        boolean firstClass = true;
        int index = 0;
        for ( Double dens : clickCostFreqDens) {
            if (firstClass) {
                histogramSeries.getData().add(new XYChart.Data("0 - " + classWidth, clickCostFreqDens.get(0)));
                index++;
                firstClass = false;

            }

            else {
                histogramSeries.getData().add(new XYChart.Data(classWidth*index + " - " + classWidth*(index+1), clickCostFreqDens.get(index)));
                index++;

            }

        }

//        for (int i = 0; i < clickCostFreqDens.size(); i++) {
//            if (i == 0) {
//                histogramSeries.getData().add(new XYChart.Data("0 - " + classWidth, clickCostFreqDens.get(0)));
//
//            }
//
//            else {
//                histogramSeries.getData().add(new XYChart.Data(classWidth*i + " " + classWidth*(i+1), clickCostFreqDens.get(i)));
//
//            }
//        }

        clickCostHistogram.getData().addAll(histogramSeries);

    }
}