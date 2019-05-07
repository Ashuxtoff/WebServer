package webServer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.omg.CORBA.TIMEOUT;

import threadDispatcher.ThreadDispatcher;

public class WebServer {

	static int port = 8005;
	
	public static void main(String[] args) {
		ThreadDispatcher dispatcher = ThreadDispatcher.instance;
			try (ServerSocket server = new ServerSocket(port)){
				while (!server.isClosed()) {			
					Socket client = server.accept();
					dispatcher.add(new ClientWorker(client));
				}
			}
			catch (Exception e) {
				System.out.print(e.getMessage());
			}		
	}	
}