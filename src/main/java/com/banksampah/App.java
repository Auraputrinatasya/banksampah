package com.banksampah;

import com.banksampah.util.ReportServer;
import com.banksampah.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        try { ReportServer.start(); }
        catch (Exception e) { System.err.println("Report server gagal: " + e.getMessage()); }

        LoginView loginView = new LoginView(stage);
        Scene scene = new Scene(loginView.getView(), 420, 500);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setTitle("Bank Sampah - Sistem Pengelolaan");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
