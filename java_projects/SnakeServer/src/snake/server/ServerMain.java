package snake.server;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author Gustavo
 */
public class ServerMain {
    public static void main(String[] args) {
        if ( args.length < 1 ) {
            System.err.println("Usage: <folder>");
            System.exit(1);
        }
        
        Path parentFolderPath = Paths.get(args[0]);
        Path absoluteParentFolderPath = parentFolderPath.toAbsolutePath();
        if ( Files.exists(parentFolderPath, LinkOption.NOFOLLOW_LINKS) && 
                Files.isDirectory(parentFolderPath, LinkOption.NOFOLLOW_LINKS) ) {
            System.out.println("Snake Server's folder: " + absoluteParentFolderPath);
            try {
                Registry registry = LocateRegistry.createRegistry(Settings.port);
                registry.rebind("Server", new SnakeServer(absoluteParentFolderPath));
                System.out.println("Snake Server is ready");
            } catch (Exception e) {
                System.err.println(e);
            }
        } else {
            System.err.println(absoluteParentFolderPath + " is an invalid folder, check its existence and permissions");
        }
    }
}
