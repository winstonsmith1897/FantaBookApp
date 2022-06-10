package it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.dao;

import com.google.common.annotations.VisibleForTesting;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Player;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.PrivilegeLevel;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Squad;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.User;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.exception.ActionNotCompletedException;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.log.UMLogger;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.persistence.mongoconnection.*;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.persistence.mongoconnection.Collections;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.persistence.neo4jconnection.Neo4jDriver;
import javafx.util.Pair;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.Neo4jException;

import java.util.*;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Updates.*;
import static org.neo4j.driver.Values.parameters;
import org.neo4j.driver.Record;


public class UserDAOImpl implements UserDAO{
    private static final Logger logger = UMLogger.getUserLogger();

    @Override
    public void createUser(User user) throws ActionNotCompletedException {
        Random random = new Random();
        try {
            createUserDocument(user);
            System.out.println("Creating User ...");
            createUserNode(user);
            System.out.println("\n\nFavourites\n\n");
            System.out.println(user.getUsername());

            Squad squad = new Squad(
                    user.getUsername(),
                    "Favourites of "
                            + user.getFirstName() + " " + user.getLastName()
            );
            squad.setFavourite(true);
            SquadDAOImpl squadDAO = new SquadDAOImpl();
            squadDAO.createSquad(squad);
            System.out.println("\n\nSQUAD CREATED\n\n");
            logger.info("Created user <" +user.getUsername()+ ">");

        } catch (MongoException mEx) {
            logger.error(mEx.getMessage());
            throw new ActionNotCompletedException(mEx, mEx.getCode());
        }
        catch (Neo4jException n4jEx) {
            logger.error(n4jEx.getMessage());
            try {
                deleteUserDocument(user);
                throw new ActionNotCompletedException(n4jEx);
            } catch (MongoException mEx) {
                logger.error(mEx.getMessage());
                throw new ActionNotCompletedException(mEx);
            }
        }

    }

    @Override
    public User getUserByUsername(String username)  throws ActionNotCompletedException{
        User user = null;

        try (MongoCursor<Document> cursor =
                     MongoDriver.getInstance().getCollection(Collections.USERS).find(eq("_id", username)).iterator()) {
            if (cursor.hasNext()) {
                user = new User(cursor.next());
                System.out.println(user);
            }
        } catch (MongoException mEx) {
            logger.warn(mEx.getMessage());
            throw new ActionNotCompletedException(mEx);
        }
        return user;
    }

    @Override
    public List<User> getUserByPartialUsername(String partialUsername) throws ActionNotCompletedException {
        return getUserByPartialUsername(partialUsername, 40);
    }

