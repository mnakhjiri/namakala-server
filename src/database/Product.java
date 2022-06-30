package database;

import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Product implements Serializable {
    public static transient String productsPath = "Products.txt";
    public String name;
    public User seller;
    public String[] images;
    public String price;
    public String[] categories;
    public Map<String , String[]> properties;
    public String rating = "0";
    public int ratingCount = 0;
    public String[] info;
    public String count;
    public String[] favUsers = new String[0];
    public Map<String , String> carts = new HashMap<>();

    public Product(String name, User seller, String[] images, String price, String[] categories, Map<String, String[]> properties, String rating, int ratingCount, String[] info, String count, String[] favUsers, Map<String, String> carts) {
        this.name = name;
        this.seller = seller;
        this.images = images;
        this.price = price;
        this.categories = categories;
        this.properties = properties;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.info = info;
        this.count = count;
        this.favUsers = favUsers;
        this.carts = carts;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", seller=" + seller +
                ", images=" + Arrays.toString(images) +
                ", price='" + price + '\'' +
                ", categories=" + Arrays.toString(categories) +
                ", properties=" + properties +
                ", rating='" + rating + '\'' +
                ", ratingCount=" + ratingCount +
                ", info=" + Arrays.toString(info) +
                ", count='" + count + '\'' +
                ", favUsers=" + Arrays.toString(favUsers) +
                ", carts=" + carts +
                '}';
    }

    public Product() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return ratingCount == product.ratingCount && Objects.equals(name, product.name) && Objects.equals(seller, product.seller) && Arrays.equals(images, product.images) && Objects.equals(price, product.price) && Arrays.equals(categories, product.categories) && Objects.equals(properties, product.properties) && Objects.equals(rating, product.rating) && Arrays.equals(info, product.info) && Objects.equals(count, product.count) && Arrays.equals(favUsers, product.favUsers) && Objects.equals(carts, product.carts);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, seller, price, properties, rating, ratingCount, count, carts);
        result = 31 * result + Arrays.hashCode(images);
        result = 31 * result + Arrays.hashCode(categories);
        result = 31 * result + Arrays.hashCode(info);
        result = 31 * result + Arrays.hashCode(favUsers);
        return result;
    }

    public static synchronized void  deleteProduct(Product product){
        Product[] products = getProducts();
        List<Product> productList= new ArrayList<>();
        Collections.addAll(productList , products);
        productList.removeIf(product1 -> product1.name.equals(product.name));
        try(FileOutputStream fileOutputStream = new FileOutputStream(productsPath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)
        ){
            objectOutputStream.writeObject(productList.toArray(new Product[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized boolean addProduct(String productJson){
        Gson gson = new Gson();
        Product product = gson.fromJson(productJson , Product.class);
        Product[] products = getProducts();
        for(Product fileProduct : products){
            if(fileProduct.name.equals(product.name)){
                return false;
            }
        }
        Product[] result = Arrays.copyOf(products , products.length + 1);
        result[result.length - 1] = product;
        try(FileOutputStream fileOutputStream = new FileOutputStream(productsPath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)
        ){
            objectOutputStream.writeObject(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    public static synchronized boolean addProduct(Product product){
        Product[] products = getProducts();
        for(Product fileProduct : products){
            if(fileProduct.name.equals(product.name)){
                return false;
            }
        }
        Product[] result = Arrays.copyOf(products , products.length + 1);
        result[result.length - 1] = product;
        try(FileOutputStream fileOutputStream = new FileOutputStream(productsPath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)
        ){
            objectOutputStream.writeObject(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    public static synchronized void   addToCart(String phoneNumber , String productName , String count){
        Product[] products = getProducts();
        for(int i = 0 ; i < products.length ; i++){
            if(products[i].name.equals(productName)){
                products[i].carts.put(phoneNumber , count);
            }
        }
        try(FileOutputStream fileOutputStream = new FileOutputStream(productsPath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)
        ){
            objectOutputStream.writeObject(products);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static synchronized Product findProduct(String name){
        Product[] products = getProducts();
        for(Product product : products){
            if(name.equals(product.name)){
                return product;
            }
        }
        return null;
    }
    public static synchronized Product[] findProductByUser(User user){
        Product[] products = getProducts();
        List<Product> result = new ArrayList<>();
        for(Product product : products){
            if(product.seller.equals(user)){
                result.add(product);
            }
        }
        return result.toArray(new Product[0]);
    }

    public static synchronized Product[] getProducts(){
        ArrayList<Product> result = new ArrayList<>();
        try{
            if(!Files.exists(Path.of(productsPath))){
                Files.createFile(Path.of(productsPath));
                return result.toArray(new Product[0]);
            }
            File newFile = new   File(productsPath);
            if (newFile.length() == 0) {
                return result.toArray(new Product[0]);
            }
            try(FileInputStream fileInputStream = new FileInputStream(productsPath);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)){
                Object obj;
                if( (obj = objectInputStream.readObject() ) instanceof Product[]){
                    return  (Product[]) obj;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toArray(new Product[0]);
    }
}
