// Import the Java classes
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;



/**
 * A chat server enables users to connect and chat
 */
public class ChatServer extends AbstractServer
{
    /**
     * A static reference to the ChatServer
     */
    public static ChatServer theServer = null;

    /**
     * A Map of usernames to chat clients
     * 在线的用户
     */
    private Map chatClients = new TreeMap();

    /**
     * Creates a new chat server listening on port 9988
     */
    public ChatServer()
    {
        super( 9988, 50, "ChatRequestHandler", 2000, 5, 50 );
        theServer = this;
    }

    /**
     * Adds a new chat client
     * 添加一个新的客户
     */
    public void addChatClient( String name, ChatRequestHandler client ) throws DuplicateLoginException
    {
        if( this.chatClients.containsKey( name ) )
        {
            throw new DuplicateLoginException( name );
        }
        for( Iterator i=this.chatClients.keySet().iterator(); i.hasNext(); )
        {
            String username = ( String )i.next();
            System.out.println( "notifying user: " + username + " that a new user was added: " + name );
            ChatRequestHandler user = ( ChatRequestHandler )this.chatClients.get( username );
            user.newUser( name );
        }
        this.chatClients.put( name, client );
    }

    /**
     * Sends a message to all users
     * 发送消息给所有的用户
     */
    public void sendMessage( String user, String message )
    {
        for( Iterator i=this.chatClients.keySet().iterator(); i.hasNext(); )
        {
            String username = ( String )i.next();
            if( !username.equalsIgnoreCase( user ) )
            {
                ChatRequestHandler client = ( ChatRequestHandler )this.chatClients.get( username );
                client.sendMessage( user, message );
            }
        }
    }

    /**
     * Sends an emotion to all users
     *发送表情给所有的用户
     */
    public void sendEmotion( String user, String message )
    {
        for( Iterator i=this.chatClients.keySet().iterator(); i.hasNext(); )
        {
            String username = ( String )i.next();
            if( !username.equalsIgnoreCase( user ) )
            {
                ChatRequestHandler client = ( ChatRequestHandler )this.chatClients.get( username );
                client.sendEmotion( user, message );
            }
        }
    }


    /**
     * Sends a message to a specific user
     * 发送私有信心给指定用户
     */
    public void sendMessage( String from, String to, String message )
    {
        for( Iterator i=this.chatClients.keySet().iterator(); i.hasNext(); )
        {
            String username = ( String )i.next();
            if( username.equalsIgnoreCase( to ) )
            {
                ChatRequestHandler client = ( ChatRequestHandler )this.chatClients.get( username );
                client.privateMessage( from, message );
            }
        }
    }

    /**
     * Returns a Set containing the currently logged in users
     * 返回一个Set，包含所有登录的用户
     */
    public Set getUsers()
    {
        return this.chatClients.keySet();
    }

    /**
     * Removes the specified chat client from the chat
     */
    public void removeChatClient( String name )
    {
        if( name != null && this.chatClients.containsKey( name ) )
        {
            this.chatClients.remove( name );
            for( Iterator i=this.chatClients.keySet().iterator(); i.hasNext(); )
            {
                String username = ( String )i.next();
                ChatRequestHandler user = ( ChatRequestHandler )this.chatClients.get( username );
                user.removeUser( name );
            }
        }
    }

    /**
     * Creates a new stand-alone chat server
     */
    public static void main( String[] args )
    {
        ChatServer cs = new ChatServer();
        cs.startServer();


    }
}