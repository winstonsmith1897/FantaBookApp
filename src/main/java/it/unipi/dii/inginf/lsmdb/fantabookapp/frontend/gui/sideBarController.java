package it.unipi.dii.inginf.lsmdb.fantabookapp.frontend.gui;

import it.unipi.dii.inginf.lsmdb.fantabookapp.frontend.MiddlewareConnector;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.PrivilegeLevel;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Squad;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.exception.ActionNotCompletedException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class sideBarController implements Initializable {
    private MiddlewareConnector connector;

    @FXML private AnchorPane parentPane;

    @FXML private Button home;
    @FXML private Button favourites;
    @FXML private Button addSquad;
    @FXML private Button personalProfile;
    @FXML private Button statistics;
    @FXML private Button logout;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connector = MiddlewareConnector.getInstance();

        home.setOnAction(actionEvent -> {
            try {
                App.setRoot("/homepage");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        favourites.setOnAction(actionEvent -> {
            try {
                SquadPageController.getSquadPage(connector.getFavourite());
            } catch (ActionNotCompletedException | IOException e) {
                e.printStackTrace();
            }
        });
// ||
        if (connector.getLoggedUser().getPrivilegeLevel() == null
                && connector.getLoggedUser().getPrivilegeLevel() != PrivilegeLevel.ADMIN
        ) {
            parentPane.getChildren().remove(statistics);
        } else {
            statistics.setOnAction(actionEvent -> {
                try {
                    App.setRoot("/statistics");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        personalProfile.setOnAction(actionEvent -> {
            try {
                userPageController.getUserPage(MiddlewareConnector.getInstance().getLoggedUser());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        logout.setOnAction(actionEvent -> {
            connector.logout();
            try {
                App.setRoot("/welcome");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        addSquad.setOnAction(actionEvent -> {
            AnchorPane background;
            AnchorPane addSquadPane;
            Label labelName = new Label("Name of the Squad:");
            TextField nameForm = new TextField(); nameForm.setStyle("-fx-background-color: white; -fx-font-weight: bold; " +
                                                                    "-fx-font-size: 16px; -fx-text-fill: black;");
            nameForm.setMinWidth(480);
            Label labelUrlImage = new Label("Image of the Squad (optional):");
            TextField urlImageForm = new TextField(); urlImageForm.setStyle("-fx-background-color: white; -fx-font-weight: bold; " +
                                                                            "-fx-font-size: 16px; -fx-text-fill: black;");
            urlImageForm.setMinWidth(480);

            Button cancelButton = new Button("Cancel");
            Button addButton = new Button("Add");
            HBox buttons = new HBox(addButton, cancelButton); buttons.setAlignment(Pos.CENTER);
            buttons.setSpacing(20);

            VBox addSquadBox = new VBox(labelName, nameForm, labelUrlImage, urlImageForm, buttons);
            addSquadBox.setPrefSize(500,200); addSquadBox.setAlignment(Pos.CENTER);

            addSquadPane = new AnchorPane(addSquadBox); addSquadPane.toFront();
            addSquadPane.setPrefSize(500,200); addSquadPane.setStyle("-fx-background-color: #424242;\n" +
                                                                                 "  -fx-background-radius: 5px, 5px, 5px, 5px;");
            addSquadPane.setLayoutX(350); addSquadPane.setLayoutY(300);
            addSquadBox.setSpacing(10);

            background = new AnchorPane();
            background.setStyle("-fx-background-color: #494949; -fx-opacity: 0.7;");
            background.setPrefSize(1300, 750); background.toFront();
            parentPane.getChildren().addAll(background, addSquadPane);

            cancelButton.setOnAction(actionEvent12 -> {
                parentPane.getChildren().remove(background);
                parentPane.getChildren().remove(addSquadPane);
            });

            addButton.setOnAction(actionEvent1 -> {
                Squad squad = new Squad(connector.getLoggedUser().getUsername(), nameForm.getText());
                if (!urlImageForm.getText().equals(""))
                    squad.setUrlImage(urlImageForm.getText());

                try {
                    connector.createSquad(squad);
                } catch (ActionNotCompletedException e) {
                    e.printStackTrace();
                }
                parentPane.getChildren().remove(background);
                parentPane.getChildren().remove(addSquadPane);
            });
        });
    }

}