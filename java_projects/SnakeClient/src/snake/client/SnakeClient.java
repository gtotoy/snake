/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.client;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import snake.server.ISnakeServer;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
    
    public static void setBoxDirectory(ISnakeServer server, String username, String directory, String ip) throws RemoteException {
        if (server.userExists(username)) {
            Path boxDirectory = Paths.get(directory);
            Path absoluteBoxDirectory = boxDirectory.toAbsolutePath();
            if ( Files.exists(boxDirectory) ) {
                server.setBoxDirectory(username, absoluteBoxDirectory.toString());
                System.out.println("Box directory (" + absoluteBoxDirectory.toString() + ") saved");
                String[] args = {username, absoluteBoxDirectory.toString(), ip};
                runSnakeSync(args, absoluteBoxDirectory);
            } else {
                System.err.println("Directory (" + absoluteBoxDirectory + ") does not exist");
            }
        } else {
            System.err.println("User " + username + " does not exist");
        }
    }
    
    public static void syncBox(ISnakeServer server, String username, String ip) throws RemoteException, IOException {
        if (server.userExists(username)) {
            String serverBoxDirectory = server.getBoxDirectory(username);
            Path serverBoxPath = Paths.get(serverBoxDirectory);
            Path boxDirectory = Paths.get("").resolve(serverBoxPath.getFileName());
            Path absoluteBoxDirectory = boxDirectory.toAbsolutePath();
            if ( !Files.exists(boxDirectory) ) {
                Files.createDirectory(absoluteBoxDirectory);
                System.out.println("Box directory (" + absoluteBoxDirectory.toString() + ") created");
            }
            String[] args = {username, absoluteBoxDirectory.toString(), ip};
            runSnakeSync(args, absoluteBoxDirectory);
        } else {
            System.err.println("User " + username + " does not exist");
        }
    }
    
    public static void autosyncBox(ISnakeServer server, String username, String ip) throws RemoteException, IOException {
        if (server.userExists(username)) {
            Path boxDirectory = Paths.get("");
            Path absoluteBoxDirectory = boxDirectory.toAbsolutePath();
            System.out.println("Using (" + absoluteBoxDirectory.toString() + ") as box directory");
            String[] args = {username, absoluteBoxDirectory.toString(), ip};
            runSnakeSync(args, absoluteBoxDirectory);
        } else {
            System.err.println("User " + username + " does not exist");
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
            Path log = directory.resolve("snake_sync_log.log");
            pb.redirectErrorStream(true);
            pb.redirectOutput(Redirect.appendTo(log.toFile()));
            Process p = pb.start();
        } catch (IOException e) {
            //System.err.println(e);
            e.printStackTrace();
        }
    }
    
    
}
