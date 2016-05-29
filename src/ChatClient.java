import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {

    public static void main(String[] args) {



        String hostname = "localhost";

        if (args.length > 0) {
            hostname = args[0];
        }

        PrintWriter out = null;
        BufferedReader networkIn = null;
        try {
            Socket localhostSocket = new Socket(hostname, 9988);
            InputStreamReader networkStreamReader = new InputStreamReader(localhostSocket.getInputStream());
            networkIn = new BufferedReader(networkStreamReader);
            out = new PrintWriter(localhostSocket.getOutputStream());
            System.out.println("Connected to echo server");

//            为手动输入开启单独的线程，避免阻塞 显示消息的线程
            HandInputThread handinput = new HandInputThread(out);

            handinput.start();


            String inputLine = networkIn.readLine();

            while (null != inputLine) {
                System.out.println(inputLine);

                inputLine = networkIn.readLine();

            }

        } catch (IOException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (networkIn != null) networkIn.close();
                if (out != null) out.close();
            } catch (IOException ex) {
            }
        }

    }
}

/**
 * 获取用户输入的线程
 */
class HandInputThread extends Thread {


    private PrintWriter out;


    public HandInputThread(PrintWriter out) {
        this.out = out;
    }


    @Override
    public void run() {


        while (true) {
            InputStreamReader userStreamReader = new InputStreamReader(System.in);

            BufferedReader userIn = new BufferedReader(userStreamReader);

            String theLine = null;
            try {
                theLine = userIn.readLine();
                if (theLine.equals('.')) break;

                out.println(theLine);
                out.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }
}