package it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.dao;


import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Player;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.exception.ActionNotCompletedException;
import javafx.util.Pair;

import java.util.List;


/**
 * Provides CRUD operations for Songs
 */
public interface PlayerDAO {

    /**
     * Add a player in both databases and handle possible errors:
     * 1) if the player isn't added to MongoDb throws ActionNotCompletedException.
     * 2) if the player isn't added to Neo4j delete also the document in MongoDb to avoid inconsitency and then throws  ActionNotCompletedException.
     * In any case errors are logged.
     *
     * @param player the player you want to add to databases.
     * @throws ActionNotCompletedException when a database write fails.
     */
    default void createPlayer(Player player) throws ActionNotCompletedException {

    }

    /**
     * @param playerID the id of the player you want to return.
     * @return the player with the specified id.
     */
    Player getPlayerByTeam(String playerID);
    Player getPlayerByName(String playerID);
    List<Player> getPlayerByPartialName(String partialName, int limit) throws ActionNotCompletedException;
    List<Player> getPlayerByPartialTeam(String partialTeam, int limit) throws ActionNotCompletedException;
    List<Player> getPlayerByPartialPosition(String partialPosition, int limit) throws ActionNotCompletedException;


        /**
         * @param partialInput the partial input of the user.
         * @param limit the max number of song you want to return.
         * @param attributeField the document's attribute you want to match.
         * @return players where the specified attribute fields contains the partial input of the user (case insensitive).
         * @throws ActionNotCompletedException when a database error occurs.
         */
    List<Player> filterPlayer(String partialInput, int limit, String attributeField) throws ActionNotCompletedException;

    /*List<Song> getSongsByPartialAlbum(String partialAlbum, int limit) throws ActionNotCompletedException;

    List<Player> getSongsByPartialName(String partialName, int limit) throws ActionNotCompletedException;

    List<Player> getSongsByPartialName(String partialName) throws ActionNotCompletedException;

    List<Player> getSongsByPartialArtist(String partialArtist, int limit) throws ActionNotCompletedException;

    List<Player> getSongsByPartialArtist(String partialArtist) throws ActionNotCompletedException;
    */
    /**
     * It's an Analytic function.
     * @return the album with the highest average of rating for every decade.
     * @throws ActionNotCompletedException when a database error occurs.
     */
    List<Pair<String, Pair<Player, Integer>>> findTopRatedPlayerPerPosition() throws ActionNotCompletedException;

    /**
     * It's an Analytic function.
     * @param golLimit the threshold to consider a song as a hit.
     * @param maxNumber the max number of artists you want to return.
     * @return artists which made the highest number of “hit songs”. A song is a “hit” if it received more than hitLimit likes.
     * @throws ActionNotCompletedException when a database error occurs.
     */
    List<Pair<String, Integer>> findPlayersWithMostNumberOfGoal(int golLimit, int maxNumber) throws ActionNotCompletedException;

    /**
     * It's an Analytic function.
     * @return songs that received more likes in the current day.
     * @param limit Maximum number of songs to return.
     * @throws ActionNotCompletedException when a database error occurs.
     */
    List<Player> getBestPlayers(int limit) throws  ActionNotCompletedException;

    /**
     * Update the song Document in MongoDb incrementing the likeCount field.
     * @param player you want to update.
     * @throws ActionNotCompletedException when a database error occurs.
     */
    void incrementLikeCount(Player player) throws ActionNotCompletedException;

    /**
     * Update the song Document in MongoDb decrementing the likeCount field.
     * @param player the song you want to update.
     * @throws ActionNotCompletedException when a database error occurs.
     */
    void decrementLikeCount(Player player) throws ActionNotCompletedException;

    /**
     * @return the number of songs present in the application.
     */
    int getTotalPlayers();

    /**
     * @param player you want to delete.
     */
    void deletePlayer(Player player) throws ActionNotCompletedException, IllegalArgumentException;

    void deletePlayerDocument(Player mongoPlayer);

    List<Player> getPlayerByPartialTeam(String partialInput) throws ActionNotCompletedException;

    List<Player> getPlayerByPartialName(String partialInput) throws ActionNotCompletedException;

    List<Player> getPlayerByPartialPosition(String partialInput) throws ActionNotCompletedException;
}
