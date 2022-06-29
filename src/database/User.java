package database;

import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class User implements Serializable {
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", mail='" + mail + '\'' +
                ", pass='" + pass + '\'' +
                '}';
    }

    public static transient String usersPath = "Users.txt";
    public String name;
    public String phoneNumber;
    public String mail;
    public String pass;
    //check
    public String img;

    public User(String name, String phoneNumber, String mail, String pass, String img) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.mail = mail;
        this.pass = pass;
        this.img = img;
    }

    public User() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) && Objects.equals(phoneNumber, user.phoneNumber) && Objects.equals(mail, user.mail) && Objects.equals(pass, user.pass) && Objects.equals(img, user.img);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phoneNumber, mail, pass, img);
    }

    public static synchronized void  deleteUser(User user){
        User[] users = getUsers();
        List<User> usersList = new ArrayList<>();
        Collections.addAll(usersList , users);
        usersList.remove(user);
        try(FileOutputStream fileOutputStream = new FileOutputStream(usersPath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)
        ){
            objectOutputStream.writeObject(usersList.toArray(new User[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized boolean addUser(String userJson){
        Gson gson = new Gson();
        User user = gson.fromJson(userJson , User.class);
        User[] users = getUsers();
        for(User fileUser : users){
            if(fileUser.phoneNumber.equals(user.phoneNumber)){
                return false;
            }
        }
        User[] result = Arrays.copyOf(users , users.length + 1);
        result[result.length - 1] = user;
        try(FileOutputStream fileOutputStream = new FileOutputStream(usersPath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)
        ){
            objectOutputStream.writeObject(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    public static synchronized User findUser(String number , String pass){
        User[] users = getUsers();
        for(User user : users){
            if(number.equals(user.phoneNumber) && pass.equals(user.pass)){
                return user;
            }
        }
        return null;
    }
    public static synchronized User findUser(String number){
        User[] users = getUsers();
        for(User user : users){
            if(number.equals(user.phoneNumber)){
                return user;
            }
        }
        return null;
    }
    public static synchronized User[] getUsers(){
        ArrayList<User> result = new ArrayList<>();
        try{
            if(!Files.exists(Path.of(usersPath))){
                Files.createFile(Path.of(usersPath));
                return result.toArray(new User[0]);
            }
            File newFile = new   File(usersPath);
            if (newFile.length() == 0) {
                return result.toArray(new User[0]);
            }
            try(FileInputStream fileInputStream = new FileInputStream(usersPath);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)){
                Object obj;
                if( (obj = objectInputStream.readObject() ) instanceof User[]){
                    return  (User[]) obj;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toArray(new User[0]);
    }
}
