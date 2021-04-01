import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        Socket socket;
        String ip;
        int port;
        String folderURL;
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out;
        String response;
        Scanner scanner;
        ArrayList<String> files = new ArrayList<String>();

        boolean fini = false;

        if (args.length != 3) {
            System.out.println("Usage: java Client ip port DirName");
            System.exit(1);
        }
        ip = args[0];
        port = Integer.parseInt(args[1]);
        folderURL = args[2];

        try {

            while (true) {

                //Connexion
                try {
                    socket = new Socket(ip, port);
                    out = new PrintWriter(socket.getOutputStream(), true);
                    scanner = new Scanner(socket.getInputStream());

                } catch (UnknownHostException e) {
                    System.err.println("Connexion: hôte inconnu : " + ip);
                    e.printStackTrace();
                    return;
                }

                /* requete LIST au clavier
                String listRequest = stdin.readLine();
                if (listRequest.equals("LIST")){
                    out.println("LIST");
                    out.flush();
                }

                 */
                out.println("LIST");
                out.flush();
                files.clear();
                while(scanner.hasNextLine()) {
                    files.add(scanner.nextLine());
                }
                System.out.println(" la liste des fichiers du serveur : ");
                System.out.println("-----------------------------------------");
                for(String f : files) {
                    System.out.println("< "+f+" >"+"\n");
                }
                System.out.println("-----------------------------------------");
                socket.close();
                out.close();
                scanner.close();


                String filename = "";
                String request = "";
                boolean wrongFileName = false;
                boolean wrongCommand = false;

                System.out.println("");


                while(wrongCommand); {
                    System.out.println("Vos Choix : ");
                    System.out.println("\nChoix n° 01 -> Selectionner un fichier");
                    System.out.println("-----------------------------------------");
                    System.out.println("Choix n° 02 -> Upload un fichier");
                    System.out.println("-----------------------------------------");
                    System.out.println("Choix n° 03 -> Créer un nouveau fichier");
                    System.out.println("-----------------------------------------");
                    System.out.println("Choix n° 04 -> Lister les fichiers\n");
                    System.out.println("-----------------------------------------");
                    System.out.println("Tapez votre choix : ");
                    System.out.println("-----------------------------------------");
                    System.out.print(">");
                    int choice = Integer.parseInt(stdin.readLine());


                    if(choice == 1) {

                        System.out.println("Veuillez choisir votre fichier\n");
                        System.out.println("-----------------------------------------");

                        for(String f : files) {
                            System.out.println(f);
                        }
                        System.out.println("-----------------------------------------");


                        System.out.println("\n\nSelectionnez un fichier:");
                        System.out.print(">");
                        filename = stdin.readLine();
                        System.out.println("Vous avez selectionné le fichier < "+filename+" >");
                        System.out.println("\n1- Télécharger le fichier < "+filename+" >");
                        System.out.println("2- Modifier le contenu d'un fichier < "+filename+" >");
                        System.out.println("3- Supprimer le fichier < "+filename+" >");
                        System.out.println("Tapez votre choix : ");
                        System.out.print(">");

                        choice = Integer.parseInt(stdin.readLine());

                        if(choice == 1) {
                            request = "DOWNLOAD";
                            wrongCommand = false;
                        } else if(choice == 2) {
                            request = "EDIT";
                            wrongCommand = false;
                        } else if(choice == 3) {
                            request = "DELETE";
                            wrongCommand = false;
                        } else {
                            System.out.println("Veuillez saisir une commande valide!");
                            wrongCommand = true;
                        }

                    } else {
                        if(choice == 2) {
                            request = "UPLOAD";
                            wrongCommand = false;
                        } else if(choice == 3) {
                            request = "CREATE";
                            wrongCommand = false;
                        } else if(choice == 4) {
                            request = "LIST";
                            wrongCommand = false;
                        } else {
                            System.out.println("Veuillez saisir une commande valide!");
                            wrongCommand= true;
                        }
                    }
                }


                switch(request) {
                    //Téléchargement d'un fichier
                    case "DOWNLOAD":
                        if(filename != "") {
                            // Creation de la socket
                            try {
                                socket = new Socket(ip, port);
                                out = new PrintWriter(socket.getOutputStream(), true);
                                //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                scanner = new Scanner(socket.getInputStream());

                            } catch (UnknownHostException e) {
                                System.err.println("Connexion: hôte inconnu : " + ip);
                                e.printStackTrace();
                                return;
                            }

                            out.println("GET " + filename);
                            out.flush();

                            response = scanner.nextLine();

                            if(response.contains("REDIRECTION")) {
                                System.out.println(response);
                                socket.close();
                                scanner.close();
                                out.close();

                                // On redirige le client pour effectuer la suppression
                                String address= response.split(" ")[1];
                                String newHost = address.split(":")[0];
                                int newPort = Integer.parseInt(address.split(":")[1]);

                                try {
                                    socket = new Socket(newHost, newPort);
                                    out = new PrintWriter(socket.getOutputStream(), true);
                                    scanner = new Scanner(socket.getInputStream());

                                    out.println("GET " + filename);
                                    out.flush();

                                    response = scanner.nextLine();

                                    if(!response.contains("OK")) {
                                        System.out.println(response);
                                        System.out.println("Deconnexion...");
                                        System.exit(0);
                                    }

                                    File newFile = new File(folderURL + "/" + filename);
                                    newFile.createNewFile();

                                    FileWriter fw = new FileWriter(folderURL + "/" + filename);

                                    while(scanner.hasNextLine()) {
                                        String line = scanner.nextLine();
                                        fw.write(line + "\n");
                                    }

                                    fw.close();
                                    socket.close();
                                    out.close();
                                    scanner.close();

                                    break;
                                } catch (UnknownHostException e) {
                                    System.err.println("Connexion: hôte inconnu : " + ip);
                                    e.printStackTrace();
                                    return;
                                }
                            }

                            File newFile = new File(folderURL + "/" + filename);
                            newFile.createNewFile();

                            FileWriter fw = new FileWriter(folderURL + "/" + filename);

                            while(scanner.hasNextLine()) {
                                String line = scanner.nextLine();
                                fw.write(line + "\n");
                            }

                            fw.close();
                            socket.close();
                            out.close();
                            scanner.close();

                        } else {
                            System.out.println("ERREUR: Veuillez saisir un nom de fichier valide!");
                        }
                        break;

                    // Uploader un fichier
                    case "UPLOAD":

                        // Connexion
                        try {
                            socket = new Socket(ip, port);
                            out = new PrintWriter(socket.getOutputStream(), true);
                            scanner = new Scanner(socket.getInputStream());

                        } catch (UnknownHostException e) {
                            System.err.println("Connexion: hôte inconnu : " + ip);
                            e.printStackTrace();
                            return;
                        }

                        boolean wrongFilename = false;
                        String uploadFilename = "";
                        do {
                            System.out.println("Saisissez le nom fichier a upload :");
                            System.out.print(">");
                            uploadFilename = stdin.readLine();

                            if(uploadFilename == "") {
                                System.out.println("ERREUR: le nom du fichier est invalide!");
                                wrongFilename = true;
                            } else {
                                wrongFilename = false;
                            }
                        } while(wrongFilename);

                        //Verifier dans la liste si le fichier existe déjà
                        if(files.contains(uploadFilename)) {

                            // Il faut écrire dans le fichier existant
                            out.println("WRITE " + uploadFilename);
                            out.flush();

                            response = scanner.nextLine();

                            if(response.contains("REDIRECTION")) {
                                System.out.println(response);
                                socket.close();
                                scanner.close();
                                out.close();

                                // On redirige le client pour effectuer la suppression
                                String address= response.split(" ")[1];
                                String newHost = address.split(":")[0];
                                int newPort = Integer.parseInt(address.split(":")[1]);

                                try {
                                    socket = new Socket(newHost, newPort);
                                    out = new PrintWriter(socket.getOutputStream(), true);
                                    scanner = new Scanner(socket.getInputStream());

                                    // Creer le fichier
                                    out.println("WRITE " + uploadFilename);
                                    out.flush();

                                    response = scanner.nextLine();

                                    if(!response.contains("OK")) {
                                        System.out.println(response);
                                        System.out.println("Deconnexion...");
                                        System.exit(0);
                                    }

                                    File file = new File(folderURL + "/" + uploadFilename);
                                    Scanner fr = new Scanner(file);

                                    while(fr.hasNextLine()) {
                                        String line = fr.nextLine();
                                        out.println(line + "\n");
                                        out.flush();
                                    }

                                    fr.close();

                                    socket.close();
                                    out.close();
                                    scanner.close();

                                    break;
                                } catch (UnknownHostException e) {
                                    System.err.println("Connexion: hôte inconnu : " + ip);
                                    e.printStackTrace();
                                    return;
                                }
                            } else if(!response.contains("OK")) {
                                System.out.println(response);
                                System.exit(0);
                            }

                            File file = new File(folderURL + "/" + uploadFilename);
                            Scanner fr = new Scanner(file);

                            while(fr.hasNextLine()) {
                                String line = fr.nextLine();
                                out.println(line + "\n");
                                out.flush();
                            }
                            fr.close();
                        } else {
                            // Creer le fichier
                            out.println("CREATE " + uploadFilename);
                            out.flush();


                            // On lit la réponse du serveur
                            response = scanner.nextLine();
                            //System.out.println(response)

                            if(response.contains("REDIRECTION")) {
                                System.out.println(response);
                                socket.close();
                                scanner.close();
                                out.close();

                                // On redirige le client pour effectuer la suppression
                                String address= response.split(" ")[1];
                                String newHost = address.split(":")[0];
                                int newPort = Integer.parseInt(address.split(":")[1]);

                                try {
                                    socket = new Socket(newHost, newPort);
                                    out = new PrintWriter(socket.getOutputStream(), true);
                                    scanner = new Scanner(socket.getInputStream());

                                    // Creer le fichier
                                    out.println("CREATE " + uploadFilename);
                                    out.flush();

                                    response = scanner.nextLine();

                                    if(!response.contains("OK")) {
                                        System.out.println(response);
                                        System.exit(0);
                                    }

                                    File file = new File(folderURL + "/" + uploadFilename);
                                    Scanner fr = new Scanner(file);

                                    while(fr.hasNextLine()) {
                                        String line = fr.nextLine();
                                        out.println(line + "\n");
                                        out.flush();
                                    }

                                    socket.close();
                                    out.close();
                                    scanner.close();

                                    break;
                                } catch (UnknownHostException e) {
                                    System.err.println("Connexion: hôte inconnu : " + ip);
                                    e.printStackTrace();
                                    return;
                                }
                            } else if(!response.contains("OK")) {
                                System.out.println(response);
                                System.exit(0);
                            }


                            File file = new File(folderURL + "/" + uploadFilename);
                            Scanner fr = new Scanner(file);

                            while(fr.hasNextLine()) {
                                String line = fr.nextLine();
                                out.println(line + "\n");
                                out.flush();
                            }
                        }

                        socket.close();
                        out.close();
                        scanner.close();
                        break;
                    //DELETE
                    case "DELETE":
                        if(filename != "") {
                            try {
                                socket = new Socket(ip, port);
                                out = new PrintWriter(socket.getOutputStream(), true);
                                //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                scanner = new Scanner(socket.getInputStream());

                            } catch (UnknownHostException e) {
                                System.err.println("Connexion: hôte inconnu : " + ip);
                                e.printStackTrace();
                                return;
                            }

                            out.println("DELETE " + filename);
                            out.flush();

                            response = scanner.nextLine();

                            if(response.contains("REDIRECTION")) {
                                System.out.println(response);

                                socket.close();
                                scanner.close();
                                out.close();

                                // On redirige le client pour effectuer la suppression
                                String address= response.split(" ")[1];
                                String newHost = address.split(":")[0];
                                int newPort = Integer.parseInt(address.split(":")[1]);

                                try {
                                    socket = new Socket(newHost, newPort);
                                    out = new PrintWriter(socket.getOutputStream(), true);
                                    scanner = new Scanner(socket.getInputStream());

                                    out.println("DELETE " + filename);
                                    out.flush();

                                    response = scanner.nextLine();

                                    if(!response.contains("OK")) {
                                        System.out.println(response);
                                        System.out.println("Deconnexion...");
                                        System.exit(0);
                                    }

                                    response = scanner.nextLine();
                                    System.out.println(response);

                                    socket.close();
                                    out.close();
                                    scanner.close();

                                    break;


                                } catch (UnknownHostException e) {
                                    System.err.println("Connexion: hôte inconnu : " + ip);
                                    e.printStackTrace();
                                    return;
                                }
                            } else if(!response.contains("OK")) {
                                System.out.println(response);
                                System.out.println("Deconnexion...");
                                System.exit(0);
                            }

                            response = scanner.nextLine();
                            System.out.println(response);

                            socket.close();
                            out.close();
                            scanner.close();

                        } else {
                            System.out.println("ERREUR: Veuillez saisir un nom de fichier valide!");
                        }
                        break;
                    // LIST
                    case "LIST":

                        try {
                            socket = new Socket(ip, port);
                            out = new PrintWriter(socket.getOutputStream(), true);
                            //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            scanner = new Scanner(socket.getInputStream());

                        } catch (UnknownHostException e) {
                            System.err.println("Connexion: hôte inconnu : " + ip);
                            e.printStackTrace();
                            return;
                        }
                        // On envoie la commande au serveur
                        out.println("LIST");
                        out.flush();

                        files.clear();
                        while(scanner.hasNextLine()) {
                            files.add(scanner.nextLine());
                        }

                        for(String f : files) {
                            System.out.println(f);
                        }

                        socket.close();
                        out.close();
                        scanner.close();

                        break;
                    // CREATE
                    case "CREATE":
                        try {
                            socket = new Socket(ip, port);
                            out = new PrintWriter(socket.getOutputStream(), true);
                            scanner = new Scanner(socket.getInputStream());

                        } catch (UnknownHostException e) {
                            System.err.println("Connexion: hôte inconnu : " + ip);
                            e.printStackTrace();
                            return;
                        }

                        System.out.println("Saisissez le nom fichier a upload :");
                        System.out.print(">");
                        filename = stdin.readLine();

                        if(filename == "") {
                            System.out.println("ERREUR: le nom du fichier est invalide!");
                            return;
                        }
                        // On execute la commande
                        out.println("CREATE " + filename);
                        out.flush();

                        // On lit la réponse du serveur
                        response = scanner.nextLine();
                        //System.out.println(response);

                        if(!response.contains("OK")) {
                            System.out.println(response);
                            System.out.println("Deconnexion...");
                            socket.close();
                            out.close();
                            scanner.close();
                            break;
                        }

                        // On saisit le contenu du fichier
                        System.out.println("Saisissez le contenu du fichier:");
                        String tampon = "";
                        while((tampon = stdin.readLine()) != null) {
                            out.println(tampon);
                            out.flush();
                        }

                        socket.close();
                        out.close();
                        scanner.close();

                        System.out.println(response);
                        break;
                    case "EDIT":

                        if(filename != "") {
                            // Connexion
                            try {
                                socket = new Socket(ip, port);
                                out = new PrintWriter(socket.getOutputStream(), true);
                                scanner = new Scanner(socket.getInputStream());

                            } catch (UnknownHostException e) {
                                System.err.println("Connexion: hôte inconnu : " + ip);
                                e.printStackTrace();
                                return;
                            }

                            // WRITE vers le serveur
                            out.println("WRITE " + filename);
                            out.flush();

                            // a modifié plustard
                            String l = "";

                            while((l = stdin.readLine()) != null) {
                                out.println(l);
                                out.flush();
                            }

                            /////////////////////

                            // On lit la réponse du serveur
                            response = scanner.nextLine();

                            if(response.contains("REDIRECTION")) {
                                System.out.println(response);
                                socket.close();
                                scanner.close();
                                out.close();

                                // On redirige le client pour effectuer la suppression
                                String address= response.split(" ")[1];
                                String newHost = address.split(":")[0];
                                int newPort = Integer.parseInt(address.split(":")[1]);

                                try {
                                    socket = new Socket(newHost, newPort);
                                    out = new PrintWriter(socket.getOutputStream(), true);
                                    scanner = new Scanner(socket.getInputStream());

                                    // WRITE vers le serveur
                                    out.println("WRITE " + filename);
                                    out.flush();

                                    response = scanner.nextLine();

                                    if(!response.contains("OK")) {
                                        System.out.println(response);
                                        System.out.println("Deconnexion...");
                                        System.exit(0);
                                    }

                                    System.out.println("Saisissez le nouveau contenu du fichier");
                                    System.out.print(">");
                                    String line = "";
                                    // Ecrive
                                    while((line = stdin.readLine()) != null) {
                                        out.println(line);
                                        out.flush();
                                    }

                                    socket.close();
                                    out.close();
                                    scanner.close();

                                    break;
                                } catch (UnknownHostException e) {
                                    System.err.println("Connexion: hôte inconnu : " + ip);
                                    e.printStackTrace();
                                    return;
                                }

                            } else if(!response.contains("OK")) {
                                System.out.println(response);
                                System.out.println("Deconnexion...");
                                System.exit(0);
                            }

                            System.out.println("Saisissez le nouveau contenu du fichier");
                            System.out.print(">");
                            String line = "";
                            // Ecrive
                            while((line = stdin.readLine()) != null) {
                                out.println(line);
                                out.flush();
                            }

                            socket.close();
                            out.close();
                            scanner.close();
                        } else {
                            System.out.println("ERREUR: Veuillez saisir un nom de fichier valide!");
                        }
                        break;
                    default:
                        System.out.println("ERREUR: commande non valide!");
                        fini = true;
                }


                if (fini == true) break;
            }


            System.err.println("Fin de la session.");
        } catch (IOException e) {
            System.err.println("Erreur");
            e.printStackTrace();
            System.exit(8);
        }

        return;
    }

}
