package Controller;

import Model.MainModel;
import View.MainView;

public class MainController {
    private MainView view;
    private MainModel model;

    public MainController(MainView view, MainModel model){
        this.view = view;
        this.model = model;
        System.out.println("init controller");
    }
}
