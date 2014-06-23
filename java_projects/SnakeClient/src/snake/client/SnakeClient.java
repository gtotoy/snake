/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.client;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import snake.server.ISnakeServer;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    
    public static void runSnakeSync(String[] args, Path directory) {
        // Paths.get("") is java_projects/SnakeClient
        Path snakeSyncPath = Paths.get("").resolve("dist/lib").resolve("SnakeSync.jar");
        if ( !(Files.exists(snakeSyncPath.toAbsolutePath(), LinkOption.NOFOLLOW_LINKS) && 
                Files.isRegularFile(snakeSyncPath.toAbsolutePath(), LinkOption.NOFOLLOW_LINKS)) ) {
            // SnakeServer.jar, SnakeClient.jar and SnakeSync.jar are at the same folder
            snakeSyncPath = Paths.get("").resolve("SnakeSync.jar");
        }
        try {
            String[] command = {"java", "-jar", snakeSyncPath.toAbsolutePath().toString()};
            List<String> commands = new ArrayList(Arrays.asList(command));
            commands.addAll(Arrays.asList(args));
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.directory(directory.toFile());
            Path log = directory.resolve("snake_sync_log.txt");
            pb.redirectErrorStream(true);
            pb.redirectOutput(Redirect.appendTo(log.toFile()));
            Process p = pb.start();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    public static void setBoxDirectory(ISnakeServer server, String username, Path directory) throws RemoteException {
        String[] args = {username, directory.toAbsolutePath().toString()};
        runSnakeSync(args, directory);
    }
}
