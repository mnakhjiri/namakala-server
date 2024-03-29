package network;

import database.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RegisterHandler implements  Runnable{
    final int port = 1717;
    int dynPort;

    public int getDynPort() {
        return dynPort;
    }

    @Override
    public void run() {
        ServerSocket serverSocket;
        try{
            serverSocket = new ServerSocket(port);
            while (true){
                try(Socket socket = serverSocket.accept();
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                ){
                    StringBuilder command = new StringBuilder();
                    int c = dataInputStream.read();
                    List<Byte> bytes = new ArrayList<>();
                    while (c != -1 && c != 0) {
                        command.append((char) c);
                        bytes.add((byte)c);
                        c = dataInputStream.read();
                    }
                    String finalString = new String(command.toString().getBytes(StandardCharsets.ISO_8859_1));
                    boolean result = User.addUser(finalString);
                    if(result){
                        dataOutputStream.writeBytes("registered");
                    }else{
                        dataOutputStream.writeBytes("unregistered");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
