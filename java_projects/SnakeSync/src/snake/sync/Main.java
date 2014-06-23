/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.sync;

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
        System.out.println("Start SnakeSync");
        try {
            ISnakeServer server = (ISnakeServer) Naming.lookup("//localhost/Server");
            
        } catch (Exception e) {
            System.err.println(e);
        }
        System.out.println("End SnakeSync");
    }
}
