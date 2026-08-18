package com.example.agendajavafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CRUDApplication extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CRUDApplication.class.getResource("crud-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 840, 680);
        stage.setTitle("CRUD Personas");
        stage.setScene(scene);
        stage.show();
    }
}
