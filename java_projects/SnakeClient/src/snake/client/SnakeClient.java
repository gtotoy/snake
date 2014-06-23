/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.client;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import snake.server.ISnakeServer;
import java.rmi.RemoteException;
import java.util.Map;

/**
 *
 * @author Gustavo
 */
public class SnakeClient {
    
    public static void createUser(ISnakeServer server, String username) throws RemoteException {
        if (!server.userExists(username)) {
            try {
                server.createUser(username);
                System.out.println("User " + username + " created succesfully");
            } catch (IOException e) {
                System.err.println(e);
            }
        } else {
            System.err.println("User not created, " + username + " alreade exists");
        }
    }
    
    public static void setBoxDirectory(ISnakeServer server, String username, Path directory) throws RemoteException {
        try {
            ProcessBuilder pb = new ProcessBuilder("java -jar SnakeSync.jar", username, directory.toAbsolutePath().toString());
            pb.directory(directory.toFile());
            File log = new File("log.txt");
            pb.redirectErrorStream(true);
            pb.redirectOutput(Redirect.appendTo(log));
            Process p = pb.start();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
