import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ReadClients extends Thread {

    public void run() {
        System.out.println("Hello from a thread!");
    }

    public static void main(String args[]) {
        (new HelloThread()).start();
    }

}