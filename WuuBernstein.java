import java.io.*;
import java.net.*;

public class WuuBernstein {
	public static void main(String args[]) {
		Integer port = Integer.parseInt(args[0]);
		WuuInstance wu = new WuuInstance(port);
		
		try (BufferedReader br = new BufferedReader(new FileReader("instances.config")) ) {
			String[] line;
			
			while ((line = br.readLine().split(" ")).equals(null)) {
				if (line[0] != wu.getHostName()) {
					//Host name, host port
					Integer hostPort = Integer.parseInt(line[1]);
					wu.connectTo(wu.getHostName(), hostPort);
					wu.listen();
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}