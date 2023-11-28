package org.example;

import org.example.communication.CommunicationLogicHandler;
import org.example.communication.ConnectionFactory;
import org.example.config.LoggerConfig;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Main class for the project
 */
public class Main {
    /**
     * logger instance and configuration
     */
    private static final Logger logger = LoggerConfig.configureLogger(Main.class.getName());

    /**
     * main method for the project.
     * @param args optional command line argument
     */
    public static void main(String[] args) {
        try {
            final var handler = CommunicationLogicHandler.getInstance();
            final Socket socket = ConnectionFactory.getInstance().establishConnection();
            handler.handleCommunicationLogic(new DataOutputStream(socket.getOutputStream()));

            socket.close();
            logger.info("Socket closed. Main finished executing.");

        } catch (IOException e) {
            logger.severe("IOException in main: " +
                    e.getMessage());
        }
    }

}