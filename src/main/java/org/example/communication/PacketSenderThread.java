package org.example.communication;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class PacketSenderThread extends Thread{
    private final DataOutputStream outputStream;
    private final ProcessPacketThread parentThread;
    private byte[] lastDeliveredPacket;
    public PacketSenderThread(DataOutputStream outputStream,
                              ProcessPacketThread parentThread){
        super();
        this.outputStream = outputStream;
        this.parentThread = parentThread;
    }
    @Override
    public void run() {
        while(true){
            try {
                final var packetToDeliver = parentThread.getPacketToDeliver();
                if(packetToDeliver != null && (packetToDeliver!= lastDeliveredPacket)){
                   outputStream.write(packetToDeliver);
                    System.out.println("Packet " + Arrays.toString(packetToDeliver) + " successfully delivered!");
                }
                lastDeliveredPacket = packetToDeliver;
            } catch (IOException e) {
                System.out.println("Could not send packet to the server!");
                e.printStackTrace();
            }
        }
    }
}
