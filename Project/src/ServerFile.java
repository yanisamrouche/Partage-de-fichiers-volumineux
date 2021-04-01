import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class ServerFile {
    ConcurrentHashMap<String, FileHandle> map;
    ConcurrentHashMap<String, Server> servers;
    Server current;
    public ServerFile(Server currentServer){
        this.map = new ConcurrentHashMap<>();
        this.servers = new ConcurrentHashMap<>();
        this.current = currentServer;
        File directory = new File(this.current.pathname);
        for (File file : directory.listFiles()){
            this.map.put(file.getName(), new FileHandle(file));
        }
        for (Server server : this.current.getServers()){
            File dir = new File(server.getPathname());
            for (File file : dir.listFiles()){
                this.servers.put(file.getName(), server);
            }
        }
    }

    public boolean isPresent(String filename){
        return map.get(filename) != null;
    }
    public boolean isPresentOnServer(String filename){
        return servers.get(filename) != null;
    }

    public void addToServer(String fn, Server server){
        this.servers.put(fn,server);
    }

    public void removeFromServer(String f){
        this.servers.remove(f);
    }

    public void readFile(File file, Socket socket) throws IOException {
        FileHandle fileHandle = new FileHandle(file);
        fileHandle.readFile(new PrintWriter(socket.getOutputStream(), true));
    }

    public void writeFile(FileHandle fileHandle, BufferedReader in) throws IOException {
        fileHandle.replaceFile(new Scanner(in));
    }

    public void createFile(String filename, Scanner scanner, PrintWriter writer) throws IOException {
        /* File en param
        file.createNewFile();
        FileHandle fileHandle = new FileHandle(file);
        map.put("",fileHandle);

         */
        File newFile = new File(this.current.getPathname() + "/" + filename);
        try {
            if (newFile.createNewFile()) {
                this.map.put(filename, new FileHandle(newFile));

                FileWriter fw = new FileWriter(this.current.getPathname() + "/" + filename);

                while(scanner.hasNext()) {
                    String line = scanner.nextLine();
                    try {
                        fw.write(line + "\n");
                    } catch (IOException e) {
                        return;
                    }
                }

                fw.close();
            } else {
                System.out.println("Le fichier <" + filename + "> existe déjà.");
                writer.println("Le fichier <"+ filename +"> n'a pas été créé");
                writer.flush();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.println("Le fichier <"+ filename +"> a bien été créé");
        writer.flush();


    }

    public boolean removeFile(String filename, PrintWriter printWriter) {
        /* fileHandle en paramatere
        fileHandle.delete();
        map.remove(fileHandle);
         */
        if(this.map.get(filename) != null){
            printWriter.println("OK");
            printWriter.flush();
            this.map.get(filename).delete();
            this.map.remove(filename);
            printWriter.println(filename+" a bien été supprimé");
            printWriter.flush();
            return true;
        }else if (this.servers.get(filename) != null){
            printWriter.println("REDIRECTION "+this.servers.get(filename).getLocalhost()+":"+this.servers.get(filename).getPort());
            printWriter.flush();
            return false;
        }else {
            printWriter.println("l'opération a échoué");
            printWriter.flush();
            return false;
        }

    }

    public void listFiles(PrintWriter printWriter) {

        File folder = new File(this.current.getPathname());

        String str = "";
        //fichiers locaux
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                str += "/" + fileEntry.getName() + " ";
                printWriter.println(str);
            } else {
                str += fileEntry.getName() + " ";
                printWriter.println(str);
            }
        }

        //fichiers externes
        for(String filename : this.servers.keySet()){
            str += filename;
            printWriter.println(str);
        }

        printWriter.flush();

    }
}