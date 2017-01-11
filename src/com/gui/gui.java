package com.gui;/**
 * Created by krego on 28.12.2016.
 */

import com.server.Error;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class gui extends Application {
    private int appNumber;

    public static void main(String[] args) {
        launch(args);
    }

    private void getAppNumber(Parameters params) {
        if(!params.getRaw().isEmpty()){
            try {
                appNumber = Integer.parseInt(params.getRaw().get(0));
            }catch (NumberFormatException e){
                throw new Error("Cannot parse argument to int");
            }
        } else {
            throw new Error("No command line arguments passed");
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        setUpStage(primaryStage);
    }

    private void setUpStage(Stage primaryStage) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        ServerListController controller = fxmlLoader.getController();
        getAppNumber(getParameters());
        controller.setAppNumber(appNumber);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        configureStage(primaryStage);
        primaryStage.show();
    }

    private void configureStage(Stage primaryStage){
        primaryStage.setOnCloseRequest(event -> {
            HostsController.closeHost(Context.getInstance().getExecutor());
        });
    }
}
