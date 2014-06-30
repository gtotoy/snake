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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Gustavo
 */
public class SnakeServer extends UnicastRemoteObject implements ISnakeServer {
    public SnakeServer( Path parentFolder ) throws RemoteException {
        super();
        parentFolder_ = parentFolder;
        users_ = new HashMap<String, User>();
        loggedUsers_ = new HashMap<String, User>();
    }

    @Override
    public boolean userExists(String username) throws RemoteException {
        return users_.containsKey(username);
    }

    
    
    @Override
    public void createUser(String username) throws RemoteException, IOException {
        User user = new User();
        user.username = username;
        users_.put(username, user);
        Path userDirectory = parentFolder_.resolve(username);
        if (!Files.exists(userDirectory)) Files.createDirectory(userDirectory);
    }

    @Override
    public void printUsers() throws RemoteException {
        System.out.println(users_.toString());
    }
    
    @Override
    public boolean login(String username) throws RemoteException {
        User user = users_.get(username);
        if (user != null) {
            loggedUsers_.put(user.username, user);
            return true;
        }
        return false;
    }
    
    @Override
    public void logout(String username) throws RemoteException {
        loggedUsers_.remove(username);
    }
    
    @Override
    public void setBoxDirectory(String username, String directory) throws RemoteException {
        User user = users_.get(username);
        if (user != null) {
            user.boxDirectory = directory;
        }
    }
    
    @Override
    public String getBoxDirectory(String username) throws RemoteException {
        User user = users_.get(username);
        if (user != null) {
            return user.boxDirectory;
        }
        return null;
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
    private Map<String, User> loggedUsers_;
    private Map<String, User> users_;
}
