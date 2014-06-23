/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.server;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Gustavo
 */
public class SnakeServer extends UnicastRemoteObject implements ISnakeServer {
    public SnakeServer( Path parentFolder ) throws RemoteException {
        super();
        parentFolder_ = parentFolder;
    }

    @Override
    public boolean userExists(String username) throws RemoteException {
        Path userFolderPath = parentFolder_.resolve(username);
        return Files.exists(userFolderPath, LinkOption.NOFOLLOW_LINKS) && 
                Files.isDirectory(userFolderPath, LinkOption.NOFOLLOW_LINKS);
    }

    
    
    @Override
    public void createUser(String username) throws RemoteException, IOException {
        Files.createDirectory(parentFolder_.resolve(username));
    }

    @Override
    public boolean login(String username) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setBoxDirectory(Path directory) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    private Path parentFolder_;
}
