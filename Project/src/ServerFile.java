import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class ServerFile {
    ConcurrentHashMap<String, FileHandle> map;
    public ServerFile(){
        this.map = new ConcurrentHashMap<>();
    }

    public boolean isPresent(File file){
        FileHandle fileHandle = new FileHandle(file);
       return map.contains(fileHandle);
    }

    public void transfertFile(InputStream in , OutputStream out) throws IOException {
        byte[] buff = new byte[1024];
        int n;
        while ((n = in.read(buff)) != -1){
            out.write(buff, 0, n);
        }
        in.close();
        out.close();
    }

    public void readFile(File file, Socket socket) throws IOException {
        FileHandle fileHandle = new FileHandle(file);
        fileHandle.readFile(new PrintWriter(socket.getOutputStream(), true));
    }

    public void changeFile(FileHandle fileHandle) throws IOException {
        Socket socket = new Socket();
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        for (Map.Entry<String, FileHandle> f : map.entrySet()){
            if (f.equals(fileHandle))
                fileHandle.replaceFile(new Scanner(in));
        }
    }

    public void createFile(File file) throws IOException {
        file.createNewFile();
        FileHandle fileHandle = new FileHandle(file);
        map.put("",fileHandle);
    }

    public void removeFile(FileHandle fileHandle) {
        //file.delete();
        fileHandle.delete();
        map.remove(fileHandle);
    }

    public String listFiles(File folder) {

        String str = "";
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                str += "/" + fileEntry.getName() + " ";
            } else {
                str += fileEntry.getName() + " ";
            }
        }
        return str;
    }
}
