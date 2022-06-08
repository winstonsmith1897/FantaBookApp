package it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.persistence.mongoconnection;

import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.exception.ActionNotCompletedException;
import org.bson.Document;

import java.util.ArrayList;

public class MongoDriver {
    private static final MongoDriver instance = new MongoDriver();
    private final MongoClient client;
    private final MongoDatabase mongoDB;
//,172.16.4.225:27020/172.16.4.223:27020,
    public MongoDriver() {
        String connectionString = "mongodb://127.0.0.1:27017";
                //+ "?w=1&readPreference=nearest";
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
    public static MongoDriver getInstance() { return instance; }
    public MongoCollection getCollection(Collections collectionName) {
        return mongoDB.getCollection(collectionName.toString());
    }
    public void closeConnection() { client.close(); }
    public static void main(String[] args) throws ActionNotCompletedException {
        getInstance();
    }
}
