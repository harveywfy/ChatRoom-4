/**
 * Created by AlexY on 2016/5/28.
 */
public interface RequestHandler
{

    /**
     * Handles the incoming request
     *
     * @param socket    The socket communication back to the client
     */
    public void handleRequest( java.net.Socket socket );
}