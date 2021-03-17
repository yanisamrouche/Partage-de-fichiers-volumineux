import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class EchoServer {

    public static void main(String[] args){
        int  port = Integer.parseInt(args[0]);
        ServerSocket ssocket;
        try {
            ssocket = new ServerSocket(port);
            while (true) {
                (new Handler(ssocket.accept())).run();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Handler implements Runnable{

        Socket socket;
        PrintWriter out;
        BufferedReader in;
        InetAddress hote;
        int port;


        Handler(Socket socket) throws IOException
        {
            this.socket = socket;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            hote = socket.getInetAddress();
            port = socket.getPort();
        }

        @Override
        public void run() {
            System.out.println("WIP : work in progress");
        }
    }






}
