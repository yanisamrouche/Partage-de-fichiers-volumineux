import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Server {

    String pathname;
     int port;
     static  String localhost;
     static ServerFile serverFile;
      List<Server> servers;


    public Server(String localhost,int port, String pathname){
        this.localhost = localhost;
        this.pathname = pathname;
        this.port = port;
        this.servers = new ArrayList<>();
        this.serverFile = new ServerFile(this);
    }
    /* Démarrage et délégation des connexions entrantes */
    public void demarrer(int port, String pathname) {
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

    public String getPathname() {
        return pathname;
    }

    public int getPort() {
        return port;
    }

    public String getLocalhost() {
        return localhost;
    }

    public  ServerFile getServerFile() {
        return serverFile;
    }

    public List<Server> getServers() {
        return servers;
    }

    public Server getServerFromServers(String hostname, int port){
        for (Server s : this.getServers()){
            if(s.getLocalhost().equals(hostname) && s.getPort() == port){
                return s;
            }

        }
        return null;
    }

    public void setServers(ArrayList<Server> s){
        this.servers = s;
    }

    public static void main(String[] args) {
        int argc = args.length;
        Server serveur;

        /* Traitement des arguments */
        if (argc > 1)
        {
            try
            {
                serveur = new Server("localhost",Integer.parseInt(args[0]),args[1]);
                serveur.demarrer(Integer.parseInt(args[0]), args[1]);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        } else
        {
            System.out.println("Usage: java Server port DirectoryName");
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
            this.serverFile = Server.serverFile;
        }

        public void run() {
            String tampon;

            String SerAddress;
            String SerHost;
            Server s;
            int SerPort;

            try {

                tampon = scanner.nextLine();
                    if (tampon != null) {

                        String[] commande = tampon.split(" ");
                        commande[0] = commande[0].toUpperCase();

                        switch(commande[0]){
                            case "LIST":

                                //File folder = new File(pathname);
                                this.serverFile.listFiles(out);
                                socket.close();
                                out.close();
                                scanner.close();
                                break;

                            case "GET":

                               // File f = new File(pathname+commande[1]);
                                //this.serverFile.readFile(f,socket);
                                String f = commande[1];
                                this.serverFile.getRequest(f,out);
                                socket.close();
                                out.close();
                                scanner.close();
                                break;

                            case "WRITE":
                               // File f1 = new File(pathname+commande[1]);
                                //FileHandle f2 = new FileHandle(f1);
                                //this.serverFile.writeFile(f2, in);
                                String filen = commande[1];
                                this.serverFile.writeRequest(filen, scanner, out);
                                socket.close();
                                out.close();
                                in.close();
                                scanner.close();
                                break;

                            case "DELETE":
                                String filename =  commande[1];
                                if(this.serverFile.removeFile(filename, out)){
                                    socket.close();
                                    out.close();
                                    scanner.close();
                                    for(Server server : getServers()){
                                        try {
                                            Socket socket = new Socket(server.getLocalhost(), server.getPort());
                                            out = new PrintWriter(socket.getOutputStream(), true);
                                            scanner = new Scanner(socket.getInputStream());
                                            out.println("SERVER_DELETE "+filename+" "+Server.localhost + ":" + getPort());
                                            out.flush();
                                            scanner.nextLine();
                                            out.close();
                                            scanner.close();
                                        }catch (UnknownHostException e){
                                            e.printStackTrace();
                                            return;
                                        }
                                    }

                                }else {
                                    socket.close();
                                    out.close();
                                    scanner.close();
                                }



                                break;

                            case "CREATE":
                                String fn = commande[1];
                                if (this.serverFile.isPresent(fn) || this.serverFile.isPresentOnServer(fn)){
                                    out.println(fn+" existe déja");
                                    out.flush();
                                    socket.close();
                                    out.close();
                                    scanner.close();
                                }else {
                                    out.println("OK");
                                    out.flush();
                                    this.serverFile.createFile(fn, scanner,out);
                                    socket.close();
                                    out.close();
                                    scanner.close();

                                    for(Server server : getServers()){
                                        try {
                                            Socket socket = new Socket(server.getLocalhost(), server.getPort());
                                            out = new PrintWriter(socket.getOutputStream(), true);
                                            scanner = new Scanner(socket.getInputStream());
                                            out.println("SERVER_CREATE "+ fn+" "+Server.localhost+":"+getPort());
                                            out.flush();
                                            scanner.nextLine();
                                            out.close();
                                            scanner.close();
                                        }catch (UnknownHostException e){
                                            e.printStackTrace();
                                            return;
                                        }
                                    }
                                }



                                break;

                            case "SERVER_CREATE":
                                //la commande : SERVER_CREATE file1.txt localhost:1234
                                String fileName = pathname + commande[1];
                                SerAddress = commande[2];
                                SerHost = SerAddress.split(":")[0];
                                SerPort = Integer.parseInt(SerAddress.split(":")[1]);

                                s = getServerFromServers(SerHost,SerPort);
                                this.serverFile.addToServer(fileName, s);
                                out.println("OK");
                                out.flush();

                                socket.close();
                                out.close();
                                scanner.close();
                                break;

                            case "SERVER_DELETE":
                                //la commande : SERVER_DELETE file1.txt localhost:1234
                                String fname = pathname + commande[1];
                                SerAddress = commande[2];
                                SerHost = SerAddress.split(":")[0];
                                SerPort = Integer.parseInt(SerAddress.split(":")[1]);

                                this.serverFile.removeFromServer(fname);
                                out.println("OK");
                                out.flush();

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
