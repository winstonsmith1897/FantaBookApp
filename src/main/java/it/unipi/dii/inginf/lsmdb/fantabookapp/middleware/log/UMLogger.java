package it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.log;

import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.dao.PlayerDAOImpl;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.dao.SquadDAOImpl;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.dao.UserDAOImpl;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class UMLogger {


    private static final String LOG_FILE = "log4j.properties";

    private static final Logger userLogger = Logger.getLogger(UserDAOImpl.class);
    private static final Logger playerLogger = Logger.getLogger(PlayerDAOImpl.class);
    private static final Logger squadLogger = Logger.getLogger(SquadDAOImpl.class);

    private UMLogger() {
        try {
            Properties loggerProperties = new Properties();
            loggerProperties.load(new FileReader(LOG_FILE));
            PropertyConfigurator.configure(loggerProperties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Logger getUserLogger(){
        return userLogger;
    }

    public static Logger getPlayerLogger(){
        return playerLogger;
    }

    public static Logger getSquadLogger(){
        return squadLogger;
    }
}