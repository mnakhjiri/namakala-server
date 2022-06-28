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
    public User(String name, String phoneNumber, String mail, String pass) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.mail = mail;
        this.pass = pass;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return phoneNumber == user.phoneNumber && Objects.equals(name, user.name) && Objects.equals(mail, user.mail) && Objects.equals(pass, user.pass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phoneNumber, mail, pass);
    }

    public static boolean addUser(String userJson){
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

    public static User[] getUsers(){
        ArrayList<User> result = new ArrayList<>();
        try{
            if(!Files.exists(Path.of(usersPath))){
                Files.createFile(Path.of(usersPath));
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
