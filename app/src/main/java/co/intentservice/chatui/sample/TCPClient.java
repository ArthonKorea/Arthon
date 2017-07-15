package co.intentservice.chatui.sample;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by hong on 2016-05-02.
 */
public class TCPClient extends Thread {

    public static String SERVERIP = "172.20.10.5";
    public static final int SERVERPORT = 8000;
    BufferedReader in;
    String Sender;
    int flag = -1; // Login = 1 , Main = 2 , His = 3 , Child = 4 , Join = 5 , Updater = 6 , SubwayFinder = 7
    private String serverMessage;
    private boolean mRun = false;
    private PrintWriter out;
    public String Message ="";
    public static Handler handler[]=new Handler[2];
    public void sendMessage(String message) {
        if (out != null && !out.checkError()) {
            out.println(message);
            Log.d("보내요", message);
            Message="";
            out.flush();
        }
    }


    public void stopClient() {
        mRun = false;
    }

    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.e("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVERPORT);
            //SocketHandler.setSocket(socket);

            try {

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                Log.e("TCP Client", "C: Sent.");

                Log.e("TCP Client", "C: Done.");


                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                boolean checker=true;
                //in this while the client listens for the messages sent by the server
                while (mRun) {
                        serverMessage = in.readLine();
                        //Log.e("100", serverMessage);
                    if (serverMessage != null) {
                        Log.d("Message",serverMessage);
                        checker=false;


                        if(serverMessage.startsWith("init/"))
                        {
                            String message = serverMessage.split("/")[1];
                            android.os.Message msg = handler[0].obtainMessage(1, (String)message);
                            handler[0].sendMessage(msg);
                        }else
                        {
                            String message = serverMessage.split("/")[1];
                            android.os.Message msg = handler[1].obtainMessage(1, (String)message);
                            handler[1].sendMessage(msg);
                        }
                        if(serverMessage.equals("quit"))
                            break;
                        sendMessage("OK");
                    }
                    Log.d("Client","inputWaiting");
                    serverMessage = null;

                }
                Log.e("TCP Client", "C: Close.");
                socket.close();

            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);

            }

        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);

        }

    }


}

