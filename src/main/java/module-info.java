module com.event.event {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;           // ← il manquait ça !
    requires java.sql;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.desktop;



    // OUVRE les packages où sont tes contrôleurs
    opens com.gestion.evenements to javafx.fxml;
    opens com.gestion.evenements.controller to javafx.fxml;
    // AJOUTEZ CETTE LIGNE :
    opens com.gestion.evenements.model.membres.entities to javafx.base;

    exports com.gestion.evenements;
    exports com.gestion.evenements.controller;
    exports com.gestion.evenements.controller.evenements;
    opens com.gestion.evenements.controller.evenements to javafx.fxml;
    opens com.gestion.evenements.view.evenements to javafx.fxml;
}