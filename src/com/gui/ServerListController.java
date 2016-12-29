package com.gui;

import com.server.Host;
import com.server.ServerCreatingError;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.event.ActionEvent;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by krego on 29.12.2016.
 */
public class ServerListController implements Initializable{
    @FXML public Button newHostButton;
    @FXML public Button showFilesButton;
    @FXML public TextField portNumberInput;
    @FXML private TableView<Host> tableView = new TableView<>();
    @FXML private TableColumn<Host, Integer> hostNumber;
    @FXML private TableColumn<Host, Integer> portNumber;
    @FXML private TableColumn<Host, Boolean> hostStatus;
    public static final int MAX_PORT_DIGITS = 5;

    private void configureInputField(){
        addTextFieldValidation();
    }

    private void addTextFieldValidation(){
        portNumberInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")){
                portNumberInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (portNumberInput.getText().length() > ServerListController.MAX_PORT_DIGITS) {
                String s = portNumberInput.getText().substring(0, ServerListController.MAX_PORT_DIGITS);
                portNumberInput.setText(s);
            }
        });
    }

    private void createTable(){
        tableView = new TableView<>();
        hostNumber = new TableColumn<>();
        portNumber = new TableColumn<>();
        hostStatus = new TableColumn<>();
    }

    private void setCellValueFactory(){
        hostNumber.setCellValueFactory(new PropertyValueFactory<Host, Integer>("ID"));
        portNumber.setCellValueFactory(new PropertyValueFactory<Host, Integer>("port number"));
        hostStatus.setCellValueFactory(new PropertyValueFactory<Host, Boolean>("status"));
    }

    private void configureTableView(){
        createTable();
        setCellValueFactory();
        tableView.getColumns().addAll(hostNumber, portNumber, hostStatus);
        tableView.getItems().setAll(parseHostsList());
    }

    private List<Host> parseHostsList(){
        return Context.getInstance().getHosts();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureInputField();
        configureTableView();
    }

    private boolean portNumberExists(){
        int port = Integer.parseInt(portNumberInput.getText());
        for (Host host : Context.getInstance().getHosts()){
            if (host.portNumber.get() == port){
                return true;
            }
        }
        return false;
    }

    private boolean validPortNumber(){
        if (!portNumberInput.getText().isEmpty()) {
            int port = Integer.valueOf(portNumberInput.getText());
            if (port > Host.MIN_PORT && port < Host.MAX_PORT && !portNumberExists()) {
                return true;
            }
        }
        return false;
    }

    @FXML
    public void addNewHost(ActionEvent actionEvent) {
        if (validPortNumber()){
            try {
                Context.getInstance()
                        .addHost(new Host(
                                Integer.valueOf(portNumberInput.textProperty().get()),
                                Context.MAX_THREAD_POOL_SIZE_PER_HOST));
            } catch (IOException e) {
                e.printStackTrace();
                throw new ServerCreatingError();
            }
        }else{
            showPortAlertError();
        }
    }

    private void showPortAlertError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Nieprawidlowy numer portu");
        alert.showAndWait();
    }

}
