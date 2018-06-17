import java.net.*;
import java.io.*;
import java.util.*;
import javax.net.ssl.*;
import java.security.Security;

public class MailTest {

    public static void main(String[] args) throws Exception {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
	// Creating a plain text socket to start the connection
        System.out.println("Connecting to "+host+":"+port);
        Socket socket = new Socket(host,port);
        System.out.println(socket.toString());
	//Setting up reader and writer
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String line;
        boolean hasTLS=false;
	//Saying hello to mail server in plain text
        writer.write("EHLO "+host+"\r\n");
        writer.flush(); 
        System.out.println("we send:"+"EHLO "+host+"\r\n");
        while ((line = reader.readLine()) != null) {
                System.out.println("We got:"+line);
		//Checking if the mail server can work using TLS, if it can, we send starttls
                if (line.contains("STARTTLS")) {
                        hasTLS=true;
                        writer.write("STARTTLS\r\n");
                        writer.flush();
                        writer.flush();
                        System.out.println("we send:"+"STARTTLS\r\n");
                }
		//Verifying if the server is able to start talking to us in TLS
                if (line.contains("Ready to start TLS")|| (line.contains("220 2.0.0"))) {
                        break;
                        }
        }
        if (hasTLS) {
                System.out.println("Starting to use ssl socket");
		//Setting up an SSLSocket based on our standard socket.
		//We use default JVM SSL context, we need to impaort any certificats in cacerts as needed.
                Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
                SSLSocket sslSocket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(
                       socket, 
                       socket.getInetAddress().getHostAddress(), 
                       socket.getPort(), 
                       true);
		//We start the SSL handshake now.
                sslSocket.startHandshake();
                System.out.println(sslSocket.toString());
		//Setting up SSL socket reader and writer
                BufferedReader sreader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
                BufferedWriter swriter = new BufferedWriter(new OutputStreamWriter(sslSocket.getOutputStream()));
		//We send Hello to the smtp server in SSL
                swriter.write("EHLO "+host+"\r\n");
                swriter.flush();
                System.out.println(" ");
                System.out.println("SSL We sent: EHLO "+host+"\r\n");
                while ((line = sreader.readLine()) != null) {
                        System.out.println("SSL We got:"+line);
			//readline() hangs on getting the data from the stream.
			//We instead break if the sreader is no longer ready()
                        if (!sreader.ready()){
                                break;
                                }
                        }
		//Closesing the connection.
                System.out.println("Closing Connection");
                sslSocket.close();
        }
	socket.close();
    
     }
}
