/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.server;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    public void receiveFile(String username, PathDescriptor descriptor, RemoteInputStream ristream) throws IOException {
        InputStream istream = RemoteInputStreamClient.wrap(ristream);
        FileOutputStream ostream = null;
        try {
          Path filePath = parentFolder_.resolve(username).resolve(descriptor.relative_path);
          File file = filePath.toFile();
          file.getParentFile().mkdirs();
          file.createNewFile();
          ostream = new FileOutputStream(file);
          byte[] buffer = new byte[1024];
          int bytesRead = 0;
          while((bytesRead = istream.read(buffer)) >= 0) {
            ostream.write(buffer, 0, bytesRead);
          }
          ostream.flush();
        } finally {
          try {
            if(istream != null) {
              istream.close();
            }
          } finally {
            if(ostream != null) {
              ostream.close();
            }
          }
        }
    }
    
    private Path parentFolder_;
    private Map<String, User> loggedUsers_;
    private Map<String, User> users_;
}
