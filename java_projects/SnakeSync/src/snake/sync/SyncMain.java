/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.sync;

import java.io.File;
import java.rmi.Naming;
import java.util.Calendar;
import snake.server.ISnakeServer;

/**
 *
 * @author Gustavo
 */
public class SyncMain {
    public static void main(String[] args) {
        System.out.println("Start SnakeSync -------------------- " + Calendar.getInstance().getTime());
        System.out.println("\n");
        try {
            ISnakeServer server = (ISnakeServer) Naming.lookup("//localhost/Server");
            String username = args[0];
            File boxDirectory = new File(args[1]);
            System.out.println("username: " + username);
            System.out.println("box directory: " + boxDirectory.toString());
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
