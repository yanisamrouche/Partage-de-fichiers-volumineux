import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args){
        File config = new File("servers.txt");
        List<Server> servers = new ArrayList<>();

        String dirName = "ServerFiles";
        int dirNbr = 1;
        try {
            Scanner scanner = new Scanner(config);
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                String host = (line.split(":"))[0];
                int port = Integer.parseInt((line.split(":"))[1]);
                servers.add(new Server(host,port,(dirName+dirNbr)));
                dirNbr++;
            }
            scanner.close();

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }


        for (Server s : servers){
            ArrayList<Server> serverArrayList = new ArrayList<>();
            for (Server p : servers){
                if(!p.equals(s)){
                    serverArrayList.add(p);
                }
            }
            s.setServers(serverArrayList);
            System.out.println("lancement du serveur:  " + s.getLocalhost() + ":" + s.getPort() + "[ " + s.getPathname() +" ]");
            s.demarrer(s.getPort(), s.getPathname());
        }

        for (Server serverPrincipale : servers){
            System.out.println("[Faites entrée lorsque les serveurs sont tous démarrés]");
            new Scanner(System.in).nextLine();

            for (Server s : servers){
                if (!s.equals(serverPrincipale)){
                    try {
                        for (String file : serverPrincipale.getServerFile().map.keySet()){
                            Socket socket = new Socket(s.getLocalhost(), s.getPort());
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                            Scanner scanner = new Scanner(socket.getInputStream());

                            out.println("SERVER_CREATE "+file+" "+s.getLocalhost()+":"+s.getPort());
                            out.flush();
                            scanner.nextLine();

                            out.close();
                            scanner.close();
                            socket.close();
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
