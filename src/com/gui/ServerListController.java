package com.gui;

import com.server.*;
import com.server.Error;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

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

    private String getIPAdress(String address){
        int colonIndex = address.indexOf(":");
        if (colonIndex != -1) {
            return address.substring(0, colonIndex);
        }
        throw new Error("Error getting ip address");
    }

    int getPort(String address){
        int colonIndex = address.indexOf(":");
        try {
            return Integer.valueOf(address.substring(colonIndex+1));
        }catch (NumberFormatException e){
            showAlertError("Niepoprawny numer portu");
            throw new Error("Error parsing port");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

    private void openFilesWindow(int port, File downloadPath, String address) {
        FXMLLoader loader = loadNewWindow("host.fxml");
        FilesListController controller = loader.<FilesListController>getController();
        controller.setHostPort(port);
        controller.setDownloadPath(downloadPath);
        controller.setAddress(address);
    }

    public void showFiles(ActionEvent actionEvent) throws InterruptedException{
        if (portNumberInput.getText() != null || !(portNumberInput.getText().equals(""))) {
            if (currentHost != null) {
                int portNumber = getPort(portNumberInput.getText());
                String address = getIPAdress(portNumberInput.getText());
                try {
                    Client client = new Client(RequestType.DIR, portNumber, address, currentHost.getDownloadPath());
                    startNewClient(client);
                    Context.getInstance().setCurrentFiles(client.filesMap);
                    openFilesWindow(portNumber, currentHost.getDownloadPath(), address);
                } catch (RuntimeException e) {
                    showAlertError(AlertErrorConstants.NO_CONN);
                }
            } else {
                showAlertError(AlertErrorConstants.SHOW_FILES_ERR);
            }
        }
    }

    public void startHost(ActionEvent actionEvent) {
        if (portNumberInput.getText() != null) {
            if (currentHost == null) {
                try {
                    int port = Integer.valueOf(portNumberInput.getText());
                    currentHost = new Host(
                            port,
                            Context.MAX_THREAD_POOL_SIZE_PER_HOST,
                            appNumber);
                    Context.getInstance().addHost(currentHost);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new ServerCreatingError("Error while creating new server");
                } catch (NumberFormatException e){
                    showAlertError("Nieprawidlowy port");
                }
                portNumberInput.setText("");
                portLabel.setText("Działa: " + currentHost.portNumber);
            } else {
                showAlertError(AlertErrorConstants.NEW_HOST_ERR);
            }
        }
    }

    public void closeWindow(ActionEvent actionEvent) throws InterruptedException{
        Context.getInstance().getExecutor().shutdownNow();
        if (!Context.getInstance().getExecutor().awaitTermination(100, TimeUnit.MICROSECONDS)) {
            System.exit(0);
        }
        ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
    }
}
