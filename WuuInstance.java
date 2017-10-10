import java.io.*;
import java.net.*;

public class EchoClient {
	public static void main(String args[]) {
		serverPort = Integer.parseInt(args[0]);
		
	}
		
	public void serverSocket(Integer port) throws IOException {
		if (args.length != 1) {
			System.err.println("Usage: java EchoServer <port number>");
			System.exit(1);
		}
		int portNumber = port;
		try (
			ServerSocket serverSocket =
			new ServerSocket(port);
			Socket clientSocket = serverSocket.accept();     
			PrintWriter out =
				new PrintWriter(clientSocket.getOutputStream(), true);                   
			BufferedReader in = new BufferedReader(
				new InputStreamReader(clientSocket.getInputStream()));
			) {
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					out.println(inputLine);
				}
			} catch (IOException e) {
				System.out.println("Exception caught when trying to listen on port "
					+ portNumber + " or listening for a connection");
				System.out.println(e.getMessage());
			}
		}

	public void clientSocket(Integer serverName, Integer serverPort) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: java EchoClient <host name> <port number>");
			System.exit(1);
		}

		try (
			Socket echoSocket = new Socket(serverName, serverPort);
			PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader( new InputStreamReader(echoSocket.getInputStream()));
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			) {
				String userInput;
				while ((userInput = stdIn.readLine()) != null) {
					out.println(userInput);
					System.out.println("echo: " + in.readLine());
				}
			} catch (UnknownHostException e) {
				System.err.println("Don't know about host " + serverName;
				System.exit(1);
			} catch (IOException e) {
				System.err.println("Couldn't get I/O for the connection to " +
                serverPort);
				System.exit(1);
			}
		} 
	}
}