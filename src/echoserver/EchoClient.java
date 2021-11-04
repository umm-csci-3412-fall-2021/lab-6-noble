package echoserver;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class EchoClient {
    public static final int portNumber = 6013;

    public static void main(String[] args) throws IOException {
      EchoClient client = new EchoClient();
      client.start();
    }

    private void start() throws IOException {
      final String server = "localhost";
      // Connect to the server
      Socket serverSocket = new Socket(server, portNumber);
  
      // Get the input stream so we can read from that socket
      InputStream inStream = serverSocket.getInputStream();

      // Get the output stream so we can write from that socket
      OutputStream outStream = serverSocket.getOutputStream();

      // Initialize the runnable writer drawing from the input
      WriteFromInputThread inputWriter = new WriteFromInputThread(serverSocket, outStream);

      // Creates a thread for the input, using the input stream from the socket
      Thread inputThread = new Thread(inputWriter);

      // Initialize the runnable writer drawing from the output
      WriteFromOutputThread outputWriter = new WriteFromOutputThread(serverSocket, inStream);

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
        
      // Once all the work is completed, close the server socket
      serverSocket.close();
    }

    public class WriteFromInputThread implements Runnable {
      Socket workSocket;
      OutputStream workOutStream;

      public WriteFromInputThread(Socket workSocket, OutputStream workOutStream) {
        this.workSocket = workSocket;
        this.workOutStream = workOutStream;
      }

      public void run() {
        try {
          // Initialize the variable to contain bytes sent to the server.
        int sentByte = System.in.read();

        // Facilitate the writing and re-sending of the bytes sent to the server back
        // to the client.
        while((sentByte) != -1) {
          // Write the byte taken in to the output stream for the socket.
          workOutStream.write(sentByte);

          // Get the next byte being sent by the client.
          sentByte = System.in.read();
        }

        workOutStream.flush();

        workSocket.shutdownInput();

        } catch (IOException ioe) {
          System.out.println("We caught an unexpected exception while reading the input:");
          System.err.println(ioe);
        }
      }
    }

    public class WriteFromOutputThread implements Runnable {
      Socket workSocket;
      InputStream workInStream;

      public WriteFromOutputThread(Socket workSocket, InputStream sockWorkInStream) {
        this.workSocket = workSocket;
        this.workInStream = sockWorkInStream;
      }
      public void run() {
          try {
            // Initialize the variable to contain bytes sent to the server.
          int receivedByte = workInStream.read();
  
          // Facilitate the writing and re-sending of the bytes sent to the server back
          // to the client.
          while((receivedByte) != -1) {
            // Write the byte taken in to the output stream for the socket.
              
            // Read a byte from the input stream and write it to the system's output.
            System.out.write(receivedByte);
            receivedByte = workInStream.read();
          }

          System.out.flush();

          workSocket.shutdownInput();

          } catch (IOException ioe) {
            System.out.println("We caught an unexpected exception while reading the input:");
            System.err.println(ioe);
          }
      }
    }
}
