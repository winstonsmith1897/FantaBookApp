package it.unipi.dii.inginf.lsmdb.fantabookapp.frontend.gui;

import it.unipi.dii.inginf.lsmdb.fantabookapp.frontend.MiddlewareConnector;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Player;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

public class statisticsPageController implements Initializable {
    private static final MiddlewareConnector connector = MiddlewareConnector.getInstance();
    private static final StatisticToShow defaultStatistic = StatisticToShow.POPULAR_PLAYERS;
    private static StatisticToShow statisticToShow = defaultStatistic;
    private static final int previewImageHeight = 150;

    @FXML private Button popularPlayersButton;
    @FXML private Button topPlayerForPositionButton;
    @FXML private Button topFavouriteTeamsButton;
    @FXML private Button topPlayersPerAgeRange;

    @FXML private TextField totalPlayersLabel;
    @FXML private TextField totalUsersLabel;
    @FXML private TextField totalSquadsLabel;

    @FXML private VBox statisticPane;

    private HBox firstFilter;
    private Button commitQuery;
    private VBox loadPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeButtons();
        displayStatisticToShow();
    }

    //--------------------------------------------------------------------------------------------------------

    private void initializeButtons() {
        popularPlayersButton.setOnAction(actionEvent -> {
            StatisticToShow oldStatistic = statisticToShow;
            statisticToShow = StatisticToShow.POPULAR_PLAYERS;
            try {
                App.setRoot("/statistics");
            } catch (IOException e) {
                statisticToShow = oldStatistic;
                e.printStackTrace();
            }
        });

        topPlayerForPositionButton.setOnAction(actionEvent -> {
            StatisticToShow oldStatistic = statisticToShow;
            statisticToShow = StatisticToShow.TOP_PLAYER_FOR_POSITION;
            try {
                App.setRoot("/statistics");
            } catch (IOException e) {
                statisticToShow = oldStatistic;
                e.printStackTrace();
            }
        });

        topFavouriteTeamsButton.setOnAction(actionEvent -> {
            StatisticToShow oldStatistic = statisticToShow;
            statisticToShow = StatisticToShow.TOP_FAVOURITE_TEAMS;
            try {
                App.setRoot("/statistics");
            } catch (IOException e) {
                statisticToShow = oldStatistic;
                e.printStackTrace();
            }
        });

        topPlayersPerAgeRange.setOnAction(actionEvent -> {
            StatisticToShow oldStatistic = statisticToShow;
            statisticToShow = StatisticToShow.TOP_PLAYER_FOR_AGE_RANGE;
            try {
                App.setRoot("/statistics");
            } catch (IOException e) {
                statisticToShow = oldStatistic;
                e.printStackTrace();
            }
        });

    }

    private void displayStatisticToShow() {
        switch (statisticToShow) {
            case POPULAR_PLAYERS:
                popularPlayersButton.setTextFill(Color.WHITE);
                displayPopularPlayers();
                break;
            case TOP_PLAYER_FOR_POSITION:
                topPlayerForPositionButton.setTextFill(Color.WHITE);
                displayTopPlayerForPosition();
                break;
            case TOP_FAVOURITE_TEAMS:
                topFavouriteTeamsButton.setTextFill(Color.WHITE);
                displayTopFavouriteTeams();
                break;
            case TOP_PLAYER_FOR_AGE_RANGE:
                topPlayersPerAgeRange.setTextFill(Color.WHITE);
                displayTopPlayerForNation();
        }

        displayNumberStatistics();

        statisticToShow = defaultStatistic;
    }


    //--------------------------------------------------------------------------------------------------------


    private void displayNumberStatistics() {
        totalPlayersLabel.setText("Total Players: " + ((connector.getTotalPlayers() == -1)?"Error counting": connector.getTotalPlayers()));
        totalUsersLabel.setText("Total users: " + ((connector.getTotalPlayers() == -1)?"Error counting": connector.getTotalUsers()));
        totalSquadsLabel.setText("Total Squads: " + ((connector.getTotalPlayers() == -1)?"Error counting": connector.getTotalSquads()));
    }


    private void displayPopularPlayers() {
        Text playerThreshold = new Text("Threshold: "); playerThreshold.setFill(Color.WHITE); playerThreshold.setStyle("-fx-font-weight: bold; -fx-font-size: 20px");
        TextField playerThresholdInput = new TextField("5"); playerThresholdInput.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 18px; -fx-max-width: 100px");
        playerThresholdInput.setOnKeyTyped(keyEvent -> {
            String text = playerThresholdInput.getText();
            if(!text.matches("[0-9]+")) {
                playerThresholdInput.setText(text.replaceAll("[^\\d]", ""));
            }
        });
        firstFilter = new HBox(5, playerThreshold, playerThresholdInput);

        Text limit = new Text("How many Players: "); limit.setFill(Color.WHITE); limit.setStyle("-fx-font-weight: bold; -fx-font-size: 20px");
        TextField limitInput = new TextField("10"); limitInput.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 18px; -fx-max-width: 50px");
        limitInput.setOnKeyTyped(keyEvent -> {
            String text = limitInput.getText();
            if(!text.matches("[0-9]{1,2}?")) {
                limitInput.setText(text.replaceAll("[^\\d]", ""));
                text = limitInput.getText();
                int substring = Math.min(text.length(), 2);
                limitInput.setText(text.substring(0, substring));
            }
        });
        HBox secondFilter = new HBox(5, limit, limitInput);

        loadPane = new VBox(10);
        commitQuery = new Button("GET RESULTS");
        commitQuery.setStyle("-fx-background-color: green; -fx-font-weight: bold; -fx-text-fill: white");
        commitQuery.setOnAction(actionEvent -> loadTopPlayers(Integer.parseInt(playerThresholdInput.getText()), Integer.parseInt(limitInput.getText())));

        HBox inputContainer = new HBox(30, commitQuery, firstFilter, secondFilter);
        statisticPane.getChildren().addAll(inputContainer, loadPane);

        loadTopPlayers(Integer.parseInt(playerThresholdInput.getText()), Integer.parseInt(limitInput.getText()));
    }

    private void displayTopPlayerForPosition() {
        loadPane = new VBox(10);
        statisticPane.getChildren().addAll(loadPane);
        loadTopPlayerForPosition();
    }

    private void displayTopFavouriteTeams() {
        Text limit = new Text("How many Teams: "); limit.setFill(Color.WHITE); limit.setStyle("-fx-font-weight: bold; -fx-font-size: 20px");
        TextField limitInput = new TextField("10"); limitInput.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 18px; -fx-max-width: 50px");
        limitInput.setOnKeyTyped(keyEvent -> {
            String text = limitInput.getText();
            if(!text.matches("[0-9]{1,2}?")) {
                limitInput.setText(text.replaceAll("[^\\d]", ""));
                text = limitInput.getText();
                int substring = Math.min(text.length(), 2);
                limitInput.setText(text.substring(0, substring));
            }
        });
        firstFilter = new HBox(5, limit, limitInput);

        loadPane = new VBox(10);
        commitQuery = new Button("GET RESULTS");
        commitQuery.setStyle("-fx-background-color: green; -fx-font-weight: bold; -fx-text-fill: white");
        commitQuery.setOnAction(actionEvent -> loadTopFavouriteTeams(Integer.parseInt(limitInput.getText())));

        HBox inputContainer = new HBox(30, commitQuery, firstFilter);
        statisticPane.getChildren().addAll(inputContainer, loadPane);

        loadTopFavouriteTeams(Integer.parseInt(limitInput.getText()));
    }

    private void displayTopPlayerForNation() {
        loadPane = new VBox(10);
        statisticPane.getChildren().addAll(loadPane);
        loadTopPlayerForNation();
    }

    //--------------------------------------------------------------------------------------------------------

    private void loadTopPlayers(int playerThreshold, int limit) {
        loadPane.getChildren().clear();
        List<Pair<String, Integer>> topPlayers = connector.getTopPlayers(playerThreshold, limit);
        if(topPlayers.size() == 0)
            displayEmpty(loadPane);
        else {
            int order = 1;
            for(Pair<String, Integer> PlayerStatistic: topPlayers) {
                loadPane.getChildren().add(createTopPlayerView(order++, PlayerStatistic));
            }
        }
    }

    private void loadTopPlayerForPosition() {
        loadPane.getChildren().clear();
        List<Pair<String, Pair<Player, Integer>>> PlayerForPosition = connector.getTopPlayerForPosition();
        if(PlayerForPosition.size() == 0)
            displayEmpty(loadPane);
        else {
            for (Pair<String, Pair<Player, Integer>> PlayerInfo: PlayerForPosition) {
                loadPane.getChildren().add(createTopPlayerView(
                        PlayerInfo.getKey(),
                        PlayerInfo.getValue().getKey(),
                        PlayerInfo.getValue().getValue()
                ));
            }
        }
    }

    private void loadTopFavouriteTeams(int limit) {
        loadPane.getChildren().clear();
        List<Pair<String, Integer>> favouriteTeams = connector.getTopFavouriteTeams(limit);
        if(favouriteTeams.size() == 0)
            displayEmpty(loadPane);
        else {
            int order = 1;
            for(Pair<String, Integer> team: favouriteTeams) {
                loadPane.getChildren().add(createTopTeamsView(order++, team));
            }
        }
    }

    private void loadTopPlayerForNation() {
        loadPane.getChildren().clear();
        List<Pair<Double, Pair<String, String>>> PlayersForAgeRange = connector.getFavouritePlayerPerNation();
        if(PlayersForAgeRange.size() == 0)
            displayEmpty(loadPane);
        else {
            for (Pair<Double, Pair<String, String>> PlayerInfo: PlayersForAgeRange) {
                loadPane.getChildren().add(createTopPlayerForNation(
                        PlayerInfo.getKey(),
                        PlayerInfo.getValue().getKey(),
                        PlayerInfo.getValue().getValue()
                ));
            }
        }
    }

    private Text createTopPlayerView(int order, Pair<String, Integer> PlayerStatistic) {
        Text PlayerNode = new Text(
                order+ "]  "
                        +PlayerStatistic.getKey()+ "  "
                        + "(" +PlayerStatistic.getValue()+ " GOALS)"
        );
        PlayerNode.setFill(Color.WHITE); PlayerNode.setStyle("-fx-font-weight: bold; -fx-font-size: 20px");
        return PlayerNode;
    }

    private HBox createTopPlayerView(String Position, Player Player, Integer avgRating) {
        Image PlayerImage;
        try {
            if(Player.getImageUrl() == null || Player.getImageUrl().equals(""))
                throw new Exception();

            PlayerImage = new Image(
                    Player.getImageUrl(),
                    0,
                    previewImageHeight,
                    true,
                    true,
                    true
            );

            if(PlayerImage.isError()) {
                throw new Exception();
            }

        }catch(Exception ex){
            PlayerImage = new Image(
                    "file:src/main/resources/img/empty.jpg",
                    0,
                    previewImageHeight,
                    true,
                    true,
                    true
            );
        }

        ImageView PlayerImageView = new ImageView(PlayerImage);

        Text PositionText = new Text(Position+ "s"); PositionText.setFill(Color.WHITE); PositionText.setStyle("-fx-font-weight: bold; -fx-font-size: 24px");
        Text name = new Text(Player.getName()); name.setFill(Color.WHITE); name.setStyle("-fx-font-weight: bold; -fx-font-size: 20px");
        Text rating = new Text("Average Rating: " +new DecimalFormat("0.00").format(avgRating)+ "%"); rating.setFill(Color.GRAY); name.setStyle("-fx-font-weight: bold; -fx-font-size: 18px");

        VBox PlayerInfo = new VBox(5, PositionText, name, rating);
        return new HBox(10, PlayerImageView, PlayerInfo);
    }

    private Text createTopTeamsView(int order, Pair<String, Integer> team) {
        Text teamNode = new Text(
                order+ "]  " +team.getKey()+ " "
                        + "(Contained " +team.getValue()+ " times in Squads)"
        );
        teamNode.setFill(Color.WHITE); teamNode.setStyle("-fx-font-weight: bold; -fx-font-size: 20px");
        return teamNode;
    }

    private HBox createTopPlayerForNation(Double Position, String Player, String popularity) {
        Text PlayerNode = new Text(Position + " - " +(Position + 9)+ "]  " + Player + "  ");
        PlayerNode.setFill(Color.WHITE); PlayerNode.setStyle("-fx-font-weight: bold; -fx-font-size: 20px");

        String[]presence = popularity.split(":");
        Text popularityNode = new Text("(Present " + presence[0] + " times on total " + presence[1] + ")");
        popularityNode.setFill(Color.GRAY); popularityNode.setStyle("-fx-font-weight: bold; -fx-font-size: 18px");
        return new HBox(0, PlayerNode, popularityNode);
    }

    private void displayEmpty(Pane pane) {
        pane.getChildren().clear();

        Text emptyText = new Text("<EMPTY>");
        emptyText.setStyle("-fx-font-size: 28");
        emptyText.getStyleClass().add("text-id");

        pane.getChildren().add(emptyText);
    }

    private enum StatisticToShow {
        POPULAR_PLAYERS,
        TOP_PLAYER_FOR_POSITION,
        TOP_FAVOURITE_TEAMS,
        TOP_PLAYER_FOR_AGE_RANGE
    }
}