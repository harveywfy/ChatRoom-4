// Import the Java classes
import javax.net.ServerSocketFactory;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Abstract super class for creating servers
 * 创建server的抽象类
 */
public abstract class AbstractServer extends Thread
{
    /**
     * Server sock that will listen for incoming connections
     */
    protected ServerSocket serverSocket;

    /**
     * Boolean that controls whether or not this server is listening
     * 控制server是否监听
     */
    protected boolean running;

    /**
     * The port that this server is listening on
     * 服务器监听的端口
     */
    protected int port;

    /**
     * The number of requests to backlog if we are busy
     * 当服务器繁忙时，允许backlog连接的数目
     */
    protected int backlog;

    /**
     * A Request Queue used for high throughput servers
     * 服务器的请求队列
     */
    protected RequestQueue requestQueue;

    /**
     * Creates a new AbstractServer
     */
    public AbstractServer( int port,
                           int backlog,
                           String requestHandlerClassName,
                           int maxQueueLength,
                           int minThreads,
                           int maxThreads )
    {
        // Save our socket parameters
        this.port = port;
        this.backlog = backlog;

        // Create our request queue
        this.requestQueue = new RequestQueue( requestHandlerClassName,
                maxQueueLength,
                minThreads,
                maxThreads );
    }

    /**
     * Starts this server
     */
    public void startServer()
    {
        try
        {
            // Create our Server Socket
            ServerSocketFactory ssf = ServerSocketFactory.getDefault();
            serverSocket = ssf.createServerSocket( this.port, this.backlog );

            // Start our thread
            this.start();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    /**
     * Stops this server
     */
    public void stopServer()
    {
        try
        {
            this.running = false;
            this.serverSocket.close();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    /**
     * Body of the server: listens in a tight loop for incoming requests
     */
    public void run()
    {
        // Start the server
        System.out.println( "Server Started, listening on port: " + this.port );
        this.running = true;
        while( running )
        {
            try
            {
                // Accept the next connection
                Socket s = serverSocket.accept();

                // Log some debugging information
                InetAddress addr = s.getInetAddress();
                System.out.println( "Received a new connection from (" + addr.getHostAddress() + "): " + addr.getHostName() );

                // Add the socket to the new RequestQueue
                this.requestQueue.add( s );
            }
            catch( SocketException se )
            {
                // We are closing the ServerSocket in order to shutdown the server, so if
                // we are not currently running then ignore the exception.
                if( this.running )
                {
                    se.printStackTrace();
                }
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }
        }
        System.out.println( "Shutting down..." );

        // Shutdown our request queue
        this.requestQueue.shutdown();
    }
}