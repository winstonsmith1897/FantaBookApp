package middleware.persistence.mongoconnection;

import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.persistence.mongoconnection.Collections;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.persistence.mongoconnection.MongoDriver;
import com.mongodb.ConnectionString;
import com.mongodb.client.*;

import org.junit.jupiter.api.Test;

public class MongoDriverTest {
    private static final MongoDriver instance = new MongoDriver();
    private final MongoClient client;
    private final MongoDatabase mongoDB;

    private MongoDriverTest() {
        String connectionString = "mongodb://172.16.4.223:27020,172.16.4.224:27020,172.16.4.225:27020/"
                + "?w=1&readPreference=nearest";
        client = MongoClients.create(new ConnectionString(connectionString));
        // connect with the
        System.out.println("Successful");
        String databaseName = "FantaBook";
        mongoDB = client.getDatabase(databaseName);
        MongoIterable<String> cols = client.listDatabaseNames();

        for (String s : cols) {
            System.out.println(s);
        }

    }
    @Test
    public static MongoDriver getInstance() { return instance; }
    @Test
    public MongoCollection getCollection() {
        Collections collectionName = Collections.PLAYERS;
        return mongoDB.getCollection(collectionName.toString());
    }
    @Test
    public void closeConnection() { client.close(); }
}
