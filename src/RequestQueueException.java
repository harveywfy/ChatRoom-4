/**
 * Created by AlexY on 2016/5/28.
 */
public class RequestQueueException extends Exception {


    public RequestQueueException(String message) {
        super("RequestQueueException: "+ message);
    }
}
