package org.example.user_interaction;

import lombok.Getter;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Runs only while user does not input a 'q' or a 'Q'. Is a thread class.
 */
public class UserQuitThread extends Thread{
    /**
     * signal attribute for quitting (contains getter)
     */
    @Getter
    private volatile boolean toQuit = false;
    /**
     * logger instance
     */
    private final Logger logger = Logger.getLogger(UserQuitThread.currentThread().getName());

    /**
     * public Constructor. Sets deamon to true to prevent lingering threads.
     */
    public UserQuitThread(){
        setDaemon(true);
    }

    /**
     * run method for thread. Runs while user does not input a 'q' or a 'Q'
     */
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
