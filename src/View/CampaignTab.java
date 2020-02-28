package View;

import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class CampaignTab extends Tab {

    private final int ROW_NUMBER = 11;


    GridPane pane;

    public CampaignTab(){
        initCampaignTab();
    }

    private void initCampaignTab(){
        setText("Campaign 1");
        pane = new GridPane();
        pane.getStylesheets().add("View/styles.css");
        setContent(pane);
        setConstraints();

        RadioButton radioButton;

        for(int i = 0; i < ROW_NUMBER; i++){
            radioButton = new RadioButton(Integer.toString(i));
            //radioButton.getStyleClass().add("campaign-tab-radio-button");
            if(i % 2 == 0){
                radioButton.getStyleClass().add("campaign-tab-radio-button-1");
            }
            else {
                radioButton.getStyleClass().add("campaign-tab-radio-button-2");
            }
            GridPane.setFillHeight(radioButton,true);
            GridPane.setFillWidth(radioButton,true);
            radioButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            pane.add(radioButton,0,i,1,1);
        }
    }

    private void setConstraints(){
        ColumnConstraints colConst = new ColumnConstraints();
        colConst.setPercentWidth(100.0);
        pane.getColumnConstraints().add(colConst);

        for (int i = 0; i < 20; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / 11);
            pane.getRowConstraints().add(rowConst);
        }
    }
}
