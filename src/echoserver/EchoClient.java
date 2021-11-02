package echoserver;
import java.net.*;
import java.io.*;

public class EchoClient {
    public static final int portNumber = 6013;

    public static void main(String[] args) throws IOException {
      EchoClient client = new EchoClient();
      client.start();
    }

    private void start() throws IOException {
      final String server = "localhost";
  
      try {
        // Connect to the server
        Socket serverSocket = new Socket(server, portNumber);
  
        // Get the input stream so we can read from that socket
        InputStream inStream = serverSocket.getInputStream();

        // Get the output stream so we can write from that socket
        OutputStream outStream = serverSocket.getOutputStream();

        // Initialize the runnable writer drawing from the input
        WriteFromInputThread inputWriter = new WriteFromInputThread();

        // Creates a thread for the input, using the input stream from the socket
        Thread inputThread = new Thread(inputWriter);

        // Initialize the runnable writer drawing from the output
        WriteFromOutputThread outputWriter = new WriteFromOutputThread();

        // Creates a thread for the output, using the output stream from the socket
        Thread outputThread = new Thread(outputWriter);

        // Start both the input and output threads
        inputThread.start();
        outputThread.start();

        // Have each of the threads wait their turn before acting to avoid race conditions and deadlock
        // Throw exceptions if there are issues
        try {
          inputThread.join();
          outputThread.join();
        } catch (InterruptedException ie) {
          System.out.println("We've caught an interrupted exception: " + ie.getMessage());
        }

        // Initialize the variable to contain bytes sent to the server.
        int sentByte = System.in.read();

        // Facilitate the writing and re-sending of the bytes sent to the server back
        // to the client.
        while((sentByte) != -1) {
          // Write the byte taken in to the output stream for the socket.
          outStream.write(sentByte);
            
          // Read a byte from the input stream and write it to the system's output.
          int receivedByte = inStream.read();
          System.out.write(receivedByte);

          // Get the next byte being sent by the client.
          sentByte = System.in.read();
        }

        //Flush the stream for the system and the server socket outputs.
        System.out.flush();
        outStream.flush();
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

    public class WriteFromInputThread implements Runnable {
      public void run() {
        //
      }
    }

    public class WriteFromOutputThread implements Runnable {
      public void run() {
        //
      }
    }
}
