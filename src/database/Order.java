package database;

import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class Order implements  Serializable {
    public static transient String ordersPath = "orders.txt";
    public Date date = new Date();
    public int price = 0;
    public String address;
    public String userPhone;

    public Order(Date date, int price, String address, String userPhone) {
        this.date = date;
        this.price = price;
        this.address = address;
        this.userPhone = userPhone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return price == order.price && Objects.equals(date, order.date) && Objects.equals(address, order.address) && Objects.equals(userPhone, order.userPhone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, price, address, userPhone);
    }

    public static synchronized boolean addOrder(String orderJson){
        Gson gson = new Gson();
        Order order = gson.fromJson(orderJson , Order.class);
        Order[] orders = getOrders();
        Order[] result = Arrays.copyOf(orders , orders.length + 1);
        result[result.length - 1] = order;
        try(FileOutputStream fileOutputStream = new FileOutputStream(ordersPath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)
        ){
            objectOutputStream.writeObject(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static synchronized Order[] getOrders(){
        ArrayList<Order> result = new ArrayList<>();
        try{
            if(!Files.exists(Path.of(ordersPath))){
                Files.createFile(Path.of(ordersPath));
                return result.toArray(new Order[0]);
            }
            File newFile = new   File(ordersPath);
            if (newFile.length() == 0) {
                return result.toArray(new Order[0]);
            }
            try(FileInputStream fileInputStream = new FileInputStream(ordersPath);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)){
                Object obj;
                if( (obj = objectInputStream.readObject() ) instanceof Order[]){
                    return  (Order[]) obj;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toArray(new Order[0]);
    }
}
