import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class AcceptClients extends Thread {

    public void run(ServerSocket socket) {
			return socket.accept();
    }

}