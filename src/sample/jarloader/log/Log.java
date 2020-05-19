package sample.jarloader.log;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {

    private static final Logger logger = Logger.getLogger("logger");
    private static final Level defaultLevel = Level.SEVERE;

    public static void syserr(String msg) {
        if (logger.getHandlers().length == 0) {
            try {
                FileHandler fileHandler = new FileHandler("syserr.txt");
                SimpleFormatter simpleFormatter = new SimpleFormatter();
                fileHandler.setFormatter(simpleFormatter);
                logger.addHandler(fileHandler);
            } catch(IOException e) {
                logger.log(defaultLevel, "Error log handler.");
            }
        }

        logger.log(defaultLevel, msg);
    }

}
