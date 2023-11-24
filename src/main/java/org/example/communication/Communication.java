package org.example.communication;

import lombok.Data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@Data
public class Communication {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public void establishConnection(String address, int portNumber) throws IOException {
        if(address == null || address.isBlank()){
            throw new RuntimeException("Your address may not be blank.");
        }
        if(portNumber < 0 || portNumber> 65535){
            throw new RuntimeException("Your port number must be from" +
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
            System.out.println(String.valueOf(bytesRead));
        }
//        return String.valueOf(bytesRead);
    }

}
