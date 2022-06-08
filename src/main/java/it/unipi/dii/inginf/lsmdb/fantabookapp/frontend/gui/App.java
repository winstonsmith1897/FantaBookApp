package it.unipi.dii.inginf.lsmdb.fantabookapp.frontend.gui;

import it.unipi.dii.inginf.lsmdb.fantabookapp.frontend.MiddlewareConnector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private MiddlewareConnector connector;

    public static final double previewImageWidth = 200;

    @Override
    public void start(Stage stage) throws IOException {
        connector = MiddlewareConnector.getInstance();
        scene = new Scene(loadFXML("/welcome"), 1300, 750);
        scene.getRoot().requestFocus();
        stage.setScene(scene);
        stage.setTitle("FantaBook");

        stage.setOnCloseRequest(windowEvent -> connector.closeApplication());

        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        System.out.println(fxmlLoader);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}


