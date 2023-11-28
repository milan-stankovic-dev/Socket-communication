package org.example.config;

import java.io.IOException;
import java.util.logging.*;

/**
 * Global config class for logger
 */
public class LoggerConfig {
    /**
     * Configures logger for app. Sets logging level to ALL, includes file logging.
     * @param loggerName name for logger
     * @return configured global logger
     */
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
