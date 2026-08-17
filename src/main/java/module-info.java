module com.example.agendajavafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.agendajavafx to javafx.fxml;
    exports com.example.agendajavafx;
}