package it.unipi.dii.inginf.lsmdb.fantabookapp.frontend;


import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.dao.*;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.*;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.exception.ActionNotCompletedException;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.persistence.mongoconnection.MongoDriver;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.persistence.neo4jconnection.Neo4jDriver;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MiddlewareConnector {
    private static final MiddlewareConnector instance = new MiddlewareConnector();

    private final UserDAO userDAO = new UserDAOImpl();
    private final SquadDAO SquadDAO = new SquadDAOImpl();
    private final PlayerDAO PlayerDAO = new PlayerDAOImpl();

    private User loggedUser;
    private MiddlewareConnector() {

    }

    public static MiddlewareConnector getInstance() { return instance; }

    public void closeApplication() {
        MongoDriver.getInstance().closeConnection();
        Neo4jDriver.getInstance().closeDriver();
    }

    public User getLoggedUser() { return loggedUser; }

    //-----------------USER-------------------------------------------------------------------

    public void registerUser(String username,
                             String password,
                             String firstName,
                             String lastName,
                             int age,
                             String country) throws ActionNotCompletedException {
        User user = new User(username, password, firstName, lastName, age, country);
        userDAO.createUser(user);
    }

    public boolean loginUser(String username, String password) {
        if(userDAO.checkUserPassword(username, password)) {
            try {
                loggedUser = userDAO.getUserByUsername(username);
            } catch (ActionNotCompletedException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    public User getUser(User user) throws ActionNotCompletedException {return userDAO.getUserByUsername(user.getUsername()); }


    public List<User> getUsersByPartialInput(String partialUsername) throws ActionNotCompletedException {return userDAO.getUserByPartialUsername(partialUsername);}


    public void logout() {
        loggedUser = null;
    }


    public void deleteUser(User user) throws ActionNotCompletedException{userDAO.deleteUser(user);}

    public void updatePrivilegeLevel(User user, PrivilegeLevel newPrivLevel) {
        try {
            userDAO.updateUserPrivilegeLevel(user, newPrivLevel);
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
        }
    }

    public List<User> getSuggestedUsers() {
        List<User> suggUsers;
        try {
            suggUsers = userDAO.getSuggestedUsers(loggedUser);
        } catch (ActionNotCompletedException ancEx) {
            ancEx.printStackTrace();
            return new ArrayList<>();
        }
        return suggUsers;
    }


    public List<User> getFollowedUsers(User user) {
        List<User> followedUsers;
        try {
            followedUsers = userDAO.getFollowedUsers(user);
        } catch (ActionNotCompletedException ancEx) {
            ancEx.printStackTrace();
            return new ArrayList<>();
        }
        return followedUsers;
    }


    public List<User> getFollowers(User user) {
        List<User> followingUsers;
        try {
            followingUsers = userDAO.getFollowers(user);
        } catch (ActionNotCompletedException ancEx) {
            ancEx.printStackTrace();
            return new ArrayList<>();
        }
        return followingUsers;
    }


    public boolean follows(User followed) {
        return userDAO.isFollowedBy(followed, loggedUser);
    }


    public boolean isFollowedBy(User following) {
        return userDAO.isFollowedBy(loggedUser, following);
    }


    public void follow(User userToBeFollowed) throws ActionNotCompletedException {
        userDAO.followUser(loggedUser, userToBeFollowed);
    }

    public void unfollow(User userToBeUnfollowed) throws ActionNotCompletedException {userDAO.unfollowUser(loggedUser, userToBeUnfollowed);}

    public List<Pair<String, Integer>> getTopFavouriteNation(int limit) {
        List<Pair<String, Integer>> favouriteTeam;
        try {
            favouriteTeam = userDAO.getFavouriteNation(limit);
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return favouriteTeam;
    }

    public List<Pair<Double, Pair<String, String>>> getFavouritePlayerPerNation() {
        List<Pair<Double, Pair<String, String>>> topPlayers;
        try {
            topPlayers = userDAO.getFavouritePlayerPerNation();
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return topPlayers;
    }

    public int getTotalUsers() { return userDAO.getTotalUsers();}

    //--------------------------Player-------------------------------------------------------------------

    public List<Player> getBestPlayers() {
        List<Player> hotPlayers;
        try {
            hotPlayers = PlayerDAO.getBestPlayers(40);
        } catch (ActionNotCompletedException ancEx) {
            ancEx.printStackTrace();
            return new ArrayList<>();
        }
        return hotPlayers;
    }

    public boolean isLikedPlayer(Player Player){return userDAO.userLikesPlayer(loggedUser, Player);}


    public boolean isFavouritePlayer(Player Player){
        return SquadDAO.isPlayerFavourite(loggedUser, Player);
    }


    public void addPlayerToFavourites(Player Player) throws ActionNotCompletedException {SquadDAO.addPlayerToFavourite(loggedUser, Player);}


    public void removePlayerFromFavourites(Player Player) throws ActionNotCompletedException {SquadDAO.deletePlayerFromFavourite(loggedUser, Player);}


    public Player getPlayerByTeam(Player Player){
        return PlayerDAO.getPlayerByTeam(Player.getTeam());
    }

    public Player getPlayerByName(Player Player){
        return PlayerDAO.getPlayerByName(Player.getName());
    }

    public void likePlayer(Player Player) throws ActionNotCompletedException {userDAO.likePlayer(loggedUser, Player);}


    public void deleteLike(Player Player) throws ActionNotCompletedException {userDAO.deleteLike(loggedUser, Player);}


    public List<Player> filterPlayer(String partialInput, String attributeField) throws ActionNotCompletedException {

        if(attributeField.equals("team"))
            return PlayerDAO.getPlayerByPartialTeam(partialInput);
        if(attributeField.equals("name")) {
            return PlayerDAO.getPlayerByPartialName(partialInput);
        }
        if(attributeField.equals("position")) {
            System.out.println("--------------------------------------position-------------------------");
            return PlayerDAO.getPlayerByPartialPosition(partialInput);
        }
        return null;
    }

    public List<Pair<String, Integer>> getTopPlayers(int goalThreshold, int limit) {
        List<Pair<String, Integer>> topPlayers;
        try {
            topPlayers = PlayerDAO.findPlayersWithMostNumberOfGoal(goalThreshold, limit);
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return topPlayers;
    }
/*
    public List<Pair<Integer, Pair<Album, Double>>> getTopAlbumForDecade() {
        List<Pair<Integer, Pair<Album, Double>>> topAlbums;
        try {
            topAlbums = PlayerDAO.findTopRatedAlbumPerDecade();
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return topAlbums;
    }

 */

    public int getTotalPlayers() { return PlayerDAO.getTotalPlayers();}


    //-----------------Squad-------------------------------------------------------------------

    public void addPlayer(Squad Squad, Player Player) throws ActionNotCompletedException { SquadDAO.addPlayer(Squad, Player);}

    public void addPlayerToFavourite(User user, Player Player) throws ActionNotCompletedException {SquadDAO.addPlayerToFavourite(user, Player);}

    public void createSquad(Squad Squad) throws ActionNotCompletedException {SquadDAO.createSquad(Squad);}

    public void deleteSquad(Squad Squad) {
        try {
            SquadDAO.deleteSquad(Squad);
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
        }
    }

    public void deletePlayerFromSquad(Player Player, Squad Squad){
        try {
            SquadDAO.deletePlayer(Squad, Player);
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
        }
    }

    public List<Squad> getSuggestedSquads() {
        List<Squad> suggestedSquads;
        try {
            suggestedSquads = SquadDAO.getSuggestedSquads(loggedUser);
        } catch (ActionNotCompletedException e) {
            return new ArrayList<>();
        }
        return suggestedSquads;
    }

    public Squad getSquadById(String ID) throws ActionNotCompletedException{return SquadDAO.getSquad(ID);}

    public List<Player> getSquadPlayers(Squad Squad){
        List<Player> result = null;

        try {
            result = SquadDAO.getAllPlayers(Squad);
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Squad> getUserSquads(User user) {
        List<Squad> SquadList;
        try {
            SquadList = userDAO.getAllSquad(user);
            SquadList.addAll(userDAO.getFollowedSquad(user));
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return SquadList;
    }

    public boolean isFollowingSquad(Squad Squad) {return userDAO.isFollowingSquad(loggedUser, Squad);}


    public void followSquad(Squad Squad) throws ActionNotCompletedException {userDAO.followSquad(loggedUser, Squad);}


    public void unfollowSquad(Squad Squad) throws ActionNotCompletedException {userDAO.unfollowSquad(loggedUser, Squad);}

    public Squad getFavourite() throws ActionNotCompletedException {
        return SquadDAO.getFavourite(loggedUser);
    }

    public int getTotalSquads() { return SquadDAO.getTotalSquads();}

    public List<Squad> getOwnUserSquads() {
        System.out.println("LOGGED USER");
        System.out.println(loggedUser);
        return getOwnUserSquads(loggedUser);}


    public List<Squad> getOwnUserSquads(User user) {
        List<Squad> SquadList;
        try {
            System.out.println("Hello Marco");
            SquadList = userDAO.getAllSquad(user);
            System.out.println(SquadList);
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return SquadList;
    }

    public void setSquadName(Squad Squad, String newName) {
        try {
            SquadDAO.updateSquadName(Squad, newName);
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
        }
    }

    public List<Pair<String, Integer>> getTopFavouriteTeams(int limit) {
        List<Pair<String, Integer>> favouriteTeam;
        try {
            favouriteTeam = userDAO.getFavouriteTeam(limit);
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return favouriteTeam;    }


    public List<Pair<String, Pair<Player, Integer>>> getTopPlayerForPosition() {
        List<Pair<String, Pair<Player, Integer>>> topPlayers;
        try {
            topPlayers = PlayerDAO.findTopRatedPlayerPerPosition();
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return topPlayers;
    }
}