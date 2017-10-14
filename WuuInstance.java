import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public class WuuInstance {
	ServerSocket socket;
	Integer port;
	ArrayList<Socket> clients; //sends
	ArrayList<Socket> hosts; //receives

	String username; 
 
	ArrayList<ArrayList<Integer>> tsMatrix;
	ArrayList<EventRecord> log;

	Dictionary<Integer, ArrayList<Integer>> blockList;
	
	public WuuInstance(Integer portNumber, String name) {
		username = name;
		port = portNumber;		
		clients = new ArrayList<Socket>();
		hosts = new ArrayList<Socket>();

		//TODO: Don't hard code size of array
		tsMatrix = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < 3; i++) {
			ArrayList<Integer> timevec = new ArrayList<Integer>();
			for (int j = 0; j < 3; j++) {
				timevec.add(0);
			}
			tsMatrix.add(timevec);
		}
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


				//TODO: Only call when necessary
				sendMessage();



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

	public ArrayList<EventRecord> getLogDiff(int proc) { //TODO
		return new ArrayList<EventRecord>();
	}

	public void sendMessage() { //TODO:ESU
		

		for (int i = 0; i < hosts.size(); i++) {
			Message message = new Message(getLogDiff(i), tsMatrix);
			byte[] byteMessage = message.toBytes();
			try {
				DataOutputStream dOut = new DataOutputStream(hosts.get(i).getOutputStream());
				dOut.writeInt(byteMessage.length);
				dOut.write(byteMessage);
			}
			catch (IOException e) {

			}
		}

	}
	
	public void receiveMessages() {
		System.out.println("# Clients: " + clients.size());
		System.out.println("# Hosts: " + hosts.size());
		for (int i = 0; i < clients.size(); i++) {
			try {
				if (clients.get(i) != null) {
					DataInputStream dIn = new DataInputStream(clients.get(i).getInputStream());
					
					if (dIn.available() != 0) {
						int length = dIn.readInt();
						byte[] byteMessage = new byte[length];
						dIn.readFully(byteMessage, 0, byteMessage.length);

						Message message = Message.fromBytes(byteMessage);
						message.printMessage();
						//TODO: Do something with message, now that it's received
					}
					//PrintWriter sendToClient = new PrintWriter(clients.get(i).getOutputStream(), true);                   
					//BufferedReader receiveFromClient = new BufferedReader(new InputStreamReader(clients.get(i).getInputStream()));
					//System.out.println("Received communication from client. Port " + clients.get(i).getLocalPort());

					//String line = receiveFromClient.readLine();
					//if (line != null) {
					//	System.out.println(line);
					//}
				}
			}
			catch (Exception e) {
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
				//PrintWriter sendToHost = new PrintWriter(hostSocket.getOutputStream(), true);
				//BufferedReader receiveFromHost = new BufferedReader(new InputStreamReader(hostSocket.getInputStream()));
				//sendToHost.println("Client port " + port + " connected to host " + hostPort + ".");
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