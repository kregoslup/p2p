package com.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.HashMap;

/**
 * Created by krego on 06.01.2017.
 */
class FilesMap {
    ObservableMap<String, byte[]> files;

    FilesMap(HashMap<String, byte[]> files){
        this.files = FXCollections.observableMap(files);
    }
}
