import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.util.concurrent.*;

public class WuuInstance {
	ServerSocket socket;
	Integer port;
	Boolean listening;
	ArrayList<Socket> clients; //sends
	ArrayList<Socket> hosts; //receives
	Integer id;
	String username; 
 
	ArrayList<ArrayList<Integer>> tsMatrix;
	ArrayList<EventRecord> log;

	Dictionary<Integer, ArrayList<Integer>> blockList;
	
	ExecutorService threadPool;
	
	public WuuInstance(Integer portNumber, String name, Integer n) {
		username = name;
		port = portNumber;		
		clients = new ArrayList<Socket>();
		hosts = new ArrayList<Socket>();
		threadPool = Executors.newCachedThreadPool();
		listening = false;
		//TODO: Don't hard code size of array
		tsMatrix = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < n; i++) {
			ArrayList<Integer> timevec = new ArrayList<Integer>();
			for (int j = 0; j < n; j++) {
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
		Socket clientSocket = null;
		System.out.println("Listening on port " + port);
		try (
			ServerSocket serverSocket = new ServerSocket(port);
		) {					
			socket = serverSocket;
			
			while (true) {


				//TODO: Only call when necessary
				sendMessage();



				receiveMessages();
				if (listening == false) {
					clientSocket = acceptConnect();
					if (clientSocket == null) {
						listening = true;
					}
					else {
						listening = false;
					}
				}
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
	
	public Socket acceptConnect() throws IOException {
        // create an open ended thread-pool
                // wait for a client to connect
				try {
					Future<Socket> result = threadPool.submit(new AcceptClients(socket));
					return result.get(10, TimeUnit.MILLISECONDS);
				} catch (Exception e) {
				
				}
				return null;
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

				}
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public ArrayList<ArrayList<Integer>> updateMatrix(ArrayList<EventRecord> clientLog, Integer clientid, ArrayList<ArrayList<Integer>> clientMatrix) {
		for (i = 0; i < tsMatrix.size(); i++) {
			tsMatrix[id][i] = Math.max(tsMatrix[id][i], clientMatrix[clientid][i]);
		}
		for (i = 0; i < tsMatrix.size(); i++) {
			for (j = 0; j < tsMatrix.size(); j++) {
				tsMatrix[i][j] = Math.max(tsMatrix[i][j], clientMatrix[i][j])
			}
		}
		
		if (tsMatrix.size() <= 0) {
			return;
		}
		for (int i = 0; i < tsMatrix.size(); i++) {
			System.out.print("[\t");
			for (int j = 0; j < tsMatrix.get(i).size(); j++) {
				System.out.print(tsMatrix.get(i).get(j) + "\t");
			}
			System.out.print("]\n");
		}
		System.out.println();
		for (int i = 0; i < log.size(); i++) {
			System.out.println("Event");
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
	
	public static class AcceptClients implements Callable<Socket> {
		ServerSocket server;

		public AcceptClients(ServerSocket s) {
			server = s;
		}
		
		@Override
	    public Socket call() throws Exception {
				try {
					return server.accept();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
				return null;
	    }
		

	}
}