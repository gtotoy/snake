/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.sync;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.Calendar;
import snake.server.ISnakeServer;
import snake.server.PathDescriptor;
import snake.server.PathUtils;
import snake.server.Settings;

/**
 *
 * @author Gustavo
 */
public class SyncMain {
    public static void main(String[] args) {
        System.out.println("\n");
        System.out.println("Start SnakeSync -------------------- " + Calendar.getInstance().getTime());
        System.out.println("");
        try {
            ISnakeServer server = (ISnakeServer) Naming.lookup("//localhost/Server");
            String username = args[0];
            Path boxDirectory = Paths.get(args[1]);
            System.out.println("username: " + username);
            System.out.println("box directory: " + boxDirectory.toString());
            SyncSnake sync = new SyncSnake(server, username, boxDirectory);
            for (;;) {
                System.out.println("Start Sync " + Calendar.getInstance().getTime());
                sync.pull();
                sync.push();
                Thread.sleep(Settings.syncWithServerSeconds * 1000);
            }
            /*
            for ( PathDescriptor desc : PathUtils.getRelativePathDescriptors(boxDirectory.toFile()) ) {
                SimpleRemoteInputStream istream = new SimpleRemoteInputStream(
                    new FileInputStream(boxDirectory.resolve(desc.relative_path).toFile()));
                try {
                  server.receiveFile(username, desc, istream.export());
                } finally {
                  istream.close();
                }
            }*/
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
