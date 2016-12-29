package com.gui;

import com.server.Host;
import com.server.ServerCreatingError;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by krego on 29.12.2016.
 */
public class ServerListController implements Initializable{
    public Button newHostButton;
    public Button showFilesButton;
    public TextField portNumberInput;
    @FXML private TableView<Host> tableView = new TableView<>();
    @FXML private TableColumn<Host, Integer> rowID;
    @FXML private TableColumn<Host, Integer> portNumber;
    @FXML private TableColumn<Host, Boolean> hostStatus;

    private void configureButtons(){
        newHostButton = new Button();
        showFilesButton = new Button();
        addButtonActions();
    }

    private void configureInputField(){
        portNumberInput = new TextField();
        addTextFieldValidation();
    }

    private void addTextFieldValidation(){
        portNumberInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")){
                portNumberInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void addButtonActions(){
        newHostButton.setOnAction(event -> {
            if (!portNumberInput.textProperty().isEmpty().get()){
                try {
                    Context.getInstance()
                            .addHost(new Host(
                                    Integer.valueOf(portNumberInput.textProperty().get()),
                                    Context.getInstance().getHosts().size(),
                                    Context.MAX_THREAD_POOL_SIZE_PER_HOST));
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new ServerCreatingError();
                }
            }
        });
    }

    private void createTable(){
        tableView = new TableView<>();
        rowID = new TableColumn<>();
        portNumber = new TableColumn<>();
        hostStatus = new TableColumn<>();
    }

    private void setCellValueFactory(){
        rowID.setCellValueFactory(new PropertyValueFactory<Host, Integer>("ID"));
        portNumber.setCellValueFactory(new PropertyValueFactory<Host, Integer>("port number"));
        hostStatus.setCellValueFactory(new PropertyValueFactory<Host, Boolean>("status"));
    }

    private void configureTableView(){
        createTable();
        setCellValueFactory();
        tableView.getColumns().addAll(rowID, portNumber, hostStatus);
        tableView.getItems().setAll(parseHostsList());
    }

    private List<Host> parseHostsList(){
        return Context.getInstance().getHosts();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureButtons();
        configureInputField();
        configureTableView();
    }
}
