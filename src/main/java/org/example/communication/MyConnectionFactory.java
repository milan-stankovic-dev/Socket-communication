package org.example.communication;

import lombok.Data;
import org.example.util.PropertyFileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@Data
public class MyConnectionFactory {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private final String serverAddress;
    private final int portNumber;
    private static MyConnectionFactory instance;

    private MyConnectionFactory() {
        serverAddress = PropertyFileUtil.getServerAddress();
        portNumber = PropertyFileUtil.getServerPort();

        try {
            establishConnection(serverAddress, portNumber);
        } catch (IOException e) {
            System.out.println("Failed to create socket with given parameters." +
                    " Check if they are well defined and try again.");
            e.printStackTrace();
        }
    }

    public static MyConnectionFactory getInstance(){
        return instance != null ? instance :  new MyConnectionFactory();
    }

    public void establishConnection(String address, int portNumber) throws IOException{
        if(address == null || address.isBlank()){
            throw new IllegalArgumentException("Your address may not be blank.");
        }

        if(portNumber < 0 || portNumber> 65535){
            throw new IllegalArgumentException("Your port number must be from" +
                    "0 to 65535 (preferably greater than 1023).");
        }

        this.socket = new Socket(address, portNumber);
        this.inputStream = this.socket.getInputStream();
        this.outputStream = this.socket.getOutputStream();
    }

    public void getServerPackage(String address, int portNumber) throws IOException{
        if(socket == null || inputStream == null || outputStream == null){
            establishConnection(address, portNumber);
        }
        byte[] buffer = new byte[16];

        while(true) {
            int bytesRead = inputStream.read(buffer);
            System.out.println("package: " +
                    String.valueOf(bytesRead) +
                    new String(buffer, 0, bytesRead));
        }
//        return String.valueOf(bytesRead);
    }

}
