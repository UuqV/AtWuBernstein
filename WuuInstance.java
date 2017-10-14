import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.lang.*;

public class WuuInstance {
	ServerSocket socket;
	Integer port;
	ArrayList<Socket> clients; //sends
	ArrayList<Socket> hosts; //receives

	String username; 
 
	ArrayList<ArrayList<Integer>> tsMatrix;
	ArrayList<EventRecord> log;
	
	public WuuInstance(Integer portNumber, String name) {
		username = name;
		port = portNumber;		
		clients = new ArrayList<Socket>();
		hosts = new ArrayList<Socket>();
	}
	
	public String getHostName() {
		return socket.getInetAddress().getHostName();
	}

	public Boolean hasRecord(EventRecord eR, int k) {
		return false;
	}
	
	public void listen() {
		System.out.println("Listening on port " + port);
		try (
			ServerSocket serverSocket = new ServerSocket(port);
			) {					
				socket = serverSocket;
				
				
				AcceptClients accept = new AcceptClients();
				
				while (true) {
					receiveMessages();
					Socket clientSocket = accept.run(socket);
					if (clientSocket != null) {
						clients.add(clientSocket);
						clientSocket = null;
					}
				}
			} catch (IOException e) {
				System.out.println("Exception caught listening for a connection to server on port " + port);
				System.out.println(e.getMessage());
			}
	}

	public void sendMessage(ArrayList<EventRecord> log) { //TODO:ESU
		try {
			
		}
	}
	
	public void receiveMessages() {
		for (int i = 0; i < clients.size(); i++) {
			try {
				if (clients.get(i) != null) {
					PrintWriter sendToClient = new PrintWriter(clients.get(i).getOutputStream(), true);                   
					BufferedReader receiveFromClient = new BufferedReader(new InputStreamReader(clients.get(i).getInputStream()));
					System.out.println("Received communication from client. Port " + clients.get(i).getLocalPort());

					String line = receiveFromClient.readLine();
					if (line != null) {
						System.out.println(line);
					}
				}
			}
			catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void connectTo(String host, Integer hostPort) {
		System.out.println("Connecting to host " + host);
		try (
			Socket hostSocket = new Socket(host, hostPort);
		) {
			if (hostSocket != null) {
				hostSocket.setKeepAlive(true);
				PrintWriter sendToHost = new PrintWriter(hostSocket.getOutputStream(), true);
				BufferedReader receiveFromHost = new BufferedReader(new InputStreamReader(hostSocket.getInputStream()));
				sendToHost.println("Client port " + port + " connected to host " + hostPort + ".");
				hosts.add(hostSocket);
				System.out.println("Connected self to host " + host);
			}
			else {
				System.out.println("Message fell on deaf ears....");
			}
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + host);
		} catch (IOException e) {
			System.out.println("Message fell on deaf ears....");
		}
	}
	
}