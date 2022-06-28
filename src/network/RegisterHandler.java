package network;

import database.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RegisterHandler implements  Runnable{
    final int port = 2525;
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
                    while (c != -1 && c != 0) {
                        command.append((char)c);
                        c = dataInputStream.read();
                    }
                    boolean result = User.addUser(command.toString());

                    if(result){
                        System.out.println("registered");
                        dataOutputStream.writeBytes("registered");
                    }else{
                        System.out.println("unregistered");
                        dataOutputStream.writeBytes("unregistered");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
