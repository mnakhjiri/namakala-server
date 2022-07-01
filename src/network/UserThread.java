package network;

import com.google.gson.Gson;
import database.Order;
import database.Product;
import database.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
                    if(commands[0].equals("fav")){
                        if(commands[1].equals("set")){
                            synchronized (Product.class){
                                Product product = Product.findProduct(commands[2]);
                                Product.deleteProduct(product);
                                ArrayList<String> favusers =  new ArrayList<>(Arrays.asList(product.favUsers));
                                favusers.add(user.phoneNumber);
                                product.favUsers = favusers.toArray(new String[0]);
                                System.out.println(product.seller);
                                Product.addProduct(product);
                            }
                        }else if(commands[1].equals("reset")){
                            synchronized (Product.class){
                                Product product = Product.findProduct(commands[2]);
                                Product.deleteProduct(product);
                                ArrayList<String> favusers =  new ArrayList<>(Arrays.asList(product.favUsers));
                                favusers.remove(user.phoneNumber);
                                product.favUsers = favusers.toArray(new String[0]);
                                System.out.println(product.seller);
                                Product.addProduct(product);
                            }
                        }
                    }
                    if(commands[0].equals("favProduct")){
                        Product[] products =Product.getProducts();
                        ArrayList<Product> favProducts = new ArrayList<>();
                        System.out.println();
                        for(Product product : products){
                            if(Arrays.asList(product.favUsers).contains(commands[1])){
                                favProducts.add(product);
                            }
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        for(Product product : favProducts){
                            stringBuilder.append(gson.toJson(product)).append(",,");
                        }
                        String result = stringBuilder.toString();
                        if(result.equals("")){
                            dataOutputStream.write(result.getBytes("UTF-8"));
                        }else{
                            dataOutputStream.write(result.substring(0,result.length()-2).getBytes("UTF-8"));
                        }
                    }
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
                    if(commands[0].equals("userProduct")){
                        Product[] products = Product.findProductByUser(user);
                        StringBuilder stringBuilder = new StringBuilder();
                        for(Product product : products){
                            stringBuilder.append(gson.toJson(product) + ",,");
                        }
                        String result = stringBuilder.toString();
                        if(result.equals("")){
                            dataOutputStream.write(result.getBytes("UTF-8"));
                        }else{
                            dataOutputStream.write(result.substring(0,result.length()-2).getBytes("UTF-8"));
                        }
                    }
                    if (commands[0].equals("deleteProduct")){
                        Product.deleteProduct(Product.findProduct(commands[1]));
                    }
                    if(commands[0].equals("addProduct")){
                        Product product = gson.fromJson(commands[1] , Product.class);
                        product.seller = user;
                        Product.addProduct(product);
                    }

                    if(commands[0].equals("getCount")){
                        Product[] products = Product.getProducts();
                        String result = "0";
                        for(Product product : products){
                            if(product.name.equals(commands[1])){
                                result = product.count;
                            }
                        }
                        dataOutputStream.write(result.getBytes("UTF-8"));
                    }
                    if(commands[0].equals("increaseCount")){
                        synchronized (Product.class){
                            String productName = commands[1];
                            Product product =  Product.findProduct(productName);
                            Product.deleteProduct(product);
                            product.count = String.valueOf(Integer.parseInt(product.count) + 1);
                            Product.addProduct(product);
                        }
                    }
                    if(commands[0].equals("reduceCount")){
                        synchronized (Product.class){
                            String productName = commands[1];
                            Product product =  Product.findProduct(productName);
                            Product.deleteProduct(product);
                            product.count = String.valueOf(Integer.parseInt(product.count) - 1);
                            Product.addProduct(product);
                        }
                    }
                    if(commands[0].equals("setCount")){
                        synchronized (Product.class){
                            String productName = commands[1];
                            Product product =  Product.findProduct(productName);
                            Product.deleteProduct(product);
                            product.count = commands[2];
                            Product.addProduct(product);
                        }
                    }

                    if(commands[0].equals("addOrder")){
                        Order.addOrder(commands[1]);
                    }
                    if(commands[0].equals("setCart")){
                        System.out.println("inside setCart");
                        Product.addToCart(commands[1] , commands[2] , commands[3]);
                    }
                    if(commands[0].equals("getOrders")){
                        Order[] orders = Order.getOrders();
                        ArrayList<String> orderArrayList = new ArrayList<>();
                        for(Order order : orders){
                            if(order.userPhone.equals(user.phoneNumber)){
                                orderArrayList.add(gson.toJson(order));
                            }
                        }
                        dataOutputStream.write(gson.toJson(orderArrayList).getBytes("UTF-8"));
                    }
                    // 0 => getCart
                    // 1=> productname
                    // 2 => userphone
                    if(commands[0].equals("getCart")){
                        Product[] products = Product.getProducts();
                        for(Product product : products){
                            if(product.name.equals(commands[1])){
                                dataOutputStream.write(product.carts.getOrDefault(commands[2], "false").getBytes("UTF-8"));
                            }
                        }
                    }
                    if(commands[0].equals("getUserCart")){
                        Product[] products = Product.getProducts();
                        Map<String , String> currentCart = new HashMap<>();
                        Map<String , String> maxCart = new HashMap<>();
                        for(Product product : products){
                            if(product.carts.containsKey(user.phoneNumber)){
                                currentCart.put(gson.toJson(product) , product.carts.get(user.phoneNumber));
                                maxCart.put(gson.toJson(product) , product.count);
                            }
                        }
                        dataOutputStream.write((gson.toJson(currentCart) +"-"+ gson.toJson(maxCart) ).getBytes("UTF-8"));
                    }
                    if(commands[0].equals("editProduct")){
                        String productName = commands[1];
                        Product product =  Product.findProduct(productName);
                        Map map = gson.fromJson(commands[2] , Map.class);
                        Product changedProduct = new Product();
                        if(map.get("name").equals("")){
                            changedProduct.name = product.name;
                        }else {
                            changedProduct.name = (String) map.get("name");
                        }
                        changedProduct.favUsers = product.favUsers;
                        changedProduct.seller = product.seller;
                        if(((ArrayList<String>) map.get("images")).get(0).equals("")){
                            changedProduct.images = product.images;
                        }else {
                            changedProduct.images = ((ArrayList<String>) map.get("images")).toArray(new String[0]);
                        }
                        if(map.get("price").equals("")){
                            changedProduct.price = product.price;
                        }else {
                            changedProduct.price = (String) map.get("price");
                        }
                        if( ((ArrayList<String>)map.get("categories")).get(0).equals("") ){
                            changedProduct.categories = product.categories;
                        }else {
                            changedProduct.categories = ((ArrayList<String>) map.get("categories")).toArray(new String[0]);
                        }
                        if(((Map<String, ArrayList<String>>) map.get("properties")).containsKey("")){
                            changedProduct.properties = product.properties;
                        }else {
                            Map<String , ArrayList<String>> jsonMap =((Map<String, ArrayList<String>>) map.get("properties"));
                            Map<String , String[]> resultMap = new HashMap<>();
                            for(String key : jsonMap.keySet()){
                                resultMap.put(key , jsonMap.get(key).toArray(new String[0]));
                            }
                            changedProduct.properties = resultMap;
                        }
                        changedProduct.carts = product.carts;
                        changedProduct.rating = product.rating;
                        changedProduct.ratingCount = product.ratingCount;
                        if(((ArrayList<String>) map.get("info")).get(0).equals("")){
                            changedProduct.info = product.info;
                        }else {
                            changedProduct.info = ((ArrayList<String>) map.get("info")).toArray(new String[0]);
                        }
                        if(map.get("count").equals("")){
                            changedProduct.count = product.count;
                        }else {
                            changedProduct.count = (String) map.get("count");
                        }
                        Product.deleteProduct(product);
                        Product.addProduct(changedProduct);
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
