package middleware.persistence.mongoconnection;
import org.junit.jupiter.api.Test;

enum CollectionTest {
    USERS("Users"),
    SONGS("Players");

    private String collName;
    CollectionTest(String collName) {
        this.collName = collName;
    }
    @Test
    public String toString() {
        return collName;
    }
}
