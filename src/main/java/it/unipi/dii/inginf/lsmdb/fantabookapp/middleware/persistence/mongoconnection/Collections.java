package it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.persistence.mongoconnection;

public enum Collections {
    USERS("Users"),
    PLAYERS("Players");

    private String collName;
    Collections(String collName) {
        this.collName = collName;
    }

    public String toString() {
        return collName;
    }
}