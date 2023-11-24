package org.example;

//import org.example.communication.Communication;
import org.example.communication.MyConnectionFactory;
import org.example.communication.ProcessPacketThread;

import java.io.IOException;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        new ProcessPacketThread().start();
    }
}