package org.example;

//import org.example.communication.Communication;
import org.example.communication.MyConnectionFactory;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        var communication = MyConnectionFactory.getInstance();
        try {
            communication.establishConnection("hermes.plusplus.rs", 4000);
            communication.getServerPackage("hermes.plusplus.rs", 4000);
        } catch (IOException e) {
            System.out.println("Error occurred while establishing connection.");
            System.out.println(e.getMessage());
        }
    }
}