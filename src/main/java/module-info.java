module org.example.chat_application {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.chat_application to javafx.fxml;
    exports org.example.chat_application;
}