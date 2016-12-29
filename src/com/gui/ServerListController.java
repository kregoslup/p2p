package com.gui;

import com.server.Host;
import com.server.ServerCreatingError;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by krego on 29.12.2016.
 */
public class ServerListController implements Initializable{
    public Button newHostButton;
    public Button showFilesButton;
    public TextField portNumberInput;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureButtons();
        configureInputField();
    }
}
