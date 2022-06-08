package it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.dao;


import com.google.common.annotations.VisibleForTesting;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Player;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.exception.ActionNotCompletedException;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.log.UMLogger;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.persistence.mongoconnection.Collections;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.persistence.mongoconnection.MongoDriver;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.persistence.neo4jconnection.Neo4jDriver;
import javafx.util.Pair;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionWork;
import org.neo4j.driver.exceptions.Neo4jException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.inc;
import static org.neo4j.driver.Values.parameters;


public class PlayerDAOImpl implements PlayerDAO {
    private static final Logger logger = UMLogger.getPlayerLogger();


    //-----------------------------------------------  CREATE  -----------------------------------------------


    @Override
    public void createPlayer(Player player) throws ActionNotCompletedException {

        if (player == null || player.getID() == null)
            throw new IllegalArgumentException();

        try {
            createPlayerDocument(player);
            createPlayerNode(player);
            logger.info("Created player <" + player.getID() + ">");

        } catch (MongoException mongoEx) {
            logger.error(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        } catch (Neo4jException neoEx) {
            logger.error(neoEx.getMessage());
            try {
                deletePlayerDocument(player);
                throw new ActionNotCompletedException(neoEx);
            } catch (MongoException mongoEx) {
                logger.error(mongoEx.getMessage());
                throw new ActionNotCompletedException(mongoEx);
            }
        }
    }



    /**
     * Add a player document in MongoDb.
     *
     * @param player the player you want to add to mongoDb.
     * @throws MongoException when the database write fails.
     */
    private void createPlayerDocument(Player player) throws MongoException {

        MongoCollection<Document> playerCollection = MongoDriver.getInstance().getCollection(Collections.PLAYERS);

        Document playerDocument = player.toBsonDocument();

        playerCollection.insertOne(playerDocument);

    }

    /**
     * Add a player node in Neo4j.
     *
     * @param player the player you want to add to Neo4j.
     * @throws Neo4jException when the database write fails.
     */
    private void createPlayerNode(Player player) throws Neo4jException {

        try (Session session = Neo4jDriver.getInstance().getDriver().session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {

                tx.run("CREATE (p:Player {playerId: $playerId, name: $name, team: $team," +
                                " imageUrl: $imageUrl, position: $position})",
                        parameters("playerId", player.getID(), "name", player.getName(),
                                "team", player.getTeam(), "imageUrl", player.getImageUrl(), "position", player.getPosition()));
                return null;
            });
        }
    }

    //----------------------------------------------  RETRIEVE  ----------------------------------------------


    /**
     * @param playerID the id of the player you want to to return.
     * @return the player with the specified id.
     */
    @Override
    public Player getPlayerByTeam(String playerID) {

        MongoCollection<Document> playerCollection = MongoDriver.getInstance().getCollection(Collections.PLAYERS);
        Player playerToReturn = null;

        try (MongoCursor<Document> cursor = playerCollection.find(eq("team", playerID)).iterator()) {
            if (cursor.hasNext()) {
                playerToReturn = new Player(cursor.next());
            }
        } catch (MongoException mongoEx) {
            logger.error(mongoEx.getMessage());
        }
        return playerToReturn;
    }

    @Override
    public Player getPlayerByName(String playerID) {

        MongoCollection<Document> playerCollection = MongoDriver.getInstance().getCollection(Collections.PLAYERS);
        Player playerToReturn = null;

        try (MongoCursor<Document> cursor = playerCollection.find(eq("name", playerID)).iterator()) {
            if (cursor.hasNext()) {
                playerToReturn = new Player(cursor.next());
            }
        } catch (MongoException mongoEx) {
            logger.error(mongoEx.getMessage());
        }
        return playerToReturn;
    }


