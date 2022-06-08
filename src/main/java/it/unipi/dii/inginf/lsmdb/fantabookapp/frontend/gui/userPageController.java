package it.unipi.dii.inginf.lsmdb.fantabookapp.frontend.gui;

import it.unipi.dii.inginf.lsmdb.fantabookapp.frontend.MiddlewareConnector;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.PrivilegeLevel;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Squad;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.User;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.exception.ActionNotCompletedException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class userPageController implements Initializable {
    private static MiddlewareConnector connector = MiddlewareConnector.getInstance();
    private static User userToDisplay;
    private static final ListToShow defaultList = ListToShow.SquadS;
    private static ListToShow listToShow = defaultList;
    private static final int previewImageHeight = 100;

    @FXML private AnchorPane parentPane;

    @FXML private TextField followsYou;
    @FXML private TextField userCompleteName;

    @FXML private Button followButton;
    @FXML private Button deleteButton;
    @FXML private Button upgradeLevelButton;

    @FXML private Button SquadsButton;
    @FXML private Button followingButton;
    @FXML private Button followedButton;

    @FXML private VBox listPane;

    public static void getUserPage(User user) throws IOException {
        try {
            userToDisplay = connector.getUser(user);
        } catch (ActionNotCompletedException e) {
            throw new IOException(e.getMessage());
        }
        App.setRoot("/userpage");
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        displayUserInformation();
        initializeButtons();
        disSquadToShow();
    }

    //---------------------------------------------------------------------------------------------------------

    private void displayUserInformation() {
        if(userToDisplay.getUsername().equals(connector.getLoggedUser().getUsername()))
            parentPane.getChildren().remove(followsYou);
        else {
            if(connector.isFollowedBy(userToDisplay)) {
                followsYou.setVisible(true);
            }
        }

        userCompleteName.setText(userToDisplay.getFirstName() + " " + userToDisplay.getLastName());
    }

    private void initializeButtons() {
        if(userToDisplay.getUsername().equals(connector.getLoggedUser().getUsername()))
            parentPane.getChildren().removeAll(followButton, upgradeLevelButton);
        else {
            if(userToDisplay.getPrivilegeLevel() == PrivilegeLevel.ADMIN)
                parentPane.getChildren().removeAll(deleteButton, upgradeLevelButton);

            if ( connector.getLoggedUser().getPrivilegeLevel() == null
                    || connector.getLoggedUser().getPrivilegeLevel() != PrivilegeLevel.ADMIN
            ) {
                parentPane.getChildren().removeAll(deleteButton, upgradeLevelButton);
            }

            if(connector.follows(userToDisplay)) {
                followButton.setText("Unfollow");
                followButton.setStyle("-fx-background-color: red; -fx-font-weight: bold");
            } else {
                followButton.setText("Follow");
                followButton.setStyle("-fx-background-color: green; -fx-font-weight: bold");
            }
        }

        followButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    if(connector.follows(userToDisplay)) {
                        connector.unfollow(userToDisplay);
                        followButton.setText("Follow");
                        followButton.setStyle("-fx-background-color: green; -fx-font-weight: bold");
                    } else {
                        connector.follow(userToDisplay);
                        followButton.setText("Unfollow");
                        followButton.setStyle("-fx-background-color: red; -fx-font-weight: bold");
                    }
                } catch (ActionNotCompletedException e) {
                    e.printStackTrace();
                }
            }
        });

        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    connector.deleteUser(userToDisplay);
                    if(userToDisplay.getUsername().equals(connector.getLoggedUser().getUsername())) {
                        connector.logout();
                        App.setRoot("/welcome");
                    } else {
                        App.setRoot("/homepage");
                    }
                } catch (ActionNotCompletedException | IOException e) {
                    e.printStackTrace();
                }
            }
        });

        upgradeLevelButton.setOnAction(actionEvent -> {
            connector.updatePrivilegeLevel(userToDisplay, PrivilegeLevel.ADMIN);
            try {
                userPageController.getUserPage(userToDisplay);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        SquadsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ListToShow oldList = listToShow;
                listToShow = ListToShow.SquadS;
                try {
                    getUserPage(userToDisplay);
                } catch (IOException e) {
                    listToShow = oldList;
                    e.printStackTrace();
                }
            }
        });

        followingButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ListToShow oldList = listToShow;
                listToShow = ListToShow.FOLLOWING;
                try {
                    getUserPage(userToDisplay);
                } catch (IOException e) {
                    listToShow = oldList;
                    e.printStackTrace();
                }
            }
        });

        followedButton.setOnAction(actionEvent -> {
            ListToShow oldList = listToShow;
            listToShow = ListToShow.FOLLOWED;
            try {
                getUserPage(userToDisplay);
            } catch (IOException e) {
                listToShow = oldList;
                e.printStackTrace();
            }
        });
    }

    private void disSquadToShow() {
        switch (listToShow) {
            case SquadS:
                SquadsButton.setTextFill(Color.WHITE);
                displaySquadList();
                break;
            case FOLLOWING:
                followingButton.setTextFill(Color.WHITE);
                displayFollowingList();
                break;
            case FOLLOWED:
                followedButton.setTextFill(Color.WHITE);
                displayFollowedList();
        }

        listToShow = defaultList;
    }

    private void displaySquadList() {
        List<Squad> SquadList = connector.getUserSquads(userToDisplay);
        if(SquadList.size() == 0)
            displayEmpty(listPane);
        else {
            for(Squad Squad: SquadList) {
                listPane.getChildren().add(createSquadPreview(Squad));
            }
        }
    }

    private void displayFollowingList() {
        List<User> followingUsers = connector.getFollowers(userToDisplay);
        if(followingUsers.size() == 0)
            displayEmpty(listPane);
        else {
            for(User user: followingUsers) {
                listPane.getChildren().add(createUserPreview(user));
            }
        }
    }

    private void displayFollowedList() {
        List<User> followedUsers = connector.getFollowedUsers(userToDisplay);
        if(followedUsers.size() == 0)
            displayEmpty(listPane);
        else {
            for(User user: followedUsers) {
                listPane.getChildren().add(createUserPreview(user));
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------

    private void displayEmpty(Pane pane) {
        pane.getChildren().clear();

        Text emptyText = new Text("<EMPTY>");
        emptyText.setStyle("-fx-font-size: 28");
        emptyText.getStyleClass().add("text-id");

        pane.getChildren().add(emptyText);
    }

    private AnchorPane createSquadPreview(Squad Squad) {
        AnchorPane SquadPreview = new AnchorPane();
        Separator horizontalSeparator = getHorizontalSeparator();
        Button SquadInformations = new Button(); SquadInformations.setStyle("-fx-background-color: transparent");
        SquadInformations.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    SquadPageController.getSquadPage(Squad);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Image SquadImage;
        HBox SquadGraphic = new HBox(30);
        try {
            if(Squad.getUrlImage() == null || Squad.getUrlImage().equals(""))
                throw new Exception();

            SquadImage = new Image(
                    Squad.getUrlImage(),
                    0,
                    previewImageHeight,
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
                    0,
                    previewImageHeight,
                    true,
                    true,
                    true
            );
        }

        ImageView SquadImageView = new ImageView(SquadImage);
        Text name = new Text(Squad.getName()); name.setFill(Color.WHITE); name.setStyle("-fx-font-weight: bold; -fx-font-size: 18px");
        SquadGraphic.getChildren().addAll(SquadImageView, name);
        SquadInformations.setGraphic(SquadGraphic);
        SquadPreview.getChildren().addAll(horizontalSeparator, SquadInformations);

        // if the user showed is not the logged user, then add buttons to follow/unfollow Squads
        if ( !connector.getLoggedUser().getUsername().equals(userToDisplay.getUsername())) {
            Button followButton = new Button();
            AnchorPane.setTopAnchor(followButton, 53.0); AnchorPane.setRightAnchor(followButton, 100.0);

            if ( connector.isFollowingSquad(Squad)) {
                followButton.setText("Unfollow");
                followButton.setStyle("-fx-background-color: red; -fx-font-weight: bold; -fx-text-fill: white");
            } else {
                followButton.setText("Follow");
                followButton.setStyle("-fx-background-color: green; -fx-font-weight: bold; -fx-text-fill: white");
            }

            followButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        if ( connector.isFollowingSquad(Squad)) {
                            connector.unfollowSquad(Squad);
                            followButton.setText("Follow");
                            followButton.setStyle("-fx-background-color: green; -fx-font-weight: bold; -fx-text-fill: white");
                        } else {
                            connector.followSquad(Squad);
                            followButton.setText("Unfollow");
                            followButton.setStyle("-fx-background-color: red; -fx-font-weight: bold; -fx-text-fill: white");
                        }
                    } catch (ActionNotCompletedException e) {
                        e.printStackTrace();
                    }
                }
            });

            SquadPreview.getChildren().add(followButton);
        }

        return SquadPreview;
    }

    private AnchorPane createUserPreview(User user) {
        AnchorPane userPreview = new AnchorPane();
        Separator horizontalSeparator = getHorizontalSeparator();
        Button userInformations = new Button(); userInformations.setStyle("-fx-background-color: transparent");
        userInformations.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    userPageController.getUserPage(user);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        HBox userGraphic = new HBox(30);
        Image userImage = new Image(
                "file:src/main/resources/it/unipi/dii/inginf/lsmdb/unimusic/frontend/gui/img/user.png",
                0,
                previewImageHeight,
                true,
                true,
                true
        );
        ImageView userImageView = new ImageView(userImage);

        VBox nameBox = new VBox(10);
        Text username = new Text(user.getUsername());
        username.setFill(Color.WHITE); username.setStyle("-fx-font-weight: bold; -fx-font-size: 18px");
        Text followersCount = new Text("(" +String.valueOf(connector.getFollowers(user).size())+ ") FOLLOWERS");
        followersCount.setFill(Color.GRAY); username.setStyle("-fx-font-weight: bold; -fx-font-size: 16px");
        nameBox.getChildren().addAll(username, followersCount);

        userGraphic.getChildren().addAll(userImageView, nameBox);
        userInformations.setGraphic(userGraphic);
        userPreview.getChildren().addAll(horizontalSeparator, userInformations);

        // add buttons to follow/unfollow users
        Button followButton = new Button();
        AnchorPane.setTopAnchor(followButton, 53.0); AnchorPane.setRightAnchor(followButton, 100.0);

        if ( connector.follows(user)) {
            followButton.setText("Unfollow");
            followButton.setStyle("-fx-background-color: red; -fx-font-weight: bold; -fx-text-fill: white");
        } else {
            followButton.setText("Follow");
            followButton.setStyle("-fx-background-color: green; -fx-font-weight: bold; -fx-text-fill: white");
        }

        followButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    if ( connector.follows(user)) {
                        connector.unfollow(user);
                        followButton.setText("Follow");
                        followButton.setStyle("-fx-background-color: green; -fx-font-weight: bold; -fx-text-fill: white");
                    } else {
                        connector.follow(user);
                        followButton.setText("Unfollow");
                        followButton.setStyle("-fx-background-color: red; -fx-font-weight: bold; -fx-text-fill: white");
                    }
                } catch (ActionNotCompletedException e) {
                    e.printStackTrace();
                }
            }
        });

        userPreview.getChildren().add(followButton);

        return userPreview;
    }

    private Separator getHorizontalSeparator() {
        Separator horizontalSeparator = new Separator();
        AnchorPane.setTopAnchor(horizontalSeparator, 0.0);
        AnchorPane.setLeftAnchor(horizontalSeparator, 0.0);
        AnchorPane.setRightAnchor(horizontalSeparator, 0.0);
        return horizontalSeparator;
    }

    private enum ListToShow {
        SquadS,
        FOLLOWING,
        FOLLOWED
    }
}
