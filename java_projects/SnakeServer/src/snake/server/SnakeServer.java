/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Gustavo
 */
public class SnakeServer extends UnicastRemoteObject implements ISnakeServer {
    public SnakeServer() throws RemoteException {
        super();
    }
    
    @Override
    public String sayTo(String text) throws RemoteException {
        return "Hello " + text;
    }

    @Override
    public void copyFile ( String relativePath ) throws RemoteException {
        try {
            FileInputStream fis = new FileInputStream(Settings.server_path + relativePath);
            FileOutputStream fos = new FileOutputStream(Settings.server_path + "copy.txt");
            FileChannel in = fis.getChannel();
            FileChannel out = fos.getChannel();
            in.transferTo(0, in.size(), out);
            out.close();
            in.close();
        } catch (Exception e) {
            
        }
    }
    
    @Override
    public FileInputStream getFIS(String relativePath) throws RemoteException, FileNotFoundException {
        return new FileInputStream(Settings.server_path + relativePath);
    }
    
    
    
    public static void main(String[] args) {
        try {
            Registry reg = LocateRegistry.createRegistry(Settings.port);
            reg.rebind("Server", new SnakeServer());
            System.out.println("Server started");
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
}
