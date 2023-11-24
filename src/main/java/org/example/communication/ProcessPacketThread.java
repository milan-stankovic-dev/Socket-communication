package org.example.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ProcessPacketThread extends Thread{
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public ProcessPacketThread() {
        try {
            Socket socket = MyConnectionFactory.getInstance().establishConnection();
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Could not establish connection. Server address or port in property" +
                    "file may be invalid or outdated.");
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while(true){
            byte[] buffer = new byte[16];
            try {
                String packet = String.valueOf(inputStream.read(buffer));
                System.out.println(packet);
            } catch (IOException e) {
                System.out.println("Could not read packet properly.");
            }
        }
    }
}
