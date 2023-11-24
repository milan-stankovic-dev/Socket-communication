package org.example.communication;

import lombok.Data;
import org.example.util.PropertyFileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@Data
public class MyConnectionFactory {
    private final String serverAddress;
    private final int portNumber;
    private static MyConnectionFactory instance;

    private MyConnectionFactory() {
        serverAddress = PropertyFileUtil.getServerAddress();
        portNumber = PropertyFileUtil.getServerPort();
    }

    public static MyConnectionFactory getInstance(){
        return instance != null ? instance : new MyConnectionFactory();
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
