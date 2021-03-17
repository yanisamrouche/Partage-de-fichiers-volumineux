import java.net.*;
import java.util.concurrent.*;
import java.io.*;

class Server {

    /* Démarrage et délégation des connexions entrantes */
    public void demarrer(int port) {
        ServerSocket ssocket; // socket d'écoute utilisée par le serveur
        Socket csocket;

        System.out.println("Lancement du serveur sur le port " + port);
        try
        {
            ssocket = new ServerSocket(port);
            ssocket.setReuseAddress(true); /* rend le port réutilisable rapidement */
            while (true)
            {
                //(new Handler(ssocket.accept())).run();
                csocket = ssocket.accept();
                Handler ch = new Handler(csocket);
                Thread thread = new Thread(ch);
                thread.start();
            }
        } catch (IOException ex)
        {
            System.out.println("Arrêt anormal du serveur."+ ex);
            return;
        }
    }

    public static void main(String[] args) {
        int argc = args.length;
        Server serveur;

        /* Traitement des arguments */
        if (argc == 1)
        {
            try
            {
                serveur = new Server();
                serveur.demarrer(Integer.parseInt(args[0]));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        } else
        {
            System.out.println("Usage: java Server port");
        }
        return;
    }

    /*
       echo des messages reçus (le tout via la socket).
       NB classe Runnable : le code exécuté est défini dans la
       méthode run().
    */
    class Handler implements Runnable {

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

        public void run()
        {

            while (true) {


                try {
                    String line = in.readLine();
                    switch (line) {
                        case "LIST":
                            System.out.println("WIP : work in process...");
                            break;
                        case "GET":
                            System.out.println("WIP : work in process...");
                            break;
                        case "CREATE":
                            System.out.println("WIP : work in process...");
                            break;
                        case "WRITE":
                            System.out.println("WIP : work in process...");
                            break;
                        case "DELETE":
                            System.out.println("WIP : work in process...");
                            break;
                        default:
                            System.out.println("ERROR : unknown request");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}

