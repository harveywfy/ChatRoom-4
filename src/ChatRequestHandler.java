

// Import the Java classes

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.Set;


/**
 * A ChatRequestHandler manages the communications for a single chat user
 */
public class ChatRequestHandler implements RequestHandler {
    /**
     * The user’s username once he logs in
     */
    private String username;

    /**
     * Has the user logged in yet?
     */
    private boolean loggedIn = false;

    /**
     * Once a client has connected, this is the interface to read from the client
     */
    private BufferedReader in;

    /**
     * Once a client has connected, this is the interface to write out to the client
     */
    private PrintWriter out;

    /**
     * Handles the incoming request
     *
     * @param socket The socket communication back to the client
     */
    public void handleRequest(Socket socket) {
        try {
            // Get input and output writers that we can use to communicate with the client through
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream());

            // Say "hi"
            System.out.println("New user connected");
            out.println("Welcome to the JavaSRC ChatServer!");
            out.flush();

            // Keep reading lines until we get the "EXIT" command
            String line = in.readLine();
            boolean connected = true;
            while (connected) {
                if (line.length() < 4) {
                    // All commands are required to contain a colon
                    showHelp();
                } else {
                    try {
                        // Extract the command name
                        String command = line.substring(0, 4);

                        // Handle the command; a false return value = the user disconnected
                        connected = handleCommand(command, line);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

                // Read the next line from the client if they are still connected
                if (connected) {
                    line = in.readLine();
                }
            }

            // Say goodbye
            System.out.println("User exited: " + this.username);
            this.loggedIn = false;
            this.username = null;
            out.println("Goodbye");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles command processing
     */
    private boolean handleCommand(String command, String line) {
        if (command.equalsIgnoreCase("user")) {
            if (!this.loggedIn) {
                String argument = line.substring(5);
                System.out.println("Received login command for: " + argument);
                if (argument.length() == 0) {
                    out.println("ERROR Invalid username");
                } else {
                    try {
                        ChatServer.theServer.addChatClient(argument, this);
                        this.username = argument;
                        this.loggedIn = true;
                        out.println("SUCCESS User " + this.username + " logged in");
                        
                    } catch (DuplicateLoginException dpe) {
                        out.println("ERROR Username " + argument + " is already in use");
                    }
                }
            } else {
                out.println("ERROR You are already logged in");
            }
        } else if (command.equalsIgnoreCase("help")) {
            showHelp();
        } else if (command.equalsIgnoreCase("exit")) {
            System.out.println("Received exit command for user: " + this.username);
            ChatServer.theServer.removeChatClient(this.username);
            return false;
        } else {
            // All of these commands require the user to first be logged in
            if (!this.loggedIn) {
                out.println("ERROR You need to logon to send commands to the ChatServer");
                out.flush();
            } else if (command.equalsIgnoreCase("send")) {
                String argument = line.substring(5);
                ChatServer.theServer.sendMessage(this.username, argument);
            } else if (command.equalsIgnoreCase("priv")) {
                String argument = line.substring(5);
                if (argument.indexOf(":") == -1) {
                    out.println("ERROR: Private messages need a recipient!");
                } else {
                    String recipient = argument.substring(0, argument.indexOf(":"));
                    String message = argument.substring(argument.indexOf(":") + 1);
                    ChatServer.theServer.sendMessage(this.username, recipient, message);
                }
            } else if (command.equalsIgnoreCase("list")) {
                Set users = ChatServer.theServer.getUsers();
                StringBuffer sb = new StringBuffer();
                for (Iterator i = users.iterator(); i.hasNext(); ) {
                    String user = (String) i.next();
                    if (!user.equalsIgnoreCase(this.username)) {
                        sb.append(user + ",");
                    }
                }
                if (sb.length() > 1) {
                    out.println("LIST " + sb.substring(0, sb.length() - 1));
                } else {
                    out.println("LIST You are the only user online");
                }
            } else if (command.equalsIgnoreCase("emot")) {
                String argument = line.substring(5);
                ChatServer.theServer.sendEmotion(this.username, argument);
            }
        }

        // Flush the buffer
        out.flush();
        return true;
    }

    /**
     * A user sent, or broadcasted, the specified message to the group
     */
    public void sendMessage(String from, String message) {
        out.println("MESG " + from + ":" + message);
     

        out.flush();
    }

    /**
     * A user sent an emotion to the chat server
     */
    public void sendEmotion(String from, String message) {
        out.println("EMOT " + from + ":" + message);
     

        out.flush();
    }

    /**
     * This user has received a private message from the specified user
     */
    public void privateMessage(String from, String message) {
        out.println("PRIV " + from + ":" + message);
     

        out.flush();
    }

    /**
     * Notification that the specified user has entered the chat
     */
    public void newUser(String otherUser) {
        out.println("USER " + otherUser);
     

        out.flush();
    }

    /**
     * Notification that the specified user has left the chat
     */
    public void removeUser(String otherUser) {
        out.println("RUSR " + otherUser);
     
        out.flush();
    }

    /**
     * Displays help info
     */
    private void showHelp() {
        StringBuffer sb = new StringBuffer();
        sb.append("COMMAND FORMAT: <four-letter-command> <argument>\r\n");
        sb.append(" CHAT COMMAND SUMMARY:\r\n");
        sb.append("  USER <username>\r\n");
        sb.append("  HELP\r\n");
        sb.append("  EXIT\r\n");
        sb.append("  SEND <message>\r\n");
        sb.append("  PRIV <recipient>:<message>\r\n");
        sb.append("  EMOT <emotion message>\r\n");
        sb.append(" MANAGEMENT COMMAND SUMMARY:\r\n");
        sb.append("  LIST\r\n");
        sb.append(" NOTIFICATION SUMMARY\r\n");
        sb.append("  MESG <user>:<message>\r\n");
        sb.append("  PRIV <user>:<message>\r\n");
        sb.append("  EMOT <user>:<message>\r\n");
        sb.append("  USER <user>\r\n");
        sb.append("  RUSR <user>\r\n");


        out.println(sb.toString());
        out.flush();
    }
}