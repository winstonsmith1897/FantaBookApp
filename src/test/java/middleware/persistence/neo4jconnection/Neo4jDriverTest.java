package middleware.persistence.neo4jconnection;

import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.persistence.neo4jconnection.Neo4jDriver;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class Neo4jDriverTest {
    private static final Neo4jDriver instance = new Neo4jDriver();
    private final Driver driver;

    private final String uri = "neo4j://172.16.4.225:7687";
    private final String user = "neo4j";
    private final String password = "root";

    private Neo4jDriverTest() {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public static Neo4jDriver getInstance() { return instance; }
    @Test
    public void getDriver() {
        driver.verifyConnectivity();
    }
    @Test
    public void closeDriver() { driver.close(); }
}