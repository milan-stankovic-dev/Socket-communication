package org.example.config;

import java.io.IOException;
import java.util.logging.*;

public class LoggerConfig {
    public static void configureLogger() throws IOException {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.ALL);

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        rootLogger.addHandler(consoleHandler);

        FileHandler fileHandler = new FileHandler("logfile.log");
        fileHandler.setLevel(Level.ALL);
        rootLogger.addHandler(fileHandler);
    }
}
