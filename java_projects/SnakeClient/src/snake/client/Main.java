/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.client;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import snake.server.ISnakeServer;

/**
 *
 * @author Gustavo
 */
public class Main {
    public static void main(String[] args) {
        try {
            ISnakeServer server = (ISnakeServer) Naming.lookup("//localhost/Server");
            String username = "gtotoy";
            Path boxDirectory = Paths.get("D:/snake_test/client");
            //SnakeClient.createUser(server, username);
            SnakeClient.setBoxDirectory(server, username, boxDirectory);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
