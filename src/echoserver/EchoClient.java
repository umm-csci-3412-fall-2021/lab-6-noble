package echoserver;
import java.net.*;
import java.io.*;

public class EchoClient {
    public static final int portNumber = 6013;

    public static void main(String[] args) throws IOException {
      String server;
      // Use "127.0.0.1", i.e., localhost, if no server is specified. Otherwise,
      // use the server specified by the client.
      if (args.length == 0) {
        server = "127.0.0.1";
      } else {
        server = args[0];
      }
  
      try {
        // Connect to the server
        Socket serverSocket = new Socket(server, portNumber);
  
        // Get the input stream so we can read from that socket
        InputStream input = serverSocket.getInputStream();

        // Get the output stream so we can write from that socket
        OutputStream output = serverSocket.getOutputStream();
  
        // Initialize the variable to contain bytes sent to the server.
        int sentByte = System.in.read();

        // Facilitate the writing and re-sending of the bytes sent to the server back
        // to the client.
        while((sentByte) != -1) {
          // Write the byte taken in to the output stream for the socket.
          output.write(sentByte);
            
          // Read a byte from the input stream and write it to the system's output.
          int receivedByte = input.read();
          System.out.write(receivedByte);

          // Get the next byte being sent by the client.
          sentByte = System.in.read();
        }

        //Flush the stream for the system and the server socket outputs.
        System.out.flush();
        output.flush();
        // Shutdown the socket's output.
        serverSocket.shutdownOutput();
        // Close the socket.
        serverSocket.close();
  
      // Provide some minimal error handling.
      } catch (ConnectException ce) {
        System.out.println("We were unable to connect to " + server + ".");
        System.out.println("You should make sure the server is running.");
      } catch (IOException ioe) {
        System.out.println("We caught an unexpected exception:");
        System.err.println(ioe);
      }
    }
}