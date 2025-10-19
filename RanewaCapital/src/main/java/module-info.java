module com.ooad.ranewacapital {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.ooad.ranewacapital to javafx.fxml;
    exports com.ooad.ranewacapital;
}