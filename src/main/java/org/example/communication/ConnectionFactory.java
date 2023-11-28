package org.example.communication;

import lombok.Data;
import lombok.Getter;
import org.example.util.PropertyFileUtil;

import java.io.IOException;
import java.net.Socket;

@Data
public class ConnectionFactory {
    private final String serverAddress;
    private final int portNumber;
    @Getter
    private static final ConnectionFactory instance = new ConnectionFactory();

    private ConnectionFactory() {
        this.serverAddress = PropertyFileUtil.getServerAddress();
        this.portNumber = PropertyFileUtil.getServerPort();
    }

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
