import java.io.*;
import java.net.*;

public class WuuBernstein {
	public static void main(String args[]) {
		WuuInstance wu = new WuuInstance(args[0]);
		
		try (BufferedReader br = new BufferedReader(new FileReader("instances.config")) ) {
			String[] line;
			
			while (line = br.readLine().split(" ") != null) {
				InetAddress host = new InetAddress(Integer.parseInt(line[0]));
				if (host.getHostName() != wu.getHostName()) {
					//Host name, host port
					wu.connectTo(host.getHostName(), line[1]);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
}