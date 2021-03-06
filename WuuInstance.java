import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.util.concurrent.*;

public class WuuInstance {
	ServerSocket socket;
	Integer port;
	Boolean listening;
	Boolean newCommand;

	HashMap<String, Integer> addressBook;

	//ArrayList<Socket> clients; //sends
	//ArrayList<DataInputStream> inStreams;
	//ArrayList<Socket> hosts; //receives
	//ArrayList<DataOutputStream> outStreams;
	Socket[] clients; //sends
	DataInputStream[] inStreams;
	Socket[] hosts; //receives
	DataOutputStream[] outStreams;

	Integer id;
	String username; 
 
	ArrayList<ArrayList<Integer>> tsMatrix;
	ArrayList<EventRecord> log;

	Dictionary<Integer, ArrayList<Integer>> blockList;
	
	ExecutorService threadPool;
	
	public WuuInstance(Integer portNumber, String name, Integer n) {
		username = name;
		port = portNumber;	

		addressBook = new HashMap<String, Integer>();	

		clients = new Socket[n];
		inStreams = new DataInputStream[n];
		hosts = new Socket[n];
		outStreams = new DataOutputStream[n];

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

	public void setAddressBook(HashMap<String, Integer> adb) {
		addressBook = adb;
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
					Integer clientID = addressBook.get(clientSocket.getInetAddress().getHostName());
					clients[clientID] = clientSocket;
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
		

		for (int i = 0; i < hosts.length; i++) {
			if (hosts[i] == null) { continue; }
			Message message = new Message(getLogDiff(i), tsMatrix, id);
			byte[] byteMessage = message.toBytes();
			try {
				outStreams[i].writeInt(byteMessage.length);
				outStreams[i].write(byteMessage);
			}
			catch (IOException e) {

			}
		}

	}
	
	public void receiveMessages() {
		for (int i = 0; i < clients.length; i++) {
			try {
				if (clients[i] != null) {
					inStreams[i] = new DataInputStream(clients[i].getInputStream());
					
					if (dIn.available() != 0) {
						int length = dIn.readInt();
						byte[] byteMessage = new byte[length];
						dIn.readFully(byteMessage, 0, byteMessage.length);

						Message message = Message.fromBytes(byteMessage);
						message.printMessage();
						updateMatrix(message.log, message.id, message.tsMatrix);
						//TODO: Do something with message, now that it's received
					}

				}
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void updateMatrix(ArrayList<EventRecord> clientLog, Integer clientid, ArrayList<ArrayList<Integer>> clientMatrix) {
		for (int i = 0; i < tsMatrix.size(); i++) {
			ArrayList<Integer> row = tsMatrix.get(id);
			row.set(i, Math.max(tsMatrix.get(id).get(i), clientMatrix.get(clientid).get(i) ) );
			tsMatrix.set(id, row);
		}
		for (int i = 0; i < tsMatrix.size(); i++) {
			for (int j = 0; j < tsMatrix.size(); j++) {
				ArrayList<Integer> row = tsMatrix.get(i);
				row.set(j, Math.max(tsMatrix.get(i).get(j), clientMatrix.get(i).get(j) ) );
				tsMatrix.set(id, row);
			}
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
	
	public void connectTo(String host, Integer hostPort, Integer hostID) {
		System.out.println("Connecting to host " + host);
		try (
			Socket hostSocket = new Socket(host, hostPort);
			Socket clientSocket = new Socket();
		) {
			if (hostSocket != null) {
				hostSocket.setKeepAlive(true);
				//PrintWriter sendToHost = new PrintWriter(hostSocket.getOutputStream(), true);
				//BufferedReader receiveFromHost = new BufferedReader(new InputStreamReader(hostSocket.getInputStream()));
				//sendToHost.println("Client port " + port + " connected to host " + hostPort + ".");
				hosts[hostID] = hostSocket;
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