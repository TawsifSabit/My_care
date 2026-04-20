package com.mycare.view.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EmergencyManagementApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("EmergencyManagementView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("EmergencyManagementStyle.css").toExternalForm());

        stage.setTitle("MyCare - Emergency Management System");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void launchEmergencyManagement() {
        // Launch JavaFX application on a new thread if not already running
        if (primaryStage == null || !primaryStage.isShowing()) {
            new Thread(() -> Application.launch(EmergencyManagementApp.class)).start();
        } else {
            // If already running, bring to front
            primaryStage.toFront();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}