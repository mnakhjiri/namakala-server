package network;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.ServerSocket;

public class ClientHandler implements Runnable {
    final int port = 8000;
    @Override
    public void run() {
        ServerSocket serverSocket;
        try{
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        while(true){
//            try{
//
//            }
//        }
    }
}
