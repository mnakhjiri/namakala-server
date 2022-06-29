package network;

import com.google.gson.Gson;
import database.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class UserThread implements Runnable{
    Gson gson = new Gson();
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
                    String finalString = new String(command.toString().getBytes(StandardCharsets.ISO_8859_1));
                    String[] commands = finalString.split("-");
                    System.out.println(finalString);
                    if(commands[0].equals("edit")){
                        //
                        System.out.println("edit mode");
                        System.out.println(commands[1]);
                        //
                        Map<String , String> map = gson.fromJson(commands[1] , Map.class);
                        User changedUser = new User();
                        if(map.get("name").equals("")){
                            changedUser.name = user.name;
                        }else {
                            changedUser.name = map.get("name");
                        }

                        if(map.get("phoneNumber").equals("")){
                            changedUser.phoneNumber = user.phoneNumber;
                        }else {
                            changedUser.phoneNumber = map.get("phoneNumber");
                        }

                        if(map.get("mail").equals("")){
                            changedUser.mail = user.mail;
                        }else {
                            changedUser.mail = map.get("mail");
                        }

                        if(map.get("pass").equals("")){
                            changedUser.pass = user.pass;
                        }else {
                            changedUser.pass = map.get("pass");
                        }
                        if(map.get("img").equals("")){
                            changedUser.img = user.img;
                        }else{
                            changedUser.img = map.get("img");
                        }
                        User.deleteUser(user);
                        User.addUser(gson.toJson(changedUser));
                        user = changedUser;
                        dataOutputStream.write(gson.toJson(user).getBytes("UTF-8"));
                    }
                    if(commands[0].equals("exit")){
                        System.out.println("exiting");
                        return;
                    }
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
