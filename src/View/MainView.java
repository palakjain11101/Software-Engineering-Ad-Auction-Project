package View;

import Controller.MainController;
import Model.MainModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainView extends Application {


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

        primaryStage.setTitle("Dashboard");
        root.getStylesheets().add("/View/styles.css");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}
