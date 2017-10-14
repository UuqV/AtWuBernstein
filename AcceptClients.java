import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.lang.*;


public class AcceptClients extends Thread {

    public Socket run(ServerSocket socket) {
			try {
				return socket.accept();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			return null;
    }
		
		public static void main(String args[]) {
			(new AcceptClients()).start();
		}

}