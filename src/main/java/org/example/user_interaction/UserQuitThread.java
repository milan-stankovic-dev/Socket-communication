package org.example.user_interaction;

import lombok.Getter;

import java.io.IOException;
import java.util.logging.Logger;

public class UserQuitThread extends Thread{
    @Getter
    private volatile boolean shouldQuit = false;
    private final Logger logger = Logger.getLogger(
            UserQuitThread.currentThread().getName()
    );
    public UserQuitThread(){
        setDaemon(true);
        start();
    }

    @Override
    public void run() {
      logger.info("""
              ***********************************
                PRESS THE Q KEY TO QUIT THE APP
              ***********************************
              """);
      while(!this.shouldQuit) {
          try {
              int result = System.in.read();
              if (result == 'Q' ||
                      result == 'q') {
                  logger.info("Quitting...");
                  this.shouldQuit = true;
              }
          } catch (IOException e) {
              logger.severe("User input not instantiated " +
                      "properly.");
          }
       }
    }
}
