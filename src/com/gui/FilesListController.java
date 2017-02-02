package com.gui;

import com.server.Client;
import com.server.RequestType;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by krego on 29.12.2016.
 */
public class FilesListController implements Initializable{
    @FXML public Button sendButton;
    @FXML public Button downloadButton;
    @FXML public TableView<ObservableMap.Entry<String, String>> tableView;
    @FXML public TableColumn<ObservableMap.Entry<String, String>, String> fileName;
    @FXML public TableColumn<ObservableMap.Entry<String, String>, String> MD5;
    private ObservableMap<String, String> files;
    private int port;
    private Stage stage;
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private File downloadPath;
    private String address;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setFilesMap();
        configureTableView();
    }

    void setHostPort(int port){
        this.port = port;
    }

    private void setFilesMap(){
        HashMap<String, String> filesMap = new HashMap<>();
        for (HashMap.Entry<String, byte[]> entry : Context.getInstance().getCurrentFiles().entrySet()){
            filesMap.put(entry.getKey(), generateHexMD5(entry.getValue()));
        }
        files = FXCollections.observableMap(filesMap);
    }

    void setAddress(String address){
        this.address = address;
    }

    private String generateHexMD5(byte[] md5){
        StringBuffer hexString = new StringBuffer();
        for (int i=0; i<md5.length; i++){
            if ((0xff & md5[i]) < 0x10) {
                hexString.append("0" + Integer.toHexString((0xFF & md5[i])));
            } else {
                hexString.append(Integer.toHexString(0xFF & md5[i]));
            }
        }
        return hexString.toString();
    }

    private void configureTableView() {
        fileName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey()));
        MD5.setCellValueFactory(param -> new SimpleStringProperty(new String(param.getValue().getValue())));
        tableView.setItems(FXCollections.observableArrayList(files.entrySet()));
    }

    public void sendFile(ActionEvent actionEvent) throws InterruptedException{
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file to send");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            executorService.submit(new Client(downloadPath, RequestType.PUSH, port, address, file.getPath()));
        }
    }

    public void downloadFile(ActionEvent actionEvent) {
        if (tableView.getSelectionModel().getSelectedItem() != null){
            String fileName = tableView.getSelectionModel().getSelectedItem().getKey();
            executorService.submit(new Client(downloadPath, RequestType.PULL, port, address, fileName));
        }
    }

    void setDownloadPath(File downloadPath) {
        this.downloadPath = downloadPath;
    }

    public void closeWindow(ActionEvent actionEvent) {
        executorService.shutdown();
        ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
    }

    public void pauseAction(ActionEvent actionEvent) {
        
    }
}
