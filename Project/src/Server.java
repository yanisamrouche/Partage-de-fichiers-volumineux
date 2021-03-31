import java.net.*;
import java.io.*;
import java.util.Scanner;

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
        Scanner scanner;
        InetAddress hote;
        int port;

        Handler(Socket socket) throws IOException
        {
            this.socket = socket;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            scanner = new Scanner(socket.getInputStream());
            hote = socket.getInetAddress();
            port = socket.getPort();
            this.serverFile = new ServerFile();
        }

        public void run() {
            String tampon;
            long compteur = 0;

            try {

                tampon = scanner.nextLine();
                    if (tampon != null) {

                        String[] commande = tampon.split(" ");
                        commande[0] = commande[0].toUpperCase();

                        switch(commande[0]){
                            case "LIST":

                                File folder = new File("./ServerFiles");
                                String listOfFiles = this.serverFile.listFiles(folder);
                                out.println(listOfFiles);
                                socket.close();
                                out.close();
                                scanner.close();
                                break;

                            case "GET":

                                File f = new File("./ServerFiles/"+commande[1]);
                                this.serverFile.readFile(f,socket);
                                socket.close();
                                out.close();
                                scanner.close();
                                break;

                            case "WRITE":
                                File f1 = new File("./ServerFiles/"+commande[1]);
                                FileHandle f2 = new FileHandle(f1);
                                this.serverFile.writeFile(f2, in);
                                socket.close();
                                out.close();
                                in.close();
                                scanner.close();
                                break;

                            case "DELETE":

                                File file1 = new File("./ServerFiles/"+commande[1]);
                                FileHandle fileHandle1 = new FileHandle(file1);
                                this.serverFile.removeFile(fileHandle1);
                                socket.close();
                                out.close();
                                scanner.close();

                                break;

                            case "CREATE":

                                File file2 = new File("./ServerFiles/"+commande[1]);
                                this.serverFile.createFile(file2);
                                socket.close();
                                out.close();
                                scanner.close();
                                break;
                            default:
                                out.println("ERROR : unknown request");
                        }
                    }

                /* le correspondant a quitté */
                out.close();
                scanner.close();
                socket.close();

                System.err.println("[" + hote + ":" + port + "]: Terminé...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
