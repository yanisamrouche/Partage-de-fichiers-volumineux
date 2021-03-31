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
        ServerFile serverFile;
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
            this.serverFile = new ServerFile();
        }

        public void run() {
            String tampon;
            long compteur = 0;

            try {
                /* envoi du message d'accueil */
                out.println("WIP");

                do {
                    /* Faire echo et logguer */

                    tampon = in.readLine();

                    if (tampon != null) {

                        String[] commande = tampon.split(" ");
                        commande[0] = commande[0].toUpperCase();

                        switch(commande[0]){
                            case "LIST":
                                out.println("WIP");
                                final File folder = new File("./files");
                                String listOfFiles = this.serverFile.listFiles(folder);
                                out.println(listOfFiles);
                                break;

                            case "GET":
                                out.println("WIP");
                                final File f = new File(commande[1]);
                                this.serverFile.readFile(f,socket);
                                break;

                            case "WRITE":
                                out.println("WIP");
                                final File file = new File(commande[1]);
                                FileHandle fileHandle = new FileHandle(file);
                                this.serverFile.changeFile(fileHandle);
                                break;

                            case "DELETE":
                                out.println("WIP");
                                final File file1 = new File(commande[1]);
                                FileHandle fileHandle1 = new FileHandle(file1);
                                this.serverFile.removeFile(fileHandle1);

                                break;

                            case "CREATE":
                                out.println("WIP");
                                final File file2 = new File(commande[1]);
                                this.serverFile.createFile(file2);
                                break;
                            default:
                                out.println("ERROR : unknown request");
                        }
                    } else {
                        break;
                    }
                } while (true);

                /* le correspondant a quitté */
                in.close();
                out.println("Au revoir...");
                out.close();
                socket.close();

                System.err.println("[" + hote + ":" + port + "]: Terminé...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}



