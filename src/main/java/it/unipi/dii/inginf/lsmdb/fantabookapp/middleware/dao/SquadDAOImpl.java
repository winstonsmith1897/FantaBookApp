package it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.dao;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Player;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Squad;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.User;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.exception.ActionNotCompletedException;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.log.UMLogger;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.persistence.mongoconnection.Collections;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.persistence.mongoconnection.MongoDriver;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.persistence.neo4jconnection.Neo4jDriver;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionWork;
import org.neo4j.driver.exceptions.Neo4jException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.neo4j.driver.Record;


import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.*;
import static it.unipi.dii.inginf.lsmdb.fantabookapp.databasePopulation.UserPopulation.addRandomPlayers;
import static org.neo4j.driver.Values.parameters;

public class SquadDAOImpl implements SquadDAO{
    private static final Logger logger =  UMLogger.getSquadLogger();

    @Override
    public void createSquad(Squad squad)  throws ActionNotCompletedException {
        squad.setID(ObjectId.get().toString());
        try {
            createSquadDocument(squad);
            createSquadNode(squad);
            logger.info("Created squad " + squad.getID());
        } catch (MongoException mongoEx) {
            logger.error(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }
         catch (Neo4jException neoEx) {
            logger.error(neoEx.getMessage());
            try {
                deleteSquadDocument(squad);
                throw new ActionNotCompletedException(neoEx);
            } catch (MongoException mongoEx) {
                logger.error(mongoEx.getMessage());
                throw new ActionNotCompletedException(mongoEx);
            }

        }

    }

    @Override
    public Squad getSquad(String squadID)  throws ActionNotCompletedException{
        MongoCollection<Document> usersCollection = MongoDriver.getInstance().getCollection(Collections.USERS);
        Squad squad = null;


        Bson match = match(eq("createdSquads.squadId", squadID));
        Bson unwind = unwind("$createdSquads");
        Bson project = project(fields(include("_id", "createdSquads")));

        try (MongoCursor<Document> cursor = usersCollection.aggregate(Arrays.asList(match, unwind, match, project)).iterator()) {
            if(cursor.hasNext()) {
                Document result = cursor.next();
                squad = new Squad(result.get("createdSquads", Document.class), result.getString("_id"));
            }
        }catch (MongoException mongoEx) {
            logger.error(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }
        return squad;
    }

    @Override
    public Squad getFavourite(User user) throws ActionNotCompletedException {
        MongoCollection<Document> usersCollection = MongoDriver.getInstance().getCollection(Collections.USERS);
        Squad squad = null;

        if (user == null)
            return null;

        Bson match1 = match(eq("_id", user.getUsername()));
        Bson unwind = unwind("$createdSquads");
        Bson match2 = match(eq("createdSquads.isFavourite", true));
        Bson project = project(fields(include("createdSquads")));

        try (MongoCursor<Document> cursor = usersCollection.aggregate(Arrays.asList(match1, unwind, match2, project)).iterator()) {
            if(cursor.hasNext()) {
                Document result = cursor.next();
                squad = new Squad(result.get("createdSquads", Document.class), user.getUsername());
            }
        }catch (MongoException mongoEx) {
            logger.error(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }
        return squad;
    }

    @Override
    public void addPlayer(Squad squad, Player player)  throws ActionNotCompletedException{
        try {
            MongoCollection<Document> usersCollection = MongoDriver.getInstance().getCollection(Collections.USERS);

            Document playerDocument = new Document("playerId", player.getID())
                    .append("name", player.getName())
                    .append("position", player.getPosition())
                    .append("nation", player.getNationality())
                    .append("team", player.getTeam())
                    .append("age", player.getAge());
            if (player.getImageUrl() != null)
                playerDocument.append("urlImage", player.getImageUrl());

            Bson find = eq("createdSquads.squadId", squad.getID());
            Bson query = push("createdSquads.$.players", playerDocument);
            usersCollection.updateOne(find, query);
            logger.info("Added player " + player.getID() + " to squad " + squad.getID());
        } catch (MongoException mongoEx) {
            logger.error(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }

    }

    @Override
    public void deletePlayer(Squad squad, Player player) throws ActionNotCompletedException{
        try {
            MongoCollection<Document> usersCollection = MongoDriver.getInstance().getCollection(Collections.USERS);
            Bson find = eq("createdSquads.squadId", squad.getID());
            Bson query = pull("createdSquads.$.players", eq("playerId", player.getID()));

            usersCollection.updateOne(find, query);
            logger.info("Deleted player " + player.getID() + " from squad " + squad.getID());
        } catch (MongoException mongoEx) {
            logger.error(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }
    }

    public void updateSquadName(Squad squad, String newName) throws ActionNotCompletedException{
        try {
            updateSquadNameDocument(squad, newName);
            updateSquadNameNode(squad, newName);
            logger.info("Updated squad name of " + squad.getID() + " from " + squad.getName() + " to " + newName);
        } catch (MongoException mongoEx) {
            logger.error(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        } catch (Neo4jException neoEx) {
            logger.error(neoEx.getMessage());
            try {
                updateSquadNameDocument(squad, squad.getName());
                throw new ActionNotCompletedException(neoEx);
            } catch (MongoException mongoEx) {
                logger.error(mongoEx.getMessage());
                throw new ActionNotCompletedException(mongoEx);
            }
        }
    }

    @Override
    public void addPlayerToFavourite(User user, Player player) throws ActionNotCompletedException{
        addPlayer(getFavourite(user), player);
    }

    @Override
    public void deletePlayerFromFavourite(User user, Player player) throws ActionNotCompletedException{
        deletePlayer(getFavourite(user), player);
    }

    @Override
    public void deleteSquad(Squad squad) throws ActionNotCompletedException{
        try {
            deleteSquadDocument(squad);
            deleteSquadNode(squad);
            logger.info("Deleted squad " + squad.getID());
        } catch (MongoException | Neo4jException mongoEx) {
            logger.error(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }
    }

    @Override
    public boolean isPlayerFavourite(User user, Player player) {
        MongoCollection<Document> usersCollection = MongoDriver.getInstance().getCollection(Collections.USERS);

        if (user == null || player == null)
            return false;

        Bson match1 = match(eq("_id", user.getUsername()));
        Bson unwind1 = unwind("$createdSquads");
        Bson match2 = match(eq("createdSquads.isFavourite", true));
        Bson unwind2 = unwind("$createdSquads.players");
        Bson match3 = match(eq("createdSquads.players.playerId", player.getID()));

        try (MongoCursor<Document> cursor = usersCollection.aggregate(Arrays.asList(match1, unwind1, match2, unwind2, match3)).iterator()) {
            if(cursor.hasNext())
                return true;
        }catch (MongoException mongoEx) {
            return false;
        }
        return false;
    }

    @Override
    public List<Player> getAllPlayers(Squad squad) throws ActionNotCompletedException{
        MongoCollection<Document> usersCollection = MongoDriver.getInstance().getCollection(Collections.USERS);
        List<Player> players = new ArrayList<>();

        if (squad == null)
            return players;

        Bson match = match(eq("createdSquads.squadId", squad.getID()));
        Bson unwind1 = unwind("$createdSquads");
        Bson unwind2 = unwind("$createdSquads.players");

        try (MongoCursor<Document> cursor = usersCollection.aggregate(Arrays.asList(match, unwind1, match, unwind2)).iterator()) {
            while(cursor.hasNext()) {
                Document result = cursor.next().get("createdSquads", Document.class).get("players", Document.class);
                Player player = new Player();
                player.setID(result.getString("playerId"));
                player.setName(result.getString("name"));
                player.setNationality(result.getString("nationality"));
                player.setImageUrl(result.getString("urlImage"));
                player.setPosition(result.getString("position"));
                players.add(player);
            }
        }catch (MongoException mongoEx) {
            logger.error(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }
        return players;
    }

    @Override
    public List<Squad> getSuggestedSquads(User user) throws ActionNotCompletedException{
        return getSuggestedSquads(user, 40);
    }

    @Override
    public List<Squad> getSuggestedSquads(User user, int limit) throws ActionNotCompletedException{
        if (user == null || limit <= 0)
            return new ArrayList<>();
        List<Squad> firstList;
        List<Squad> secondList = new ArrayList<>();
        try( Session session = Neo4jDriver.getInstance().getDriver().session()) {
            firstList = session.readTransaction((TransactionWork<List<Squad>>) tx -> {
                Result result = tx.run(
                        "MATCH (me:User {username: $me})-[:FOLLOWS_USER]->(followed:User)"
                                + "-[:FOLLOWS_squad]->(suggested:Squad) WHERE NOT (me)-[:FOLLOWS_squad]->(suggested) "
                                + "RETURN suggested, count(*) AS Strength "
                                + "ORDER BY Strength DESC LIMIT $limit",
                        parameters("me", user.getUsername(), "limit", limit)
                );
                ArrayList<Squad> squads = new ArrayList<>();
                while ((result.hasNext())){
                    Record r = result.next();
                    squads.add(new Squad(r.get("suggested")));
                }
                return squads;
            });

            //second level suggestions
            final int firstSuggestionsSize = firstList.size();

            if (firstList.size() < limit) {
                secondList = session.readTransaction((TransactionWork<List<Squad>>) tx2 -> {
                    Result result = tx2.run(
                            "MATCH (me:User {username: $me})-[:FOLLOWS_USER]->(followed:User)\n" +
                                    "-[:FOLLOWS_USER]->(suggestedUser:User)-[:FOLLOWS_squad]->(suggestedsquad) \n" +
                                    "WHERE NOT (me)-[:FOLLOWS_USER]->(suggestedUser)  \n" +
                                    "AND me <> suggestedUser \n" +
                                    "AND NOT (me)-[:FOLLOWS_squad]->(suggestedsquad)\n" +
                                    "AND NOT (followed)-[:FOLLOWS_squad]->(suggestedsquad)\n" +
                                    "RETURN suggestedsquad, count(*) AS Strength \n" +
                                    "ORDER BY Strength DESC LIMIT $limit",
                            parameters("me", user.getUsername(), "limit", limit - firstSuggestionsSize)
                    );
                    ArrayList<Squad> squads = new ArrayList<>();
                    while ((result.hasNext())) {
                        Record r = result.next();
                        squads.add(new Squad(r.get("suggestedSquad")));
                    }
                    return squads;
                });
            }
        } catch (Neo4jException n4jEx) {
            logger.error(n4jEx.getMessage());
            throw new ActionNotCompletedException(n4jEx);
        }
        firstList.addAll(secondList);
        return firstList;
    }

    /**
     * @return the number of squads present in the application.
     */
    @Override
    public int getTotalSquads() {
        try (Session session = Neo4jDriver.getInstance().getDriver().session())
        {
            return session.readTransaction((TransactionWork<Integer>) tx -> {

                Result result = tx.run("MATCH (:Squad) RETURN COUNT(*) AS NUM");
                if(result.hasNext())
                    return result.next().get("NUM").asInt();
                else
                    return -1;
            });

        }catch (Neo4jException neo4){
            neo4.printStackTrace();
            return -1;
        }
    }

    private void createSquadDocument(Squad squad) throws ActionNotCompletedException {
        UserDAOImpl userDAO = new UserDAOImpl();
        userDAO.addSquadToUserDocument(new User(squad.getOwner()), squad);
        System.out.println("\n\nSquad Added To User\n\n");
        addRandomPlayers(squad, 22);
    }

    private void createSquadNode(Squad squad) {
        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "CREATE (p:Squad {squadId: $squadId, name: $name, urlImage: $urlImage})",
                        parameters("squadId", squad.getID(), "name", squad.getName(),
                                "urlImage", squad.getUrlImage()) );
                return null;
            });
        }
    }

    @Override
    public void deleteSquadDocument(Squad squad) throws MongoException{
        Bson find = eq("createdSquads.squadId",squad.getID());
        MongoCollection<Document> usersCollection = MongoDriver.getInstance().getCollection(Collections.USERS);
        usersCollection.updateOne(find, pull("createdSquads", eq("squadId", squad.getID())));
    }

    private void deleteSquadNode(Squad squad) throws Neo4jException{
        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (p:Squad { squadId: $squadId }) DETACH DELETE p",
                        parameters("squadId", squad.getID()) );
                return null;
            });
        }
    }

    private void updateSquadNameDocument(Squad squad, String newName) throws ActionNotCompletedException{
        MongoCollection<Document> userCollection = MongoDriver.getInstance().getCollection(Collections.USERS);

        Bson find = eq("createdSquads.squadId", squad.getID());
        Bson updateQuery = set ("createdSquads.$.name", newName);

        try{
            userCollection.updateOne(find, updateQuery);
        } catch (MongoException mongoEx) {
            logger.error(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }
    }

    private void updateSquadNameNode(Squad squad, String newName) {
        try ( Session session = Neo4jDriver.getInstance().getDriver().session() )
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run( "MATCH (p:Squad { squadId: $squadId }) SET p.name = $newName",
                        parameters("squadId", squad.getID(), "newName", newName) );
                return null;
            });
        }
    }

}