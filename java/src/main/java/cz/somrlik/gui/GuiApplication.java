package cz.somrlik.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class GuiApplication extends Application {

    private static Model model;

    @Override
    public void start(Stage primaryStage) {
        model = new Model();
        new Presenter(primaryStage, model);
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Throwable e) {
            System.out.println("!!! Uncaught Exception occurred in the app !!!");
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            model.halt();
        }
    }
}
