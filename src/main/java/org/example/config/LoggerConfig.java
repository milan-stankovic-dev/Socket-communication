package org.example.config;

import java.io.IOException;
import java.util.logging.*;

public class LoggerConfig {
    public static Logger configureLogger(String loggerName)  {
        final Logger customLogger = Logger.getLogger(loggerName);
        customLogger.setLevel(Level.ALL);

        final Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        customLogger.addHandler(consoleHandler);

        FileHandler fileHandler = null;
        try {

            fileHandler = new FileHandler("logfile.log", true);
            fileHandler.setLevel(Level.ALL);
            customLogger.addHandler(fileHandler);

        } catch (IOException e) {
            Logger.getAnonymousLogger().severe("Could not create " +
                    "logger (logfile not accessible.).");
        }

        return customLogger;
    }
}
