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

      // Creates an intermediary agent for working between the server and the client, while gathering the client's socket
      Intermediary clientServerIntermediary = new Intermediary(clientSocket);

      // Once the intermediary is made, a thread is created for the client. Then, it is executed.
      Thread intermediaryThread = new Thread(clientServerIntermediary);
      clientThreads.execute(intermediaryThread);
    }
  }

  public class Intermediary implements Runnable {
    // These fields consist of the socket and streams that the client will use for
    // working with the server
    Socket workSocket;
    InputStream workInStream;
    OutputStream workOutStream;

    // Create the client thread using the socket; the streams will be created for it
    // later in the run method due to the use of get methods
    public Intermediary(Socket sockWorkSocket) {
      this.workSocket = sockWorkSocket;
    }

    public void run() {
      try {
        // Get the input from the socket
        this.workInStream = workSocket.getInputStream();

        // Get the output from the socket
        this.workOutStream = workSocket.getOutputStream();

        // Get the first byte sent by client
        int sentByte = workInStream.read();

        while((sentByte) != -1) {
          // Write the byte taken in to the output stream for the socket.
          System.out.write(sentByte);
          // Read a byte from the input stream and write it to the system's output.
          sentByte = workInStream.read();
        }
        
        // Flush the output stream.
        System.out.flush();

        // Shutdown the output to the socket.
        workSocket.shutdownOutput();

      } catch(IOException inOut) {
        System.out.println("There's an issue with the input/output: " + inOut);
      }
    }
  }
}