    @VisibleForTesting
    List<User> getUserByPartialUsername(String partialUsername, int limitResult) throws ActionNotCompletedException, IllegalArgumentException {
        if(limitResult <= 0) throw new IllegalArgumentException();

        MongoCollection<Document> userCollection = MongoDriver.getInstance().getCollection(Collections.USERS);
        List<User> usersToReturn = new ArrayList<>();

        Bson match = match(regex("_id", "(?i)^" + partialUsername + ".*"));

        try (MongoCursor<Document> cursor = userCollection.aggregate(Arrays.asList(match, limit(limitResult))).iterator()) {
            while(cursor.hasNext()) {
                usersToReturn.add(new User(cursor.next()));
            }
        } catch (MongoException mongoEx) {
            logger.error(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }
        return usersToReturn;
    }


    @Override
    public List<User> getSuggestedUsers(User user) throws ActionNotCompletedException {
        return getSuggestedUsers(user, 40);
    }

    @Override
    public List<User> getSuggestedUsers(User user, int limit) throws ActionNotCompletedException, IllegalArgumentException {
        if(limit <= 0 || user == null) throw new IllegalArgumentException();

        List<User> list;
        try( Session session = Neo4jDriver.getInstance().getDriver().session()) {
            list = session.readTransaction((TransactionWork<List<User>>) tx -> {
                Result result = tx.run(
                        "MATCH (me:User {username: $me})-[:FOLLOWS_USER]->(followed:User)"
                                + "-[:FOLLOWS_USER]->(suggested:User) WHERE NOT (me)-[:FOLLOWS_USER]->(suggested) "
                                + "AND me <> suggested RETURN suggested, count(*) AS Strength "
                                + "ORDER BY Strength DESC LIMIT $limit",
                        parameters("me", user.getUsername(), "limit", limit)
                );
                ArrayList<User> firstLayerUsers = new ArrayList<>();
                while ((result.hasNext())){
                    Record r = result.next();
                    firstLayerUsers.add(new User(r.get("suggested").get("username").asString()));
                }
                return firstLayerUsers;
            });

            final int firstSuggestionSize = list.size();
            if(firstSuggestionSize < limit) {
                List<User> secondLayerSuggestion = session.readTransaction((TransactionWork<List<User>>) tx -> {
                    Result result = tx.run(
                            "MATCH (me:User {username: $username})-[:LIKES]->()<-[:LIKES]-(suggested:User) "
                                    + "WHERE NOT (me)-[:FOLLOWS_USER]->(suggested) "
                                    + "AND NOT (me)-[:FOLLOWS_USER]->()-[:FOLLOWS_USER]->(suggested) AND me <> suggested "
                                    + "RETURN suggested, count(*) AS Strength ORDER BY Strength DESC LIMIT $limit",
                            parameters("username", user.getUsername(), "limit", limit - firstSuggestionSize)
                    );
                    ArrayList<User> secondLayerUsers = new ArrayList<>();
                    while ((result.hasNext())){
                        Record r = result.next();
                        secondLayerUsers.add(new User(r.get("suggested").get("username").asString()));
                    }
                    return secondLayerUsers;
                });
                list.addAll(secondLayerSuggestion);
            }
        } catch (Neo4jException n4jEx) {
            logger.error(n4jEx.getMessage());
            throw new ActionNotCompletedException(n4jEx);
        }
        return list;
    }

    @Override
    public boolean checkUserPassword(String username, String password) {
        try (MongoCursor<Document> cursor = MongoDriver.getInstance().getCollection(Collections.USERS)
                .find(eq("_id", username)).iterator()) {
            if (cursor.hasNext())
                if(password.equals(cursor.next().get("password").toString()))
                    return true;
        } catch (MongoException mEx) {
            return false;
        }
        return false;
    }

    @Override
    public void followUser(User userFollowing, User userFollowed) throws ActionNotCompletedException, IllegalArgumentException {
        if(userFollowing == null || userFollowed == null) throw new IllegalArgumentException();

        try (Session session = Neo4jDriver.getInstance().getDriver().session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (following:User { username: $following }) "
                                + "MATCH (followed:User { username: $followed }) "
                                + "WHERE following <> followed "
                                + "MERGE (following)-[:FOLLOWS_USER]->(followed)",
                        parameters("following", userFollowing.getUsername(), "followed", userFollowed.getUsername())
                );
                return null;
            });
            logger.info("User <" + userFollowing.getUsername() + "> follows user <" + userFollowed.getUsername() + ">");
        } catch (Neo4jException n4jEx) {
            logger.error(n4jEx.getMessage());
            throw new ActionNotCompletedException(n4jEx);
        }
    }

    @Override
    public void unfollowUser(User userFollowing, User userFollowed) throws ActionNotCompletedException, IllegalArgumentException {
        if(userFollowing == null || userFollowed == null) throw new IllegalArgumentException();

        try (Session session = Neo4jDriver.getInstance().getDriver().session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (:User { username: $username1 })-[f:FOLLOWS_USER]->(:User { username: $username2 }) "
                                + "DELETE f",
                        parameters("username1", userFollowing.getUsername(), "username2", userFollowed.getUsername())
                );
                return null;
            });
            logger.info("Deleted user <" +userFollowing.getUsername()+ "> follows user <" +userFollowed.getUsername()+ ">");
        } catch (Neo4jException n4jEx) {
            logger.error(n4jEx.getMessage());
            throw new ActionNotCompletedException(n4jEx);
        }
    }

    @Override
    public void followSquad(User user, Squad squad) throws ActionNotCompletedException, IllegalArgumentException {
        if(user == null || squad == null) throw new IllegalArgumentException();

        try (Session session = Neo4jDriver.getInstance().getDriver().session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (following:User { username: $following }) "
                                + "MATCH (followed:Squad { squadId: $followed }) "
                                + "MERGE (following)-[:FOLLOWS_squad]->(followed)",
                        parameters("following", user.getUsername(), "followed", squad.getID())
                );
                return null;
            });
            logger.info("User <" +user.getUsername()+ "> follows squad <" + squad.getID()+ ">");
        } catch (Neo4jException n4jEx) {
            logger.error(n4jEx.getMessage());
            throw new ActionNotCompletedException(n4jEx);
        }
    }

    @Override
    public boolean isFollowingSquad(User user, Squad squad) throws IllegalArgumentException {
        if(user == null || squad == null) throw new IllegalArgumentException();

        try( Session session = Neo4jDriver.getInstance().getDriver().session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run(
                        "MATCH (:User { username: $username })-[f:FOLLOWS_squad]->(:squad { squadId: $squadId }) "
                                + "RETURN count(*) AS Times",
                        parameters("username", user.getUsername(), "squadId", squad.getID())
                );
                if ((result.hasNext())) {
                    int times = result.next().get("Times").asInt();
                    return times > 0;
                }
                return false;
            });
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isFollowedBy(User followed, User following) throws IllegalArgumentException {
        if(followed == null || following == null) throw new IllegalArgumentException();

        try( Session session = Neo4jDriver.getInstance().getDriver().session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run(
                        "MATCH (:User { username: $following })-[f:FOLLOWS_USER]->(:User { username: $followed }) "
                                + "RETURN count(*) AS Times",
                        parameters("following", following.getUsername(), "followed", followed.getUsername())
                );
                if ((result.hasNext())) {
                    int times = result.next().get("Times").asInt();
                    return times > 0;
                }
                return false;
            });
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public void unfollowSquad(User user, Squad squad) throws ActionNotCompletedException, IllegalArgumentException {
        if(user == null || squad == null) throw new IllegalArgumentException();

        try (Session session = Neo4jDriver.getInstance().getDriver().session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (:User { username: $username })-[f:FOLLOWS_squad]->(:squad { squadId: $squadId }) "
                                + "DELETE f",
                        parameters("username", user.getUsername(), "squadId", squad.getID())
                );
                return null;
            });
            logger.info("Deleted user <" +user.getUsername()+ "> follows squad <" + squad.getID()+ ">");
        } catch (Neo4jException n4jEx) {
            logger.error(n4jEx.getMessage());
            throw new ActionNotCompletedException(n4jEx);
        }
    }

    @Override
    public void likePlayer(User user, Player player) throws ActionNotCompletedException, IllegalArgumentException {
        if(user == null || player == null) throw new IllegalArgumentException();

        try (Session session = Neo4jDriver.getInstance().getDriver().session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (u:User { username: $username }) "
                                + "MATCH (s:Player { playerId: $playerId }) "
                                + "MERGE (u)-[:LIKES]->(s)",
                        parameters("username", user.getUsername(), "playerId", player.getID())
                );
                return null;
            });
            logger.info("User <" + user.getUsername() + "> likes player <" + player.getID() + ">");

            // Handle the redundancy $likeCount
            PlayerDAOImpl playerDAO = new PlayerDAOImpl();
            playerDAO.incrementLikeCount(player);
        } catch (Neo4jException n4jEx) {
            logger.error(n4jEx.getMessage());
            throw new ActionNotCompletedException(n4jEx);
        }
    }

    @Override
    public boolean userLikesPlayer(User user, Player player) throws IllegalArgumentException {
        if(user == null || player == null) throw new IllegalArgumentException();

        try( Session session = Neo4jDriver.getInstance().getDriver().session()) {
            return session.readTransaction((TransactionWork<Boolean>) tx -> {
                Result result = tx.run(
                        "MATCH (:User { username: $username })-[l:LIKES]->(:Player { playerId: $playerId }) "
                                + "RETURN count(*) AS Times",
                        parameters("username", user.getUsername(), "playerId", player.getID())
                );
                if ((result.hasNext())) {
                    int times = result.next().get("Times").asInt();
                    return times > 0;
                }
                return false;
            });
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public void deleteLike(User user, Player player) throws ActionNotCompletedException, IllegalArgumentException {
        if(user == null || player == null) throw new IllegalArgumentException();

        try (Session session = Neo4jDriver.getInstance().getDriver().session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MATCH (:User { username: $username })-[l:LIKES]->(:Player { playerId: $playerId }) "
                                + "DELETE l",
                        parameters("username", user.getUsername(), "playerId", player.getID())
                );
                return null;
            });
            logger.info("Deleted user <" + user.getUsername() + "> likes player <" + player.getID() + ">");

            // Handle the redundancy $likeCount
            PlayerDAOImpl playerDAO = new PlayerDAOImpl();
            playerDAO.decrementLikeCount(player);
        } catch (Neo4jException n4jEx) {
            logger.error(n4jEx.getMessage());
            throw new ActionNotCompletedException(n4jEx);
        }
    }

    @Override
    public void updateUserPrivilegeLevel(User user, PrivilegeLevel newPrivLevel) throws ActionNotCompletedException, IllegalArgumentException {
        if(user == null || newPrivLevel == null)
            throw new IllegalArgumentException();

        user.setPrivilegeLevel(newPrivLevel);
        try {
            updateUserDocument(user);
            logger.info("Updated privilege level of user <" +user.getUsername()+ "> : new level <" +user.getPrivilegeLevel()+ ">");
        } catch (MongoException mEx) {
            logger.warn(mEx.getMessage());
            throw new ActionNotCompletedException(mEx);
        }
    }

    @Override
    public List<Squad> getAllSquad(User user) throws ActionNotCompletedException, IllegalArgumentException {
        if(user == null) throw new IllegalArgumentException();

        MongoCollection<Document> usersCollection = MongoDriver.getInstance().getCollection(Collections.USERS);
        List<Squad> squads = new ArrayList<>();
        System.out.println(user.getUsername());
        Bson match = match(eq("_id", user.getUsername()));
        Bson unwind = unwind("$createdSquads");

        try (MongoCursor<Document> cursor = usersCollection.aggregate(Arrays.asList(match, unwind)).iterator()) {
            while(cursor.hasNext()) {
                Document result = cursor.next();
                System.out.println("DOCUMENT RESULT");
                System.out.println(result);
                System.out.println(result.get("createdSquads", Document.class));
                squads.add(new Squad(result.get("createdSquads", Document.class), user.getUsername()));
                System.out.println("SQUADS");
                System.out.println(squads);
            }
        }catch (MongoException mongoEx) {
            System.out.println("!!!!! ERROR !!!!!!!!");
            logger.error(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }
        return squads;
    }

    @Override
    public List<Squad> getFollowedSquad(User user) throws ActionNotCompletedException, IllegalArgumentException {
        if(user == null) throw new IllegalArgumentException();

        try( Session session = Neo4jDriver.getInstance().getDriver().session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run(
                        "MATCH (:User {username: $username})-[:FOLLOWS_squad]->(squad:Squad) RETURN squad ",
                        parameters("username", user.getUsername())
                );
                List<Squad> tmpList = new ArrayList<>();
                while ((result.hasNext())) {
                    Squad squad = new Squad(result.next().get("squad"));
                    squad.setOwner(user.getUsername());
                    tmpList.add(squad);
                }
                return tmpList;
            });
        } catch (Neo4jException n4jEx) {
            logger.error(n4jEx.getMessage());
            throw new ActionNotCompletedException(n4jEx);
        }
    }

    @Override
    public List<User> getFollowedUsers(User user) throws ActionNotCompletedException, IllegalArgumentException {
        if(user == null) throw new IllegalArgumentException();

        try( Session session = Neo4jDriver.getInstance().getDriver().session()) {
            return session.readTransaction((TransactionWork<List<User>>) tx -> {
                Result result = tx.run(
                        "MATCH (:User {username: $username})-[:FOLLOWS_USER]->(followedUser:User) RETURN followedUser ",
                        parameters("username", user.getUsername())
                );
                List<User> tmpList = new ArrayList<>();
                while ((result.hasNext())) {
                    User followedUser = new User(result.next().get("followedUser"));
                    tmpList.add(followedUser);
                }
                return tmpList;
            });
        } catch (Neo4jException n4jEx) {
            logger.error(n4jEx.getMessage());
            throw new ActionNotCompletedException(n4jEx);
        }
    }

    @Override
    public List<User> getFollowers(User user) throws ActionNotCompletedException, IllegalArgumentException {
        if(user == null) throw new IllegalArgumentException();

        try( Session session = Neo4jDriver.getInstance().getDriver().session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run(
                        "MATCH (followingUser:User)-[:FOLLOWS_USER]->(:User {username: $username}) RETURN followingUser ",
                        parameters("username", user.getUsername())
                );
                List<User> tmpList = new ArrayList<>();
                while ((result.hasNext())) {
                    User followingUser = new User(result.next().get("followingUser"));
                    tmpList.add(followingUser);
                }
                return tmpList;
            });
        } catch (Neo4jException n4jEx) {
            logger.error(n4jEx.getMessage());
            throw new ActionNotCompletedException(n4jEx);
        }
    }

    @Override
    public List<Pair<String, Integer>> getFavouriteNation(int team) throws ActionNotCompletedException, IllegalArgumentException{
        if(team <= 0) throw new IllegalArgumentException();

        MongoCollection<Document> usersCollection = MongoDriver.getInstance().getCollection(Collections.USERS);
        List<Pair<String, Integer>> result = new ArrayList<>();
        Bson unwind1 = unwind("$createdsquads");
        Bson unwind2 = unwind("$createdsquads.players");
        Bson group = Document.parse("{$group: {" +
                "_id: \"$createdsquads.players.genre\"," +
                "totalplayers: { $sum: 1}" +
                "}}");
        Bson sort = sort(descending("totalPlayers"));
        Bson limit = limit(team);
        try (MongoCursor<Document> cursor = usersCollection.aggregate(Arrays.asList(unwind1, unwind2, group, sort, limit)).iterator()) {
            while(cursor.hasNext()) {
                Document genre = cursor.next();
                result.add(new Pair<>(genre.getString("_id"), genre.getInteger("totalPlayers")));
            }
        } catch (MongoException mEx) {
            logger.error(mEx.getMessage());
            throw new ActionNotCompletedException(mEx);
        }
        return result;
    }

    @Override
    public List<Pair<String, Integer>> getFavouriteTeam(int lim) throws ActionNotCompletedException, IllegalArgumentException{
        //if(team <= 0) throw new IllegalArgumentException();

        MongoCollection<Document> usersCollection = MongoDriver.getInstance().getCollection(Collections.USERS);
        List<Pair<String, Integer>> result = new ArrayList<>();
        Bson unwind1 = unwind("$createdSquads");
        Bson unwind2 = unwind("$createdSquads.players");
        Bson group = Document.parse("{$group: {" +
                "_id: \"$createdSquads.players.team\"," +
                "totalPlayers: { $sum: 1}" +
                "}}");
        Bson sort = sort(descending("totalPlayers"));
        Bson limit = limit(lim);
        try (MongoCursor<Document> cursor = usersCollection.aggregate(Arrays.asList(unwind1, unwind2, group, sort, limit)).iterator()) {
            while(cursor.hasNext()) {
                Document team = cursor.next();
                System.out.println(team);
                result.add(new Pair<>(team.getString("_id"), team.getInteger("totalPlayers")));
            }
        } catch (MongoException mEx) {
            logger.error(mEx.getMessage());
            throw new ActionNotCompletedException(mEx);
        }
        return result;
    }

    @Override
    public List<Pair<Double, Pair<String, String>>> getFavouritePlayerPerNation() throws ActionNotCompletedException {
        MongoCollection<Document> userCollection = MongoDriver.getInstance().getCollection(Collections.USERS);
        List<Pair<Double, Pair<String, String>>> topPlayers = new ArrayList<>();

        Bson computeyear = Document.parse("{$multiply: [{ $floor:{ $divide: [ \"$age\", 10 ] }}, 10]}");

        Bson match = match(exists("createdSquads.players.age"));
        Bson projectyear = project(fields(
                excludeId(),
                include("createdSquads.players.name"),
                include("age"),
                computed("year", computeyear)
        ));
        Bson unwind1 = unwind("$createdSquads");
        Bson unwind2 = unwind("$createdSquads.players");
        Bson groupPlayers = Document.parse(
                "{ $group:" +
                        "    {" +
                        "        _id: { year: \"$year\", player: \"$createdSquads.players.name\" }," +
                        "        presences: { $sum: 1}" +
                        "    }" +
                        "}"
        );
        Bson sortPresences = sort(descending("presences"));
        Bson groupyears = Document.parse(
                "{ $group:" +
                        "    {" +
                        "        _id: \"$_id.year\"," +
                        "        favouritePlayer: { $first: \"$_id.player\" }," +
                        "        playerPresences: { $first: \"$presences\" }," +
                        "        overallPresences: { $sum: \"$presences\" }" +
                        "    }" +
                        "}"
        );
        Bson finalProject = project(fields(
                excludeId(),
                include("favouritePlayer"),
                computed("year", "$_id"),
                include("playerPresences"),
                include("overallPresences")
        ));
        Bson sortyears = sort(ascending("year"));

        try (MongoCursor<Document> cursor = userCollection.aggregate(Arrays.asList(match, unwind1, unwind2, projectyear, groupPlayers, sortPresences, groupyears, finalProject, sortyears)).iterator()) {
            while (cursor.hasNext()) {
                Document record = cursor.next();
                System.out.println(record);
                Double year = record.getDouble("year");
                System.out.println(year);

                String player = record.getString("favouritePlayer");
                String presences = String.valueOf(record.getInteger("playerPresences"));
                String overallPresences = String.valueOf(record.getInteger("overallPresences"));
                topPlayers.add(new Pair(year, new Pair<>(player, presences + ":" + overallPresences)));


            }
        } catch (MongoException mongoEx) {
            logger.error(mongoEx.getMessage());
            throw new ActionNotCompletedException(mongoEx);
        }
        return topPlayers;
    }

    @Override
    public void deleteUser(User user) throws ActionNotCompletedException, IllegalArgumentException {
        if(user == null) throw new IllegalArgumentException();

        List<Squad> userSquads = getAllSquad(user);

        SquadDAOImpl squadDAO = new SquadDAOImpl();
        for(Squad squad: userSquads)
            squadDAO.deleteSquad(squad);

        try {
            deleteUserDocument(user);
            deleteUserNode(user);
            logger.info("DELETED User " + user.getUsername());
        } catch (MongoException | Neo4jException mEx) {
            logger.error(mEx.getMessage());
            throw new ActionNotCompletedException(mEx);
        }
    }

    /**
     * @return the number of users present in the application.
     */
    @Override
    public int getTotalUsers() {
        try (Session session = Neo4jDriver.getInstance().getDriver().session())
        {
            return session.readTransaction((TransactionWork<Integer>) tx -> {

                Result result = tx.run("MATCH (:User) RETURN COUNT(*) AS NUM");
                if(result.hasNext())
                    return result.next().get("NUM").asInt();
                else
                    return -1;
            });

        }catch (Neo4jException n4jEx){
            logger.error(n4jEx.getMessage());
            return -1;
        }
    }

    //--------------------------PACKAGE-----------------------------------------------------------

    void addSquadToUserDocument(User user, Squad squad) throws MongoException, IllegalArgumentException{

        if(user == null || squad == null) throw new IllegalArgumentException();

        Document squadDoc = squad.toBsonDocument();

        MongoCollection<Document> userColl = MongoDriver.getInstance().getCollection(Collections.USERS);
        userColl.updateOne(
                eq("_id", user.getUsername()),
                addToSet("createdSquads", squadDoc)
        );
    }

    //--------------------------PRIVATE------------------------------------------------------------

    private void createUserDocument(User user) throws MongoException {
        Document userDoc = user.toBsonDocument();
        //System.out.println(userDoc);

        MongoCollection<Document> userColl = MongoDriver.getInstance().getCollection(Collections.USERS);
        //System.out.println(userColl);
        userColl.insertOne(userDoc);
    }

    private void createUserNode(User user) throws Neo4jException {
        try (Session session = Neo4jDriver.getInstance().getDriver().session()) {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run("MERGE (a:User {username: $username})",
                        parameters("username", user.getUsername())
                );
                return null;
            });
        }
    }

    private void updateUserDocument(User user) throws MongoException {
        MongoCollection<Document> userColl = MongoDriver.getInstance().getCollection(Collections.USERS);
        userColl.updateOne(
                eq("_id", user.getUsername()),
                combine(
                        set("firstName", user.getFirstName()),
                        set("lastName", user.getLastName()),
                        set("age", user.getAge()),
                        set("privilegeLevel", user.getPrivilegeLevel().toString())
                ));
    }

    @Override
    public void deleteUserDocument(User user) throws MongoException {
        MongoCollection<Document> userColl = MongoDriver.getInstance().getCollection(Collections.USERS);
        userColl.deleteOne(eq("_id", user.getUsername()));
    }

    public void deleteUserNode(User user) throws Neo4jException {
        try (Session session = Neo4jDriver.getInstance().getDriver().session())
        {
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run(
                        "MATCH (a:User {username: $username})"
                                + "DETACH DELETE a",
                        parameters("username", user.getUsername()));
                return null;
            });
        }
    }

}
