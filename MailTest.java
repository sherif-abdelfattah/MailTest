import java.net.*;
import java.io.*;
import java.util.*;
import javax.net.ssl.*;

public class MailTest {

    public static void main(String[] args) throws Exception {
	String host = args[0];
	int port = Integer.parseInt(args[1]);
	System.out.println("Connecting to "+host+":"+port);
	Socket socket = new Socket(host,port);
	System.out.println(socket.toString());
    	BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    	BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    	String line;
	boolean hasTLS=false;
	writer.write("EHLO "+host+"\r\n");
    	writer.flush(); 
	System.out.println("we send:"+"EHLO "+host+"\r\n");
    	while ((line = reader.readLine()) != null) {
		System.out.println("We got:"+line);
        	if (line.contains("STARTTLS")) {
			hasTLS=true;
			writer.write("STARTTLS\r\n");
			writer.flush();
			writer.flush();
			System.out.println("we send:"+"STARTTLS\r\n");
		}
	}
	if (hasTLS) {
		writer.flush();
		while ((line = reader.readLine()) != null) {
                	System.out.println("We got:"+line);
                }
		SSLSocket sslSocket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(
                       socket, 
                       socket.getInetAddress().getHostAddress(), 
                       socket.getPort(), 
                       true);
		InputStream inputStream = sslSocket.getInputStream();
		OutputStream outputStream = sslSocket.getOutputStream();
		// reads from the socket
		Scanner scanner = new Scanner(inputStream);
		// writes to the socket
		OutputStream bufferedout = new BufferedOutputStream(outputStream);
		System.out.println(sslSocket.toString());

        }
    	
     }
}
