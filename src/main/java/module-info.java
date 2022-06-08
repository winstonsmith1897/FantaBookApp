module it.unipi.dii.inginf.lsmdb.fantabookapp.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;
    requires org.neo4j.driver;
    //requires com.google.common;
    //requires log4j;
    requires org.json;
    requires log4j;
    requires google.collections;
    //requires com.google.common;

    opens it.unipi.dii.inginf.lsmdb.fantabookapp.frontend.gui to javafx.fxml;
    exports it.unipi.dii.inginf.lsmdb.fantabookapp.frontend.gui;
}
