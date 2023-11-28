package org.example.communication;

import lombok.Data;
import lombok.Getter;
import org.example.util.PropertyFileUtil;

import java.io.IOException;
import java.net.Socket;

/**
 * Singleton class that facilitates connection to server and creation of client socket.
 */
@Data
public class ConnectionFactory {
    /**
     * host server address
     */
    private final String serverAddress;
    /**
     * server socket port
     */
    private final int portNumber;
    /**
     * singleton pattern instance (contains getter)
     */
    @Getter
    private static final ConnectionFactory instance = new ConnectionFactory();

    /**
     * Private constructor. Initializes fields serverAddress and portNumber
     * with values read from property file.
     */
    private ConnectionFactory() {
        this.serverAddress = PropertyFileUtil.getServerAddress();
        this.portNumber = PropertyFileUtil.getServerPort();
    }

    /**
     * Establishes connection to server with serverAddress and portNumber specified
     * in property file (through attribute initialization via constructor),
     * @return client Socket
     * @throws IOException if connection to server cannot be made
     */
    public Socket establishConnection() throws IOException{
        if(this.serverAddress == null || this.serverAddress.isBlank()){
            throw new IllegalArgumentException("Your address may not be blank.");
        }

        if(this.portNumber < 0 || this.portNumber> 65535){
            throw new IllegalArgumentException("Your port number must be from" +
                    "0 to 65535 (preferably greater than 1023).");
        }

        return new Socket(this.serverAddress, portNumber);
    }

}
