package it.unipi.dii.inginf.lsmdb.fantabookapp.frontend.gui;


import it.unipi.dii.inginf.lsmdb.fantabookapp.frontend.MiddlewareConnector;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Player;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Squad;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.exception.ActionNotCompletedException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

public class PlayerPageController implements Initializable {
    private static final MiddlewareConnector connector = MiddlewareConnector.getInstance();
    private static Player PlayerToDisplay;

    @FXML private Button favouriteButton;
    @FXML private Button squadButton;
    @FXML private Button likeButton;
    @FXML private Button closeListButton;


    @FXML private TextField nameText;
    @FXML private TextField teamText;
    @FXML private TextField ageText;
    @FXML private TextField NationalityText;
    @FXML private TextField positionText;
    @FXML private TextField goalText;

    @FXML private TextField minuteText;
    @FXML private TextField assistText;
    @FXML private TextField redCardText;
    @FXML private TextField valueText;
    @FXML private TextField NumberText;







    @FXML private Label ratingLabel;
    @FXML private Label likeLabel;


    @FXML private ImageView imagePlayer;
    @FXML private ImageView favouriteImg;
    @FXML private ImageView likeImg;

    @FXML private ScrollPane scrollList;
    @FXML private ListView<Node> SquadList;


    /**
     * @param Player The Player to be displayed in the page.
     */
    public static void getPlayerPage(Player Player) throws IOException {
        PlayerToDisplay = connector.getPlayerByName(Player);
        System.out.println(PlayerToDisplay.getName());
        System.out.println(PlayerToDisplay.getImageUrl());
        App.setRoot("/playerpage");

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setPlayerImage();
        displayPlayerInformation();
        initializeButton();
    }


    /**
     * Set the image of the player in the page.
     */
    private void setPlayerImage() {
        Image PlayerImage;
        try {
            PlayerImage = new Image(PlayerToDisplay.getImageUrl(),310,0,true,true,true);

            if(PlayerImage.isError()) {
                throw new Exception();
            }

        }catch(Exception ex){
            String filePath = "file:src/main/resources/img/empty.jpg";
            PlayerImage = new Image(filePath,310,0,true,true,true);
        }

        imagePlayer.setImage(PlayerImage);
    }


    /**
     * Add all Player's information.
     */
    private void displayPlayerInformation(){
        String[] name = PlayerToDisplay.getName().trim().split(" ", -1);
        nameText.setText(name[1]);

        String team = "Team: " + PlayerToDisplay.getTeam();
        teamText.setText(team);
        System.out.println(team);

        ageText.setText("Age: " + PlayerToDisplay.getAge());
        System.out.println(ageText);

        String Nationality = PlayerToDisplay.getNationality();
        NationalityText.setText("Nationality: " + Nationality);
        System.out.println(Nationality);

        int goal =  PlayerToDisplay.getGoals();

        goalText.setText("Goal: " + goal);

        positionText.setText("Position: " + PlayerToDisplay.getPosition());

        String minutes = PlayerToDisplay.getMinutes();

        minuteText.setText("Minutes: " + minutes);

        String assist = PlayerToDisplay.getAssists();
        assistText.setText("Assists: " + assist);

        int RedCard = PlayerToDisplay.getRedCard();
        redCardText.setText("RedCard: " + RedCard);

        String Value = PlayerToDisplay.getValue();
        valueText.setText("Value: " + Value);

        String Number = PlayerToDisplay.getShirtNumber();
        NumberText.setText("Shirt: " + Number);


    }


    /**
     * Initializes all the actions associated to the button in the page.
     */
    private void initializeButton() {
        System.out.println("INITIALIZE BUTTON");

        likeButton.setOnAction(actionEvent -> handleLike());

        favouriteButton.setOnAction(actionEvent -> handleFavourite());

        squadButton.setOnAction(actionEvent -> handleSquad());

        System.out.println("allUserSquad");

        List<Squad> allUserSquad = connector.getOwnUserSquads();

        System.out.println(allUserSquad);

        for(Squad plToAdd: allUserSquad)
            SquadList.getItems().add(getsquadButton(plToAdd));

        closeListButton.setOnAction(actionEvent -> {
            closeListButton.setVisible(false);
            scrollList.setVisible(false);
        });

        if(connector.isLikedPlayer(PlayerToDisplay)){
            likeImg.setImage(new Image("file:src/main/resources/img/like.png"));
        }else{
            likeImg.setImage(new Image("file:src/main/resources/img/nonLike.png"));
        }

        if(connector.isFavouritePlayer(PlayerToDisplay)){
            favouriteImg.setImage(new Image("file:src/main/resources/img/heart.png"));
        }else{
            favouriteImg.setImage(new Image("file:src/main/resources/img/nonHeart.png"));
        }

        DecimalFormat formatter = new DecimalFormat();
        formatter.setMaximumFractionDigits(2);

        likeLabel.setText("Like: " + PlayerToDisplay.getLikeCount());
        ratingLabel.setText("Rating: " + formatter.format(PlayerToDisplay.getRating()));
    }

    private void handleSquad() {
        closeListButton.setVisible(true);
        scrollList.setVisible(true);
    }

    private Node getsquadButton(Squad plToAdd) {

        Button buttonSquad = new Button(plToAdd.getName());buttonSquad.setStyle("-fx-background-color: transparent");buttonSquad.setTextAlignment(TextAlignment.LEFT);
        System.out.println("ADDINF TO SQUAD");
        buttonSquad.setOnAction(actionEvent -> {
            try {
                connector.addPlayer(plToAdd, PlayerToDisplay);
                Button thisButton = (Button) actionEvent.getSource();
                thisButton.setText("Successfully added to " + thisButton.getText());
                thisButton.setDisable(true);
            } catch (ActionNotCompletedException e) {
                System.out.println("HELP22222222222222222222");
                e.printStackTrace();
            }
        });

        return buttonSquad;
    }

    private void handleLike() {
        try {
            int numLike = Integer.parseInt(likeLabel.getText().split(": ")[1]);
            if (likeImg.getImage().getUrl().endsWith("nonLike.png")) {
                connector.likePlayer(PlayerToDisplay);
                likeImg.setImage(new Image("file:src/main/resources/img/like.png"));
                likeLabel.setText("Like: " + (numLike + 1));
            }else{
                connector.deleteLike(PlayerToDisplay);
                likeImg.setImage(new Image("file:src/main/resources/img/nonLike.png"));
                likeLabel.setText("Like: " + (numLike - 1));
            }
        }catch(Exception ex){
            System.out.println("HELP1111111111111111111111111111111");
            ex.printStackTrace();
        }
    }

    private void handleFavourite() {
        try {
            if (favouriteImg.getImage().getUrl().endsWith("nonHeart.png")) {
                favouriteImg.setImage(new Image("file:src/main/resources/img/heart.png"));
                connector.addPlayerToFavourites(PlayerToDisplay);
            }else{
                System.out.println("OK2!");
                favouriteImg.setImage(new Image("file:src/main/resources/img/nonHeart.png"));
                connector.removePlayerFromFavourites(PlayerToDisplay);
            }
        }catch(Exception ex){
            System.out.println("HELP!!!!!!!!!!!!!!!!!!!!!");
            ex.printStackTrace();
        }
    }


}