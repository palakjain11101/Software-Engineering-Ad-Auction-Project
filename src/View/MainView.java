package View;

import Controller.MainController;
import Model.MainModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MainView extends Application {

    Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root;
        //root = FXMLLoader.load(getClass().getResource("/View/sample.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/sample.fxml"));
        root = fxmlLoader.load();
        MainController controller = fxmlLoader.getController();
        controller.setView(this);
        controller.setModel(new MainModel());

        stage = primaryStage;

        primaryStage.setTitle("Dashboard");
        root.getStylesheets().add("/View/styles.css");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public File showFileChooser(){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showOpenDialog(stage);
    }

    public void showErrorMessage(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

}
