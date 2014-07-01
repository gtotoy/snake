/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.server;

import com.healthmarketscience.rmiio.RemoteInputStream;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author Gustavo
 */
public interface ISnakeServer extends Remote {
    public boolean userExists(String username) throws RemoteException;
    public void createUser(String username) throws RemoteException, IOException;
    public void printUsers() throws RemoteException;
    public boolean login(String username) throws RemoteException;
    public void logout(String username) throws RemoteException;
    public void setBoxDirectory(String username, String directory) throws RemoteException;
    public String getBoxDirectory(String username) throws RemoteException;
    public void receiveFile(String username, PathDescriptor descriptor, RemoteInputStream ristream) throws IOException;
}
