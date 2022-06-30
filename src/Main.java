import network.ClientHandler;
import network.LoginHandler;
import network.ProductView;
import network.RegisterHandler;

public class Main {
    public static void main(String[] args) {
        new Thread(new RegisterHandler()).start();
        new Thread(new ClientHandler()).start();
        new Thread(new LoginHandler()).start();
        new Thread(new ProductView()).start();
    }
}
