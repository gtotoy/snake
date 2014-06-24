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
public class Main {
    public static void main(String[] args) {
        try {
            String url = "rmi://localhost/Server";
            ISnakeServer server = (ISnakeServer) Naming.lookup(url);
            System.out.println(server.getPathDescriptor());
            String username = "gtotoy";
            Path boxDirectory = Paths.get(Settings.client_folder_path);
            //SnakeClient.createUser(server, username);
            SnakeClient.setBoxDirectory(server, username, boxDirectory);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
