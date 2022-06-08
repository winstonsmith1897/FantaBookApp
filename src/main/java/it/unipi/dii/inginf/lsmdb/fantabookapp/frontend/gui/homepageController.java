package it.unipi.dii.inginf.lsmdb.fantabookapp.frontend.gui;


import it.unipi.dii.inginf.lsmdb.fantabookapp.frontend.MiddlewareConnector;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Player;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Squad;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class homepageController implements Initializable {
    private static final MiddlewareConnector connector = MiddlewareConnector.getInstance();

    @FXML private ScrollPane scrollPane;
    @FXML private VBox verticalScroll;

    @FXML private ScrollPane hotPlayersScroll;
    @FXML private HBox hotPlayersPane;

    @FXML private ScrollPane suggSquadsScroll;
    @FXML private HBox suggSquadsPane;

    @FXML private ScrollPane suggUsersScroll;
    @FXML private HBox suggUsersPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        scrollPane.setFitToWidth(true);
        verticalScroll.setSpacing(75);

        hotPlayersScroll.setFitToHeight(true); hotPlayersScroll.setMinViewportHeight(340);
        hotPlayersPane.setSpacing(5);

        suggSquadsScroll.setFitToHeight(true); suggSquadsScroll.setMinViewportHeight(340);
        suggSquadsPane.setSpacing(5);

        suggUsersScroll.setFitToHeight(true); suggUsersScroll.setMinViewportHeight(200);
        suggUsersPane.setSpacing(5);

        displayHotPlayers();
        displaySuggSquads();
        displaySuggUsers();

    }

    //--------------------------------------------------------------------------------------------------------

    private void displayHotPlayers() {
        List<Player> hotPlayers = connector.getBestPlayers();
        if(hotPlayers.size() == 0)
            displayEmpty(hotPlayersPane);
        else {
            for(Player Player: hotPlayers) {
                hotPlayersPane.getChildren().add(createPlayerPreview(Player));
            }
        }
    }

    private void displaySuggSquads() {
        List<Squad> sugSquads = connector.getSuggestedSquads();
        if(sugSquads.size() == 0)
            displayEmpty(suggSquadsPane);
        else {
            for(Squad Squad: sugSquads) {
                suggSquadsPane.getChildren().add(createSquadPreview(Squad));
            }
        }
    }

    private void displaySuggUsers() {
        List<User> suggUsers = connector.getSuggestedUsers();
        if(suggUsers.size() == 0)
            displayEmpty(suggUsersPane);
        else {
            for(User user: suggUsers) {
                suggUsersPane.getChildren().add(createUserPreview(user));
            }
        }
    }

    //--------------------------------------------------------------------------------------------------------

    private void displayEmpty(Pane pane) {
        pane.getChildren().clear();

        Text emptyText = new Text("<EMPTY>");
        emptyText.setStyle("-fx-font-size: 28");
        emptyText.getStyleClass().add("text-id");

        pane.getChildren().add(emptyText);
    }

    private Button createPlayerPreview(Player Player) {
        Button PlayerPreview = new Button(); PlayerPreview.setStyle("-fx-background-color: transparent");
        PlayerPreview.setOnAction(actionEvent -> {

            try {
                PlayerPageController.getPlayerPage(Player);
            } catch (IOException e) {

            }
        });

        Image PlayerImage;
        VBox PlayerGraphic = new VBox(5);
        try {
           System.out.println("-----------------------------PLAYER PREVIEW----------------------------------------------");
            PlayerImage = new Image(
                    Player.getImageUrl(),
                    App.previewImageWidth,
                    0,
                    true,
                    true,
                    true
            );

            if(PlayerImage.isError()) {
                throw new Exception();
            }

        }catch(Exception ex){
            PlayerImage = new Image(
                    "file:src/main/resources/it/unipi/dii/inginf/lsmdb/fantabook/frontend/gui/img/empty.jpg",
                    App.previewImageWidth,
                    0,
                    true,
                    true,
                    true
            );
        }

        ImageView PlayerImageView = new ImageView(PlayerImage);
        Text name = new Text(Player.getName()); name.setWrappingWidth(App.previewImageWidth); name.setFill(Color.WHITE);
        Text team = new Text(Player.getTeam()); team.setWrappingWidth(App.previewImageWidth); team.setFill(Color.GRAY);

        PlayerGraphic.getChildren().addAll(PlayerImageView, name, team);

        PlayerPreview.setGraphic(PlayerGraphic);
        return PlayerPreview;
    }

    private Button createSquadPreview(Squad Squad) {
        Button SquadPreview = new Button(); SquadPreview.setStyle("-fx-background-color: transparent");
        SquadPreview.setOnAction(actionEvent -> {
            try {
                SquadPageController.getSquadPage(Squad);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Image SquadImage;
        VBox SquadGraphic = new VBox(5);
        try {

            if(Squad.getUrlImage() == null || Squad.getUrlImage().equals("")) {
                throw new Exception();
            }

            SquadImage = new Image(
                    Squad.getUrlImage(),
                    App.previewImageWidth,
                    0,
                    true,
                    true,
                    true
            );

            if(SquadImage.isError()) {
                throw new Exception();
            }

        }catch(Exception ex){
            SquadImage = new Image(
                    "file:src/main/resources/img/empty.jpg",
                    App.previewImageWidth,
                    0,
                    true,
                    true,
                    true
            );
        }
        ImageView SquadImageView = new ImageView(SquadImage);
        Text pName = new Text(Squad.getName()); pName.setWrappingWidth(App.previewImageWidth); pName.setFill(Color.WHITE);

        SquadGraphic.getChildren().addAll(SquadImageView, pName);

        SquadPreview.setGraphic(SquadGraphic);
        return SquadPreview;
    }

    private Button createUserPreview(User user) {
        Button PlayerPreview = new Button(); PlayerPreview.setStyle("-fx-background-color: transparent");
        PlayerPreview.setOnAction(actionEvent -> {
            try {
                userPageController.getUserPage(user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Image userImage = new Image(
                "file:src/main/resources/user.png",
                App.previewImageWidth,
                0,
                true,
                true,
                true
        );
        ImageView userImageView = new ImageView(userImage);
        Text username = new Text(user.getUsername()); username.setWrappingWidth(App.previewImageWidth); username.setFill(Color.WHITE);

        VBox userGraphic = new VBox(5); userGraphic.getChildren().addAll(userImageView, username);
        PlayerPreview.setGraphic(userGraphic);
        return PlayerPreview;
    }
}
