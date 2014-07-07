/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.server;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.nio.file.Path;
import java.util.ArrayList;
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
    public RemoteInputStream sendFile(String username, PathDescriptor descriptor) throws IOException;
    public void deleteFile(String username, PathDescriptor descriptor) throws IOException;
    public ArrayList<PathDescriptor> getRelativeDescriptors(String username) throws RemoteException;
    public void startPull(String username) throws RemoteException;
    public void endPull(String username) throws RemoteException;
    public void startPush(String username) throws RemoteException;
    public void endPush(String username) throws RemoteException;
}
