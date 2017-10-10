import java.io.*;
import java.net.*;

public class WuuInstance {
	ServerSocket socket;
	Socket clients;
	PrintWriter sendToClient;
	BufferedReader receiveFromClient;
	
	public void WuuInstance(Integer port) throws IOException {
		int portNumber = port;
		try (
			ServerSocket serverSocket = new ServerSocket(port);
		) {
			socket = serverSocket;
		} catch (IOException e) {
			System.out.printl("Exception caught while attempting to listen on port " + port);
			System.out.println(e.getMessage());
		}
		
	}
	public listen() {
		try (
			Socket clientSocket = serverSocket.accept();     
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);                   
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			) {
				clients = clientSocket;
				sendToClient = out;
				receiveFromClient = in;
				
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					out.println(inputLine);
				}
			} catch (IOException e) {
				System.out.println("Exception caught listening for a connection to server on port " + port);
				System.out.println(e.getMessage());
			}
		}
	
}