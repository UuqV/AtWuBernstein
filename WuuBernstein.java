import java.io.*;
import java.net.*;

public class WuuBernstein {
	public static void main(String args[]) {
		Integer port = Integer.parseInt(args[0]);
		WuuInstance wu = new WuuInstance(port);
		
		try (BufferedReader br = new BufferedReader(new FileReader("instances.config")) ) {
			String line;
			//Read the config file
			while ( (line = br.readLine()) != null ) {
				String[] tokens = line.split(" ");
				InetAddress clientIP = InetAddress.getByName(tokens[0]);
				System.out.println("clientIP = " + clientIP);
				System.out.println("Local Host " + InetAddress.getLocalHost());
				if ( !clientIP.equals(InetAddress.getLocalHost())) {
					System.out.println("Will send message to " + clientIP);
					//Host name, host port
					Integer hostPort = Integer.parseInt(tokens[1]);
					wu.connectTo(clientIP.getHostAddress(), hostPort);
				}
			}
			wu.listen();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}