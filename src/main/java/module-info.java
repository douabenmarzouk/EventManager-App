module com.event.event {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;


    opens com.gestion.evenements to javafx.fxml;
    exports com.gestion.evenements;
}