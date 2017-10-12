import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class WuuInstance {
	ServerSocket socket;
	Integer port;
	ArrayList<Socket> clients;
	ArrayList<Socket> hosts;
	
	public WuuInstance(Integer portNumber) {
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
	
	public String getHostName() {
		return socket.getInetAddress().getHostName();
	}
	
	public void listen() {
		try (
			Socket clientSocket = socket.accept();     
			PrintWriter sendToClient = new PrintWriter(clientSocket.getOutputStream(), true);                   
			BufferedReader receiveFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			) {
				clients.add(clientSocket);
				
				sendToClient.println("Received communication from client.");
				System.out.println("\t " + receiveFromClient.readLine());
			} catch (IOException e) {
				System.out.println("Exception caught listening for a connection to server on port " + port);
				System.out.println(e.getMessage());
			}
	}
	
	public void connectTo(String host, Integer hostPort) {
		try (
			Socket hostSocket = new Socket(host, hostPort);
			PrintWriter sendToHost = new PrintWriter(hostSocket.getOutputStream(), true);
			BufferedReader receiveFromHost = new BufferedReader(new InputStreamReader(hostSocket.getInputStream()));
		) {
			sendToHost.println("Client port " + port + "connected to host " + hostPort + ".");
			hosts.add(hostSocket);
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + host);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + host);
			System.exit(1);
		}
	}
	
}