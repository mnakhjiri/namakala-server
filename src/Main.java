import network.ClientHandler;
import network.RegisterHandler;

public class Main {
    public static void main(String[] args) {
        new Thread(new RegisterHandler()).start();
        new Thread(new ClientHandler()).start();
    }
}
