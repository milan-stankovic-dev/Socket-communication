package org.example;

import org.example.communication.PacketReader;
import org.example.communication.ThreadHandler;
import org.example.config.LoggerConfig;

import java.io.IOException;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger =
            Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        try {
            LoggerConfig.configureLogger();
            final PacketReader reader = new PacketReader();

            while(true) {
                final var packet = reader
                        .readPacketFully();
                ThreadHandler.manageWriterThreads(reader.getOutputStream(),packet);
            }
        } catch (IOException e) {
            logger.severe("IOException in main: " + e.getMessage());
        }
    }


}