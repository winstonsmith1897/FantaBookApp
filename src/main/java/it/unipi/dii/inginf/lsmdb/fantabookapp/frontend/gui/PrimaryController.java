package it.unipi.dii.inginf.lsmdb.fantabookapp.frontend.gui;

import javafx.fxml.FXML;

import java.io.IOException;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
