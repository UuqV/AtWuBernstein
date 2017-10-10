import java.io.*;
import java.net.*;

public class WuuBernstein {
	public static void main(String args[]) {
		WuuInstance wu1 = new WuuInstance(6969);
		WuuInstance wu2 = new WuuInstance(3210);
		wu2.connectTo(wu1.getHostName(), 6969);
		wu1.connectTo(wu2.getHostName(), 3210);
	}
}