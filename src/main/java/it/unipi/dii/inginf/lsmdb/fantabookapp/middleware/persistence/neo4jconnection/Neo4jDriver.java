package it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.persistence.neo4jconnection;
import org.neo4j.driver.*;

public class Neo4jDriver {
    private static final Neo4jDriver instance = new Neo4jDriver();
    private final Driver driver;

    private final String uri = "neo4j://127.0.0.1:7687";
    private final String user = "neo4j";
    private final String password = "root";

    public Neo4jDriver() {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public static Neo4jDriver getInstance() { return instance; }

    public Driver getDriver() { return driver; }

    public void closeDriver() { driver.close(); }
}