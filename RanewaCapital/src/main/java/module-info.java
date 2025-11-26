module com.ooad.ranewacapital {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;


    opens com.ooad.ranewacapital to javafx.fxml;
    exports com.ooad.ranewacapital;
}