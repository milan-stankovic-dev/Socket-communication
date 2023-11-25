package org.example;

import org.example.communication.ProcessPacketThread;

public class Main {
    public static void main(String[] args) {
        new ProcessPacketThread().start();
    }
}