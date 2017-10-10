import java.io.*;
import java.net.*;

public class WuuInstance {
	ServerSocket socket;
	int port;
	Socket clients;
	PrintWriter sendToClient;
	BufferedReader receiveFromClient;
	
	public void WuuInstance(Integer portNumber) throws IOException {
		port = portNumber;
		try (
			ServerSocket serverSocket = new ServerSocket(port);
		) {
			socket = serverSocket;
		} catch (IOException e) {
			System.out.println("Exception caught while attempting to listen on port " + port);
			System.out.println(e.getMessage());
		}
		
	}
	public void listen() {
		try (
			Socket clientSocket = socket.accept();     
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