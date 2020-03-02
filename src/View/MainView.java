package View;

import Controller.MainController;
import Model.MainModel;
import com.sun.prism.shader.Solid_TextureYV12_AlphaTest_Loader;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;

public class MainView extends Application {

    private Stage stage;
    private Parent root;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        //root = FXMLLoader.load(getClass().getResource("/View/sample.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/sample.fxml"));
        root = fxmlLoader.load();
        MainController controller = fxmlLoader.getController();
        controller.setView(this);
        controller.setModel(new MainModel());

        stage = primaryStage;
        setUpTimeGranulationSlider(controller);

        primaryStage.setTitle("Dashboard");
        root.getStylesheets().add("/View/styles.css");
        primaryStage.setScene(new Scene(root, 300, 275));

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        primaryStage.setMaximized(true);
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(900);

        primaryStage.show();
    }

    private void setUpTimeGranulationSlider(MainController controller){
        Slider slider = (Slider) root.lookup("#timeGranulationSlider");
        slider.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double d) {
                switch (d.intValue()){
                    case MainController.SLIDER_DAY:
                        return "Days";
                    case MainController.SLIDER_WEEK:
                        return "Weeks";
                    case MainController.SLIDER_MONTH:
                        return "Months";
                    case MainController.SLIDER_YEAR:
                        return "Years";
                }
                return "";
            }

            @Override
            public Double fromString(String s) {
                return null;
            }
        });
        slider.setOnMouseReleased(mouseEvent -> {
            controller.onTimeGranulationSliderChanged((int) slider.getValue());
        });
        controller.onTimeGranulationSliderChanged(0);
    }

    public File showFileChooser(){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showOpenDialog(stage);
    }

    public Window getWindow(){
        return root.getScene().getWindow();
    }

    public void showErrorMessage(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

}
