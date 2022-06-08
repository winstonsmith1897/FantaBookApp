package it.unipi.dii.inginf.lsmdb.fantabookapp.frontend.gui;


import it.unipi.dii.inginf.lsmdb.fantabookapp.frontend.MiddlewareConnector;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Player;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.User;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.exception.ActionNotCompletedException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class searchBarController implements Initializable {
    private static final MiddlewareConnector connector = MiddlewareConnector.getInstance();

    private static CheckBoxEnum checkBoxSelected;

    @FXML private Button searchButton;
    @FXML private Button closeSearchButton;
    @FXML private TextField searchInput;

    @FXML private CheckBox nameCheck;
    @FXML private CheckBox teamCheck;
    @FXML private CheckBox positionCheck;


    @FXML private CheckBox userCheck;

    @FXML private AnchorPane anchorSearch;

    @FXML private VBox resultBox;

    public searchBarController() {
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeSearch();
        initializeCheckBox();
    }


    /**
     * initialize actions associated to the search and the close button.
     */
    private void initializeSearch() {

        searchInput.addEventHandler(KeyEvent.KEY_RELEASED, e -> handleSearchInput());

        searchButton.setOnAction(actionEvent -> handleSearchInput());

        closeSearchButton.setOnAction(actionEvent -> {
            resultBox.getChildren().clear();
            anchorSearch.setVisible(false);
        });

    }

    public void handleSearchInput(){
        String partialInput = searchInput.getText();
        if(partialInput.equals("")){
            displayNoInputInserted();
            return;
        }
        String attributeField = checkBoxSelected.toString();
        if(attributeField == null)
            return;
        try {
            if (!attributeField.equals("Username")) {
                List<Player> PlayersFiltered = connector.filterPlayer(partialInput, attributeField);
                displayPlayer(PlayersFiltered);
            } else{
                List<User> userFiltered = connector.getUsersByPartialInput(partialInput);
                displayUser(userFiltered);
            }
        } catch (ActionNotCompletedException ex) {
            System.out.println("ERROR");
        }
    }


    /**
     * Initialize all the actions associated to the checkbox.
     */
    private void initializeCheckBox() {

        if(checkBoxSelected == null)
            checkBoxSelected = CheckBoxEnum.name;

        switch (checkBoxSelected) {
            case Username:
                userCheck.setSelected(true);
                break;
            case name:
                nameCheck.setSelected(true);break;
            case position:
                positionCheck.setSelected(true);
                break;
            default:
                teamCheck.setSelected(true);
        }

        nameCheck.setOnAction(actionEvent -> handleChechBox(CheckBoxEnum.name, actionEvent));

        teamCheck.setOnAction(actionEvent -> handleChechBox(CheckBoxEnum.team, actionEvent));

        positionCheck.setOnAction(actionEvent -> handleChechBox(CheckBoxEnum.position, actionEvent));


        userCheck.setOnAction(actionEvent -> handleChechBox(CheckBoxEnum.Username, actionEvent));

    }

    private void handleChechBox(CheckBoxEnum checkBoxSel, ActionEvent actionEvent) {
        checkBoxSelected = checkBoxSel;
        nameCheck.setSelected(false);
        teamCheck.setSelected(false);
        positionCheck.setSelected(false);
        userCheck.setSelected(false);

        CheckBox checkBox = (CheckBox) actionEvent.getSource();
        checkBox.setSelected(true);
    }


    /**
     * Display the list of Players.
     * @param PlayersFiltered Is the list of Players that will be shown.
     */
    private void displayPlayer(List<Player> PlayersFiltered) {

        resultBox.getChildren().clear();

        if(PlayersFiltered.size() != 0) {
            for (Player player : PlayersFiltered)
                resultBox.getChildren().addAll(createPlayerPreview(player), new Separator());
            if(PlayersFiltered.size() < 4)
                displayNoMoreResult(PlayersFiltered.size());
        }else
            displayEmpty();

        anchorSearch.setVisible(true);
    }


    /**
     * Display the list of users.
     * @param userFiltered Is the list of users that will be shown.
     */
    private void displayUser(List<User> userFiltered) {

        resultBox.getChildren().clear();

        if(userFiltered.size() != 0) {
            for (User user : userFiltered)
                resultBox.getChildren().addAll(createUserPreview(user), new Separator());
            if(userFiltered.size() < 4)
                displayNoMoreResult(userFiltered.size());
        }else
            displayEmpty();

        anchorSearch.setVisible(true);
    }


    /**
     * Display the message of empty result when no user/Player is found according to the user's input.
     */
    public void displayEmpty(){
        TextField empty = new TextField("No result found"); empty.setStyle("-fx-background-color: black;");empty.setMinWidth(500);empty.setPrefHeight(500); empty.setAlignment(Pos.CENTER);
        resultBox.getChildren().add(empty);

    }


    /**
     * Display a generic text in the result box.
     * @param text The text to be displayed.
     */
    public void displayText(String text){
        resultBox.getChildren().clear();
        TextField fill = new TextField(text); fill.setStyle("-fx-background-color: black;"); fill.setMinHeight(500);fill.setMinWidth(500); fill.setAlignment(Pos.CENTER);
        resultBox.getChildren().add(fill);
        anchorSearch.setVisible(true);
    }


    public void displayNoInputInserted(){
        displayText("Insert something...");
    }


    public void displayNoMoreResult(int size){
        TextField fill = new TextField("No more result found"); fill.setStyle("-fx-background-color: black;"); fill.setMinHeight((4 - size) * 150);fill.setMinWidth(500); fill.setAlignment(Pos.CENTER);
        resultBox.getChildren().add(fill);
    }


    /**
     * @param Player The Player to be shown.
     * @return The section that contains all the preview's information as a button.
     */
    private Button createPlayerPreview(Player Player) {

        Button PlayerPreview = new Button();
        PlayerPreview.setStyle("-fx-background-color: black"); PlayerPreview.setMinWidth(500);
        PlayerPreview.setOnAction(actionEvent -> {
            try {
                System.out.println("Player Preview OK");
                PlayerPageController.getPlayerPage(Player);
            } catch (IOException e) {
                System.out.println("Player Preview Failed");
            }
        });

        Image PlayerImage;
        try {
            PlayerImage = new Image(Player.getImageUrl(),0,100,true,true,true);

            if(PlayerImage.isError()) {
                throw new Exception();
            }

        }catch(Exception ex){
            String filePath = "file:src/main/resources/img/empty.jpg";
            PlayerImage = new Image(filePath,100,140,true,true,true);
        }

        ImageView PlayerImageView = new ImageView(PlayerImage);

        Text name = new Text(Player.getName()); name.setWrappingWidth(300); name.setFill(Color.WHITE);
        System.out.println("name");
        System.out.println(name);
        Text team = new Text(Player.getTeam()); team.setWrappingWidth(300);team.setFill(Color.GRAY);
        System.out.println("team");
        System.out.println(team);

        VBox information = new VBox(name, team);information.setPadding(new Insets(20, 0, 20, 20));

        HBox PlayerGraphic = new HBox(2, PlayerImageView, information);

        PlayerPreview.setGraphic(PlayerGraphic);
        return PlayerPreview;
    }


    /**
     * @param user The user to be shown.
     * @return The section that contains all the preview's information as a button.
     */
    public Button createUserPreview(User user) {

        Button userPreview = new Button(); userPreview.setStyle("-fx-background-color: black"); userPreview.setMinWidth(500);

        userPreview.setOnAction(actionEvent -> {

            try {
                userPageController.getUserPage(user);
                throw new IOException();
            } catch (IOException e) {
                System.out.println("User Preview Failed");
            }
        });

        String filePath = "file:src/main/resources/img/user.png";
        Image userImage = new Image(filePath,140,0,true,true,true);
        ImageView PlayerImageView = new ImageView(userImage);

        Text username = new Text(user.getUsername()); username.setFill(Color.WHITE);

        HBox userGraphic = new HBox(20, PlayerImageView, username);
        userPreview.setGraphic(userGraphic);

        return userPreview;
    }


    private enum CheckBoxEnum{
        name,
        team,
        position,
        Username
    }


}