    /**
     * @param partialInput   the partial input of the user.
     * @param maxNumber      the max number of player you want to return.
     * @param attributeField the document's attribute you want to match.
     * @return players where the specified attribute fields contains the partial input of the user (case insensitive).
     * @throws ActionNotCompletedException when a database error occurs.
     */
    @VisibleForTesting
    public List<Player> filterPlayer(String partialInput, int maxNumber, String attributeField) throws ActionNotCompletedException {

        if (attributeField == null || maxNumber <= 0)
            throw new IllegalArgumentException();

        MongoCollection<Document> playerCollection = MongoDriver.getInstance().getCollection(Collections.PLAYERS);
        List<Player> playersToReturn = new ArrayList<>();

        String capitalPartialInput = partialInput.substring(0, 1).toUpperCase() + partialInput.substring(1);

        Bson match = match(regex(attributeField, "^" + capitalPartialInput + ".*"));
        Bson sortLike = sort(descending("likeCount"));
        try (MongoCursor<Document> cursor = playerCollection.aggregate(Arrays.asList(match, sortLike, limit(maxNumber))).iterator()) {
            while (cursor.hasNext()) {
                playersToReturn.add(new Player(cursor.next()));
            }
        } catch (MongoException mongoEx) {
            logger.error(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }
        return playersToReturn;
    }

    @Override
    public List<Pair<String, Pair<Player, Integer>>> findTopRatedPlayerPerPosition() throws ActionNotCompletedException {
        MongoCollection<Document> playerCollection = MongoDriver.getInstance().getCollection(Collections.PLAYERS);
        List<Pair<String, Pair<Player, Integer>>> topPlayers = new ArrayList<>();
        Bson project = project(fields(excludeId(), include("imageUrl"), include("position"), include("name"), include("likeCount")));
        Bson group1 = Document.parse("{$group:{_id : {name: \"$name\", position: \"$position\", imageUrl:\"$imageUrl\", likeCount: \"$likeCount\"}}}");
        Bson sortRate = sort(ascending("rating"));
        Bson group2 = Document.parse("{$group:{" +
                "_id: \"$_id.position\"," +
                "topPlayerName: {$last: \"$_id.name\"}," +
                "topPlayerImage: {$last: \"$_id.imageUrl\"}," +
                "likeCount:{$last: \"$_id.likeCount\"}" +
                "}}");
        Bson sortId = sort(ascending("_id"));
        Bson project2 = project(fields(excludeId(), computed("position", "$_id"), include("topPlayerImage"), include("topPlayerName"),  include("likeCount")));


        try (MongoCursor<Document> cursor = playerCollection.aggregate(Arrays.asList(
                project,
                group1,
                sortRate,
                group2,
                sortId,
                project2
        )).iterator()){
            while(cursor.hasNext()) {
                Document record = cursor.next();
                System.out.println(record);


                Player playerToAdd = new Player();
                playerToAdd.setName(record.getString("topPlayerName"));
                playerToAdd.setImageUrl(record.getString("topPlayerImage"));
                String position = record.getString("position");
                Integer avgRating = record.getInteger("likeCount");

                Pair<String, Pair<Player, Integer>> resultToAdd= new Pair<>(position, new Pair<>(playerToAdd, avgRating));
                topPlayers.add(resultToAdd);


            }
        } catch (MongoException mongoEx) {
            logger.error(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }
        return topPlayers;
    }

    @Override
    public List<Player> getPlayerByPartialTeam(String partialInput) throws ActionNotCompletedException {
        return getPlayerByPartialTeam(partialInput, 20);
    }

    @Override
    public List<Player> getPlayerByPartialName(String partialInput) throws ActionNotCompletedException {
        return  getPlayerByPartialName(partialInput, 20);
    }

    @Override
    public List<Player> getPlayerByPartialName(String partialName, int limit) throws ActionNotCompletedException {
        return filterPlayer(partialName, limit, "name");
    }


    @Override
    public List<Player> getPlayerByPartialTeam(String partialTeam, int limit) throws ActionNotCompletedException {
        return filterPlayer(partialTeam, limit, "team");
    }

    @Override
    public List<Player> getPlayerByPartialPosition(String partialPosition) throws ActionNotCompletedException {
        return getPlayerByPartialPosition(partialPosition, 20);
    }

    @Override
    public List<Player> getPlayerByPartialPosition(String partialPosition, int limit) throws ActionNotCompletedException {
        return filterPlayer(partialPosition, limit, "position");
    }


    @Override
    public List<Pair<String, Integer>> findPlayersWithMostNumberOfGoal(int golLimit, int maxNumber) throws ActionNotCompletedException {

        if (maxNumber <= 0)
            throw new IllegalArgumentException();

        MongoCollection<Document> playerCollection = MongoDriver.getInstance().getCollection(Collections.PLAYERS);

        List<Pair<String, Integer>> playerRank = new ArrayList<>();
        Bson match2 = match(or(eq("league", "LaLiga"),
                eq("league", "Premier League"),
                eq("league", "Serie A"),
                eq("league", "Bundesliga"),
                eq("league", "Ligue 1")));

        Bson match = match(gte("goals", golLimit));
        Bson sortGoals = sort(descending("goals"));
        //Bson limit = limit(maxNumber);
        Bson project = project(fields(excludeId(), include("name"), include("goals")));
        try (MongoCursor<Document> cursor = playerCollection.aggregate(Arrays.asList(match2, match, sortGoals, project)).iterator()) {
            while (cursor.hasNext()) {
                Document player = cursor.next();
                System.out.println(player);
                playerRank.add(new Pair<>(player.getString("name"), player.getInteger("goals")));
            }
        } catch (MongoException mongoEx) {
            logger.error(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }
        return playerRank;
    }


    @Override
    public List<Player> getBestPlayers(int limit) throws ActionNotCompletedException {

        List<Player> bestPlayers = new ArrayList<>();

        try (Session session = Neo4jDriver.getInstance().getDriver().session()) {
            session.writeTransaction(tx -> {

                String query = "MATCH (s:Player)<-[l:LIKES]-(u:User) WITH s, COUNT(*) as num ORDER BY num DESC RETURN s.playerId as playerId," +
                        " s.name as name, s.position as position,s.team as team, s.imageUrl as imageUrl LIMIT $limit";

                Result result = tx.run(query, parameters("limit", limit));
                while (result.hasNext()) {
                    bestPlayers.add(new Player(result.next()));
                }
                return bestPlayers;
            });
        } catch (Neo4jException neoEx) {
            logger.error(neoEx.getMessage());
            throw new ActionNotCompletedException(neoEx);
        }
        return bestPlayers;
    }


    //-----------------------------------------------  UPDATE  -----------------------------------------------



    @Override
    public void incrementLikeCount(Player player) throws ActionNotCompletedException {

        if (player == null || player.getID() == null)
            throw new IllegalArgumentException();

        MongoCollection<Document> playerCollection = MongoDriver.getInstance().getCollection(Collections.PLAYERS);
        try {
            playerCollection.updateOne(eq("_id", player.getID()), inc("likeCount", 1));
            player.setLikeCount(player.getLikeCount() + 1);
        } catch (MongoException mongoEx) {
            logger.error(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }

    }


    @Override
    public void decrementLikeCount(Player player) throws ActionNotCompletedException {

        if (player == null || player.getID() == null)
            throw new IllegalArgumentException();

        MongoCollection<Document> playerCollection = MongoDriver.getInstance().getCollection(Collections.PLAYERS);
        try {
            playerCollection.updateOne(eq("_id", player.getID()), inc("likeCount", -1));
            player.setLikeCount(player.getLikeCount() - 1);
        } catch (MongoException mongoEx) {
            logger.error(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }
    }




    @Override
    public int getTotalPlayers() {
        try (Session session = Neo4jDriver.getInstance().getDriver().session()) {
            return session.readTransaction(tx -> {

                Result result = tx.run("MATCH (:Player) RETURN COUNT(*) AS NUM");
                if (result.hasNext())
                    return result.next().get("NUM").asInt();
                else
                    return -1;
            });

        } catch (Neo4jException neo4) {
            neo4.printStackTrace();
            return -1;
        }
    }

    //-----------------------------------------------  DELETE  -----------------------------------------------


    @Override
    public void deletePlayer(Player player) throws ActionNotCompletedException, IllegalArgumentException {
        if (player == null) throw new IllegalArgumentException();

        try {
            deletePlayerDocument(player);
            deletePlayerNode(player);
            logger.info("DELETED Player " + player.getID());
        } catch (MongoException | Neo4jException mEx) {
            logger.error(mEx.getMessage());
            throw new ActionNotCompletedException(mEx);
        }
    }

    @Override
    public void deletePlayerDocument(Player player) throws MongoException {
        MongoCollection<Document> playerColl = MongoDriver.getInstance().getCollection(Collections.PLAYERS);
        playerColl.deleteOne(eq("_id", player.getID()));
    }


    private void deletePlayerNode(Player player) throws Neo4jException {
        try (Session session = Neo4jDriver.getInstance().getDriver().session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {

                tx.run("MATCH (s:player {playerId: $playerId})"
                                + "DETACH DELETE s",
                        parameters("playerId", player.getID()));
                return null;
            });
        }
    }
}


