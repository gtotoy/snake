package snake.client;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import snake.server.ISnakeServer;
import snake.server.Settings;

/**
 *
 * @author Gustavo
 */
public class ClientMain {
    public static void main(String[] args) {
        try {
            String url = "rmi://localhost/Server";
            ISnakeServer server = (ISnakeServer) Naming.lookup(url);
            String username = "gtotoy";
            SnakeClient.createUser(server, username);
            SnakeClient.setBoxDirectory(server, username, Settings.client_folder_path);
            //server.printUsers();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}