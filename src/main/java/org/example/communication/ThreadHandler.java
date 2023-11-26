package org.example.communication;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;

public class ThreadHandler {

    public static void manageWriterThreads(DataOutputStream outputStream, byte[] packet) throws IOException{
        final PacketWriter writer = new PacketWriter(outputStream, packet);
        Executors.newCachedThreadPool().execute(writer);
    }
}
