/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.*;

/**
 *
 * @author Gustavo
 */
public interface ISnakeServer extends Remote {
    public String sayTo ( String text ) throws RemoteException;
    public void copyFile ( String relativePath ) throws RemoteException;
    public FileInputStream getFIS ( String relativePath ) throws RemoteException, FileNotFoundException;
}
