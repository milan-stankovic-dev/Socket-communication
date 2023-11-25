package org.example.communication;

import lombok.Getter;
import org.example.data_packet.impl.DummyPacket;
import org.example.util.ByteUtil;
import org.example.util.PropertyFileUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.net.Socket;
public class ProcessPacketThread extends Thread{
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private static final int dataSegmentBytes = PropertyFileUtil.getDataSegmentBytes();
    @Getter
    private byte[] packetToDeliver;


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
        new PacketSenderThread(outputStream, this).start();
        while (true) {
            try {
                final int packetId = readPacketId();
                final int dataLength = (packetId == 1) ? 12 : ((packetId == 2) ? 8 : -1);

                if (dataLength == -1) {
                    System.out.println("UNKNOWN PACKET TYPE (id =" + packetId + ")");
                    throw new InvalidObjectException("Packet type is unknown");
                }

                final byte[] packetBytes = new byte[dataLength];
                this.inputStream.readFully(packetBytes);

                System.out.println("PACKET ID: " + packetId);
                System.out.print("PACKET DATA: ");
                ByteUtil.printDataBits(packetBytes);
                this.packetToDeliver = packetBytes;

                if (packetId == 1) {
                    System.out.println("PACKET TYPE: Dummy");
                } else {
                    System.out.println("PACKET TYPE: Cancel");
                }
            } catch (IOException e) {
                System.out.println("Could not read packet properly.");
            }
        }
    }

    private int readPacketId() throws IOException {
        final byte[] idBytes = new byte[dataSegmentBytes];
        this.inputStream.readFully(idBytes);

        return ByteUtil.byteBlockAsIntLE(idBytes);
    }
}
