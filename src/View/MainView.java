package View;

import Controller.MainController;
import Model.MainModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainView extends Application {


    private void launch(){
        MainModel model = new MainModel();
        new MainController(this,model);
    }
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        launch();

        Parent root;
        root = FXMLLoader.load(getClass().getResource("/View/sample.fxml"));

        primaryStage.setTitle("Dashboard");
        primaryStage.setScene(new Scene(root, 300, 275));
        //primaryStage.setMaximized(true);
        primaryStage.show();
    }
}
