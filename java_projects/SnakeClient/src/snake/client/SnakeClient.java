/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.client;

import java.rmi.Naming;
import snake.server.ISnakeServer;
import java.io.*;
import java.nio.channels.FileChannel;
import snake.server.Settings;

/**
 *
 * @author Gustavo
 */
public class SnakeClient {

    public static void main(String[] args) {
        try {
            ISnakeServer server = (ISnakeServer) Naming.lookup("//localhost/Server");
            System.out.println(server.sayTo("World"));
            /*FileInputStream fis = server.getFIS(Settings.file_relative_path);
            FileOutputStream fos = new FileOutputStream(Settings.client_path + Settings.file_relative_path);
            
            FileChannel in = fis.getChannel();
            FileChannel out = fos.getChannel();
            in.transferTo(0, in.size(), out);
            out.close();
            in.close();*/
            server.copyFile(Settings.file_relative_path);
            FileInputStream fis = server.getFIS(Settings.file_relative_path);
            System.out.println(fis);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
}
