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

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by krego on 29.12.2016.
 */
public class ServerListController implements Initializable{
    @FXML public Button newHostButton;
    @FXML public Button showFilesButton;
    @FXML public TextField portNumberInput;
    @FXML public Button start;
    @FXML private TableView<Host> tableView;
    @FXML private TableColumn<Host, Integer> hostNumber;
    @FXML private TableColumn<Host, Integer> portNumber;
    @FXML private TableColumn<Host, Boolean> hostStatus;
    private Host currentHost;

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

    private void setCellValueFactory(){
        hostNumber.setCellValueFactory(new PropertyValueFactory<Host, Integer>("hostNumber"));
        portNumber.setCellValueFactory(new PropertyValueFactory<Host, Integer>("portNumber"));
        hostStatus.setCellValueFactory(new PropertyValueFactory<Host, Boolean>("hostStatus"));
    }

    private void configureTableView(){
        setCellValueFactory();
        tableView.setItems(parseHostsList());
    }

    private ObservableList<Host> parseHostsList(){
        ObservableList<Host> hosts = FXCollections.observableArrayList();
        ObservableList<Host> allHosts = Context.getInstance().getHosts();
        hosts.addAll(allHosts.stream().filter(hostEntry -> hostEntry != currentHost).collect(Collectors.toList()));
        return hosts;
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

    void refresh(){
        tableView.setItems(parseHostsList());
    }

    private Host addHost(){
        if (validPortNumber()){
            try {
                Host host = new Host(
                        Integer.valueOf(portNumberInput.textProperty().get()),
                        Context.MAX_THREAD_POOL_SIZE_PER_HOST);
                Context.getInstance().addHost(host);
                return host;
            } catch (IOException e) {
                e.printStackTrace();
                throw new ServerCreatingError("Error while creating new server");
            }
        }else{
            showPortAlertError();
            throw new ServerCreatingError("Error while creating new server - wrong port");
        }
    }

    @FXML
    public void addNewHost(ActionEvent actionEvent) {
        loadNewWindow("main.fxml");
    }

    private void loadNewWindow(String fxmlName){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlName));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void showPortAlertError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Nieprawidlowy numer portu");
        alert.showAndWait();
    }

    private void startNewClient(Client client) throws InterruptedException{
        Thread thread = new Thread(client);
        thread.start();
        thread.join();
    }

    private Host getCurrentlySelectedHost(){
        return tableView.getSelectionModel().getSelectedItem();
    }

    private void openFilesWindow(){
        loadNewWindow("host.fxml");
    }

    public void showFiles(ActionEvent actionEvent) throws InterruptedException {
        if (tableView.getSelectionModel().getSelectedItem() != null){
            Host host = getCurrentlySelectedHost();
            Client client = new Client(RequestType.DIR, host.getPortNumber());
            startNewClient(client);
            Context.getInstance().setCurrentFiles(client.filesMap);
            openFilesWindow();
        }
    }

    public void startHost(ActionEvent actionEvent) {
        currentHost = addHost();
        Context.getInstance().addServerList(this);
    }
}
