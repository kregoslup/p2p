package com.gui;/**
 * Created by krego on 28.12.2016.
 */

import com.server.Host;
import com.server.HostsController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class gui extends Application {
    private ArrayList<Host> hosts;
    private Parent root;
    private Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        setUpStage(primaryStage);
    }

    private void setUpStage(Stage primaryStage) throws IOException{
        root = FXMLLoader.load(getClass().getResource("main.fxml"));
        scene = new Scene(root);
        primaryStage.setScene(scene);
        configureStage(primaryStage);
    }

    private void configureStage(Stage primaryStage){
        primaryStage.setOnCloseRequest(event -> {
            HostsController.closeAllHosts(hosts);
        });
    }

    void configureHosts(){
        hosts = new ArrayList<>();
    }
}
