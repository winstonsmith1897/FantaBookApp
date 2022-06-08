package it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.dao;

import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Player;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Squad;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.User;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.exception.ActionNotCompletedException;

import java.util.List;


public interface SquadDAO {
    /** Creates a Squad in the the databases
     * @param squad squad to be created
     * @throws ActionNotCompletedException when the construction fails in one of the databases
     */
    void createSquad(Squad squad) throws ActionNotCompletedException;

    /** Gets information about a playlist
     * @param squadlistID the id of the playlist to retrieve
     * @return Squad object containing all the information about the playlist
     * @throws ActionNotCompletedException when the operation fails
     */
    Squad getSquad(String squadlistID) throws ActionNotCompletedException;

    /** Gets the information about the favourite Squad of a user
     * @param user user of which we want to retrieve the favourite squad
     * @return Squad object containing the information of the favourite squad of the user
     * @throws ActionNotCompletedException when the operation fails
     */
    Squad getFavourite(User user) throws ActionNotCompletedException;

    /** Adds a song to a playlist
     * @param squad playlist that we want to update
     * @param player to add
     * @throws ActionNotCompletedException when the operation fails
     */
    void addPlayer(Squad squad, Player player) throws ActionNotCompletedException;

    /** Deletes a song from a playlist
     * @param squad that we want to update
     * @param player  to delete
     * @throws ActionNotCompletedException when the operation fails
     */
    void deletePlayer(Squad squad, Player player) throws ActionNotCompletedException;

    /** Adds a song to the favourite playlist of a user
     * @param user user of which we want to update the favourite playlist
     * @param player  to add
     * @throws ActionNotCompletedException when the operation fails
     */
    void addPlayerToFavourite(User user, Player player) throws ActionNotCompletedException;

    /** Deletes a song from the favourite playlist of a user
     * @param user user of which we want to update the favourite playlist
     * @param player to delete
     * @throws ActionNotCompletedException when the operation fails
     */
    void deletePlayerFromFavourite(User user, Player player) throws ActionNotCompletedException;

    /** Deletes a squad
     * @param squad to delete
     * @throws ActionNotCompletedException when the operation fails
     */
    void deleteSquad(Squad squad) throws ActionNotCompletedException;


    /** Update the name of a Squad
     * @param squad to rename
     * @param newName new name of the squad
     * @throws ActionNotCompletedException
     */
    void updateSquadName(Squad squad, String newName) throws ActionNotCompletedException;

    /** Checks if the player is the favourite of the user
     * @param user
     * @param player
     * @return true if the player is int the favourite squad of the user
     */
    boolean isPlayerFavourite(User user, Player player);

    /** Gets all the songs contained in a playlist
     * @param squad of which we want to retrieve the songs
     * @return the list of songs of the playlist
     * @throws ActionNotCompletedException when the operation fails
     */
    List<Player> getAllPlayers(Squad squad) throws ActionNotCompletedException;

    /** It's an Analytic function
     *  Gets the suggested playlists of a user
     * @param user user of which we want to retrieve suggested playlists
     * @return the list of suggested playlists
     * @throws ActionNotCompletedException when the operation fails
     */
    List<Squad> getSuggestedSquads(User user) throws ActionNotCompletedException;

    /** It's an Analytic function
     *  Gets the suggested playlists of a user
     * @param user user of which we want to retrieve suggested playlists
     * @param limit max number of suggested playlists to return
     * @return the list of suggested playlists
     * @throws ActionNotCompletedException when the operation fails
     */
    List<Squad> getSuggestedSquads(User user, int limit) throws ActionNotCompletedException;

    /** Deletes a playlist from the MongoDB database
     * @param squad we want to delete
     * @throws ActionNotCompletedException when the operation fails
     */
    void deleteSquadDocument(Squad squad) throws ActionNotCompletedException;

    /** Gets the number of playlists saved on the databases
     * @return the number of playlists saved on the databases
     */
    int getTotalSquads();

}

