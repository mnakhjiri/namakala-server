package network;

import com.google.gson.Gson;
import database.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LoginHandler implements Runnable{
    Gson gson = new Gson();
    final int port = 4242;
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
                    String[] info = command.toString().split("-");

                    User result = User.findUser(info[0] , info[1]);
                    if(result == null){

                        dataOutputStream.writeBytes("failed");
                    }else{
                        //
                        System.out.println("user Found");
                        //
                        UserThread userThread = new UserThread(result);
                        new Thread(userThread).start();
                        int port = userThread.getPort();
                        System.out.println("port is " + port);
                        String json = gson.toJson(result);
                        dataOutputStream.write((port + "-" + json).getBytes("UTF-8"));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
