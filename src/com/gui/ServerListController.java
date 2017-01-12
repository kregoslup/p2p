package com.gui;

import com.server.Client;
import com.server.Host;
import com.server.RequestType;
import com.server.ServerCreatingError;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by krego on 29.12.2016.
 */
public class ServerListController implements Initializable{
    @FXML public Button showFilesButton;
    @FXML public TextField portNumberInput;
    @FXML public Button start;
    @FXML public Label portLabel;
    private Host currentHost;
    private int appNumber;

    private void configureInputField(){
        addTextFieldValidation();
    }

    private void addTextFieldValidation(){
        portNumberInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")){
                portNumberInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureInputField();
    }

    private boolean validPortNumber(){
        if (!portNumberInput.getText().isEmpty()) {
            int port = Integer.valueOf(portNumberInput.getText());
            if (port > Host.MIN_PORT && port < Host.MAX_PORT) {
                return true;
            }
        }
        return false;
    }

    private FXMLLoader loadNewWindow(String fxmlName){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlName));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
            return fxmlLoader;
        } catch(Exception e) {
            e.printStackTrace();
            throw new Error("Error creating new window");
        }
    }

    void setAppNumber(int num){
        this.appNumber = num;
    }

    private void showAlertError(String alertText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(alertText);
        alert.showAndWait();
    }

    private void startNewClient(Client client) throws InterruptedException{
        Thread thread = new Thread(client);
        thread.start();
        thread.join();
    }

    private void openFilesWindow(int port, File downloadPath) {
        FXMLLoader loader = loadNewWindow("host.fxml");
        FilesListController controller = loader.<FilesListController>getController();
        controller.setHostPort(port);
        controller.setDownloadPath(downloadPath);
    }

    public void showFiles(ActionEvent actionEvent) throws InterruptedException{
        if (validPortNumber() && currentHost != null){
            int portNumber = Integer.valueOf(portNumberInput.textProperty().get());
            try {
                Client client = new Client(RequestType.DIR, portNumber, currentHost.getDownloadPath());
                startNewClient(client);
                Context.getInstance().setCurrentFiles(client.filesMap);
                openFilesWindow(portNumber, currentHost.getDownloadPath());
            }catch (RuntimeException e){
                showAlertError(AlertErrorConstants.NO_CONN);
            }
        }else {
            showAlertError(AlertErrorConstants.SHOW_FILES_ERR);
        }
    }

    public void startHost(ActionEvent actionEvent) {
        if (currentHost == null && validPortNumber()) {
            try {
                currentHost = new Host(
                        Integer.valueOf(portNumberInput.textProperty().get()),
                        Context.MAX_THREAD_POOL_SIZE_PER_HOST,
                        appNumber);
                Context.getInstance().addHost(currentHost);
            } catch (IOException e) {
                e.printStackTrace();
                throw new ServerCreatingError("Error while creating new server");
            }
            portNumberInput.setText("");
            portLabel.setText("Działa: " + currentHost.portNumber);
        }else if(currentHost != null){
            showAlertError(AlertErrorConstants.NEW_HOST_ERR);
        }else if (!validPortNumber()){
            showAlertError(AlertErrorConstants.ADD_HOST_ERR);
        }
    }
}
