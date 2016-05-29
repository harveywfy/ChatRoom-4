import java.net.*;

/**
 * A Request thread handles incoming requests
 */
public class RequestThread extends Thread
{
    /**
     * A reference to our request queue
     */
    private RequestQueue queue;

    /**
     * Our state: are we running or not?
     */
    private boolean running;

    /**
     * Our processing state: are we currently processing a request?
     */
    private boolean processing = false;

    /**
     * Our thread number, used for accounting purposes
     */
    private int threadNumber;

    /**
     * Our request handler
     */
    private RequestHandler requestHandler;

    /**
     * Creates a new Request Thread
     *
     * @param queue         The queue that we are associated with
     * @param threadNumber  Our thread number
     */
    public RequestThread( RequestQueue queue, int threadNumber, String requestHandlerClassName )
    {
        this.queue = queue;
        this.threadNumber = threadNumber;
        try
        {
            // Create our request handler
            this.requestHandler = ( RequestHandler )( Class.forName( requestHandlerClassName ).newInstance() );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    /**
     * Returns true if we are currently processing a request, false otherwise
     */
    public boolean isProcessing()
    {
        return this.processing;
    }

    /**
     * If a thread is waiting, then wake it up and tell it to die
     */
    public void killThread()
    {
        System.out.println( "[" + threadNumber + "]: Attempting to kill thread..." );
        this.running = false;
    }

    /**
     * The thread’s main processing loop
     */
    public void run()
    {
        this.running = true;
        while( running )
        {
            try
            {
                // Obtain the next pending socket from the queue; only process requests if 
                // we are still running. The shutdown mechanism will wake up our threads at this
                // point, so our state could have changed to not running here.
                Object o = queue.getNextObject();
                if( running )
                {
                    // Cast the object to a Socket
                    Socket socket = ( Socket )o;

                    // Mark ourselves as processing a request
                    this.processing = true;
                    System.out.println( "[" + threadNumber + "]: Processing request..." );

                    // Handle the request
                    this.requestHandler.handleRequest( socket );

                    // We’ve finished processing, so make ourselves available for the next request
                    this.processing = false;
                    System.out.println( "[" + threadNumber + "]: Finished Processing request..." );
                }
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }
        }

        System.out.println( "[" + threadNumber + "]: Thread shutting down..." );
    }
}