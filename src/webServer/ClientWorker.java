package webServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.text.FieldPosition;
import java.util.ArrayList;

import fileWorker.FileWorker;
import md5Executor.Md5Executor;
import observingInterfaces.Observer;
import threaded.Threaded;
import tuple.Tuple;

public class ClientWorker extends Threaded{

	public Socket currentClient;
	public BufferedWriter output;
	public BufferedReader input;
	private String endMessage = "END OF DATA";
	
	public ClientWorker(Socket socket) {
		currentClient = socket;
		try {
			this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} 
		catch (IOException e) {
			e.printStackTrace();
		}		
	}
		
	public void execute() {
		try {
			try {
				while (currentClient.isConnected()) {
					String request = "";
					StringBuilder requestBuilder = new StringBuilder();
					String line = input.readLine();
					while(!line.equals(endMessage)) {
						requestBuilder.append(line);
						line = input.readLine();
					}
					request = requestBuilder.toString();			
					if (request.equals("list")) {
						FileWorker fileWorker = new FileWorker("C:/Users/Александр/Desktop/Учеба/ООП/Task2/TestFolder");
						ArrayList<String> result = fileWorker.getAllFiles();
						StringBuilder responseBuilder = new StringBuilder();
						for (String name: result) {
							responseBuilder.append(name);
							responseBuilder.append("\r\n");
						}
						responseBuilder.append("\r\n");
						responseBuilder.append(endMessage);
						responseBuilder.append("\r\n");
						this.output.write(responseBuilder.toString());
					}
					else if (request.startsWith("hash")) {
						String path = request.split(" ")[1];
						FileWorker fileWorker = new FileWorker("C:/Users/Александр/Desktop/Учеба/ООП/Task2/TestFolder");
						Md5Executor md5 = new Md5Executor();
						Tuple<String, String> hash = fileWorker.execute(md5, path);
						output.write(hash.value2 + "\r\n" + endMessage + "\r\n");
					}
					else {
						output.write("Server hasn't this command" + "\r\n" + endMessage + "\r\n");
					}
					output.flush();	
				}
			}
			catch (SocketException e) {
				System.out.print("Connection Failed");
				}		
			finally {
				currentClient.shutdownInput();
				currentClient.shutdownOutput();
				input.close();
				output.close();
				currentClient.close();
			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}		
	}

	public void notifyObserver(Observer observer) {
		observer.handleEvent(Thread.currentThread().getId(), Thread.currentThread().getName());		
	}
	
}
