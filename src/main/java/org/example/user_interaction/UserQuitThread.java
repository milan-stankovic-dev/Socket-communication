package org.example.user_interaction;

import lombok.Getter;

import java.io.IOException;
import java.util.logging.Logger;

public class UserQuitThread extends Thread{
    @Getter
    private volatile boolean toQuit = false;
    private final Logger logger = Logger.getLogger(UserQuitThread.currentThread().getName());

    public UserQuitThread(){
        setDaemon(true);
    }
    @Override
    public void run() {
      logger.info("""
              ************************************
               ENTER IN THE Q KEY TO QUIT THE APP
              ************************************
              """);
      while(!this.toQuit) {
          try {
              int result = System.in.read();
              if (result == 'Q' ||
                      result == 'q') {
                  logger.info("Quitting...");
                  this.toQuit = true;
              }
          } catch (IOException e) {
              logger.severe("User input not instantiated " +
                      "properly.");
          }
       }
    }
}
