package it.unipi.dii.inginf.lsmdb.fantabookapp.frontend.gui;


import it.unipi.dii.inginf.lsmdb.fantabookapp.frontend.MiddlewareConnector;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Player;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Squad;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.User;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.exception.ActionNotCompletedException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SquadPageController implements Initializable {
    private static Squad SquadToDisplay;
    private static final MiddlewareConnector connector = MiddlewareConnector.getInstance();

    @FXML private TextField nameText;
    @FXML private Label ownerText;
    @FXML private ImageView imageSquad;
    @FXML private ImageView binImage;
    @FXML private ImageView modifyImage;

    @FXML private VBox PlayerListBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        displaySquadInformation();

        //showing the information of every Player in the Squad
        List<Player> PlayerList = connector.getSquadPlayers(SquadToDisplay);
        for (Player Player: PlayerList)
            PlayerListBox.getChildren().add(getPlayerRecord(Player));

        PlayerListBox.setSpacing(5);
        PlayerListBox.setAlignment(Pos.CENTER); PlayerListBox.setFillWidth(true);
        //if the author of the Squad is the logged user, it has the possibility to delete the Squad
        if (SquadToDisplay.getName().equals(connector.getLoggedUser().getUsername()) && !SquadToDisplay.isFavourite()) {
            modifyImage.setVisible(true);
            modifyImage.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> modifySquadName());

            binImage.setVisible(true);
            binImage.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                connector.deleteSquad(SquadToDisplay);
                try {
                    App.setRoot("homepage");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void modifySquadName() {
        if(modifyImage.getImage().getUrl().equals("file:src/main/resources/img/modify.png")) {
            modifyImage.setImage(new Image("file:src/main/resources/img/confirm.png"));
            nameText.setText("Insert the new Squad name...");
            nameText.setEditable(true);
        }else{
            modifyImage.setImage(new Image("file:src/main/resources/img/modify.png"));
            nameText.setEditable(false);
            connector.setSquadName(SquadToDisplay, nameText.getText());
        }
    }


    public static void getSquadPage(Squad squad) throws IOException {
        try {
            SquadToDisplay = connector.getSquadById(squad.getID());
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
        }
        App.setRoot("/squadPage");

    }

    private void displaySquadInformation(){
        nameText.setText(SquadToDisplay.getName());
        ownerText.setText("Owner " + SquadToDisplay.getOwner());
        //adding the event to move to the Player page by clicking it
        ownerText.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                userPageController.getUserPage(new User(SquadToDisplay.getOwner()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            event.consume();
        });
        setSquadImage();

    }

    private void setSquadImage() {
        Image SquadImage;
        try {
            if (SquadToDisplay.getUrlImage() == null || SquadToDisplay.getUrlImage().equals(""))
                throw new Exception();
            SquadImage = new Image(
                    SquadToDisplay.getUrlImage(),
                    310,
                    0,
                    true,
                    true,
                    true
            );
        }
        catch(Exception ex) {
            SquadImage = new Image(
                    "file:src/main/resources/gui/img/empty.jpg",
                    310,
                    0,
                    true,
                    true,
                    true
            );
        }

        imageSquad.setImage(SquadImage);
    }

    private HBox getPlayerRecord(Player Player){
        HBox PlayerBox;

        Label name = new Label(Player.getName()); name.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        Label team = new Label(Player.getTeam()); team.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        ImageView PlayerImageView = new ImageView();
        setPlayerImage(PlayerImageView, Player);

        name.setMinWidth(600); name.setTextAlignment(TextAlignment.CENTER);
        team.setMinWidth(340); team.setTextAlignment(TextAlignment.CENTER);

        ImageView heartImageView = new ImageView(); heartImageView.setStyle("-fx-cursor: hand;");
        heartImageView.setPickOnBounds(true);
        if(connector.isFavouritePlayer(Player)){
            heartImageView.setImage(new Image("file:src/main/resources/img/heart.png"));
        }else{
            heartImageView.setImage(new Image("file:src/main/resources/img/nonHeart.png"));
        }
        //Image heartImage = new Image("file:src/main/resources/it/unipi/dii/inginf/lsmdb/unimusic/frontend/gui/img/heart.png");
        //heartImageView.setImage(heartImage); heartImageView.setY(60);

        //adding the possibility to click on the heart to add a Player to favourites
        heartImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                if (heartImageView.getImage().getUrl().endsWith("nonHeart.png")) {
                    connector.addPlayerToFavourites(Player);
                    heartImageView.setImage(new Image("file:src/main/resources/img/heart.png"));
                }else{
                    connector.removePlayerFromFavourites(Player);
                    heartImageView.setImage(new Image("file:src/main/resources/img/nonHeart.png"));
                }
            }catch(Exception ex){
                try {
                    connector.addPlayerToFavourite(connector.getLoggedUser(), Player);
                } catch (ActionNotCompletedException e) {
                    e.printStackTrace();
                }
            }
            event.consume();
        });


        HBox PlayerInformationBox = new HBox(PlayerImageView, name, team); PlayerInformationBox.setStyle("-fx-cursor: hand;");
        PlayerInformationBox.setAlignment(Pos.CENTER); PlayerInformationBox.setSpacing(5);
        //adding the event to move to the Player page by clicking it
        PlayerInformationBox.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                PlayerPageController.getPlayerPage(Player);
            } catch (IOException e) {
                e.printStackTrace();
            }
            event.consume();
        });

        //if the user is the owner of the Squad he can remove Players from the Squad
        if (SquadToDisplay.getOwner().equals(connector.getLoggedUser().getUsername()) && !SquadToDisplay.isFavourite()) {
            ImageView removePlayerImageView = new ImageView(); removePlayerImageView.setStyle("-fx-cursor: hand;");
            removePlayerImageView.setImage(new Image("file:src/main/resources/it/unipi/dii/inginf/lsmdb/fantabook/frontend/gui/img/delete.png"));

            PlayerBox = new HBox(heartImageView, PlayerInformationBox, removePlayerImageView);

            removePlayerImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                connector.deletePlayerFromSquad(Player, SquadToDisplay);
                PlayerListBox.getChildren().remove(PlayerBox);
            });
        }
        else
            PlayerBox = new HBox(heartImageView, PlayerInformationBox);

        PlayerBox.setStyle("-fx-background-color: transparent");
        PlayerBox.setAlignment(Pos.CENTER); PlayerBox.setSpacing(10);

        return PlayerBox;
    }

    private void setPlayerImage(ImageView imageView, Player Player) {
        Image PlayerImage;

        PlayerImage = new Image(
                "file:src/main/resources/img/empty.jpg",
                50,
                0,
                true,
                true,
                true
        );

        try {
            PlayerImage = new Image(
                    Player.getImageUrl(),
                    50,
                    0,
                    true,
                    true,
                    true
            );

            if(PlayerImage.isError()) {
                throw new Exception();
            }

        }catch(Exception ex){
            System.out.println("!!!!!!!!!!!!!!!!!!!!Squad ERROR!!!!!!!!!!!!!");

        }
        imageView.setImage(PlayerImage);


    }
}
