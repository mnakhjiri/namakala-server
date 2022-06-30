package network;

import com.google.gson.Gson;
import database.Product;
import database.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductView implements Runnable{
    final int port = 4646;
    Gson gson = new Gson();
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
                    String[] commands = finalString.split("-");
                    if(commands[0].equals("getProducts")){
                        Product[] products = Product.getProducts();
                        ArrayList<Product> foundedList = new ArrayList<>();
                        for(Product product : products){
                            if(Arrays.asList(product.categories).contains(commands[1])){
                                foundedList.add(product);
                            }
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        for(Product product : foundedList){
                            stringBuilder.append(gson.toJson(product)).append(",,");
                        }
                        String result = stringBuilder.toString();
                        if(result.equals("")){
                            dataOutputStream.write(result.getBytes("UTF-8"));
                        }else{
                            dataOutputStream.write(result.substring(0,result.length()-2).getBytes("UTF-8"));
                        }
                        continue;
                    }
                    Product product = Product.findProduct(commands[1]);
                    boolean isOwner = false;
                    if(!commands[0].equals("")){
                        if(commands[0].equals(product.seller.phoneNumber)){
                            isOwner = true;
                        }
                    }
                    dataOutputStream.write((isOwner + "-" + gson.toJson(product) + "-" + product.seller.img).getBytes("UTF-8"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
