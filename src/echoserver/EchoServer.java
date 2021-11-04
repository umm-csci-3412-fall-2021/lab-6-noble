package echoserver;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
// These imports in particular are used for the thread pool service. 
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {
  public static final int portNumber = 6013;

  public static void main(String[] args) throws IOException {
    EchoServer server = new EchoServer();
    server.start();
  }

  private void start() throws IOException {
    // Initialized a cached thread pool for running the service; this will
    // provide the greatest leniency for allowing multiple clients to connect
    ExecutorService clientThreads = Executors.newCachedThreadPool();

    // Start listening on the specified port
    ServerSocket serverSocket = new ServerSocket(portNumber);

    // Run forever, which is common for server style services
    while (true) {
      // Wait until someone connects, thereby requesting a date
      Socket clientSocket = serverSocket.accept();
      System.out.println("Got a request!");

      // Get the input stream from the client's socket so that we can read from it
      InputStream input = clientSocket.getInputStream();

      // Get the output stream of the client's socket so that we can write to it
      OutputStream output = clientSocket.getOutputStream();

      // A variable for storing the bytes sent
      int sentByte;

      // Facilitate the writing of the bytes sent and received to the client's output
      // stream
      while ((sentByte = input.read()) != -1) {
        // Write a byte to the output
        output.write(sentByte);
        // Flush the output after each write.
        output.flush();
      }

      System.out.println("The request has been processed.");
      // Shutdown the output of the client socket so that no more bytes can be written
      // to it
      clientSocket.shutdownOutput();
      // Close the client socket completely
      clientSocket.close();
    }
  }
}
