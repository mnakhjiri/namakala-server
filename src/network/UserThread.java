package network;

import database.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class UserThread implements Runnable{
    ServerSocket serverSocket = new ServerSocket(0);
    User user;

    public UserThread(User user) throws IOException {
        this.user = user;
    }

    @Override
    public void run() {
        try{
            while (true){
                try(Socket socket = serverSocket.accept();
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                ){
                    StringBuilder command = new StringBuilder();
                    int c = dataInputStream.read();
                    while (c != -1 && c != 0) {
                        command.append((char)c);
                        c = dataInputStream.read();
                    }
                    //todo
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int getPort(){
        return serverSocket.getLocalPort();
    }
}
