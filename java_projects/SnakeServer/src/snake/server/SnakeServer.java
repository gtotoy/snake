/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    
    @Override
    public List<PathDescriptor> getPathDescriptor() throws RemoteException {
        PathDescriptor d = new PathDescriptor();
        d.relative_path = Paths.get("").toAbsolutePath().toString();
        d.status = PathDescriptor.Status.Deleted;
        d.statusTimePoint = new Date();
        
        List<PathDescriptor> l = new ArrayList<PathDescriptor>();
        l.add(d);
        l.add(d);
        return l;
    }
    
    private Path parentFolder_;
}
