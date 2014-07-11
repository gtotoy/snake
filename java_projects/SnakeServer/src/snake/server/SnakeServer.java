/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snake.server;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author Gustavo
 */
public class SnakeServer extends UnicastRemoteObject implements ISnakeServer {
    public SnakeServer( Path parentFolder ) throws RemoteException {
        super();
        parentFolder_ = parentFolder;
        users_ = new HashMap<String, User>();
        descriptors_ = new HashMap<String, ArrayList<PathDescriptor>>();
        try {
            config_ = parentFolder_.resolve(Settings.config);
            if (!Files.exists(config_)) Files.createDirectory(config_);
            usersPath_ = config_.resolve(Settings.users);
            if (Files.exists(usersPath_)) loadUsers();
            descriptorsPath_ = config_.resolve(Settings.usersPathDescriptors);
            if (Files.exists(descriptorsPath_)) loadDescriptors();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
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
        descriptors_.put(username, PathUtils.getRelativePathDescriptors(userDirectory.toFile(), true));
        saveUsers();
    }

    @Override
    public void printUsers() throws RemoteException {
        System.out.println(users_.toString());
    }
    
    @Override
    public void setBoxDirectory(String username, String directory) throws RemoteException {
        User user = users_.get(username);
        if (user != null) {
            user.boxDirectory = directory;
            saveUsers();
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
    public RemoteInputStream sendFile(String username, PathDescriptor descriptor) throws IOException {
         SimpleRemoteInputStream istream = new SimpleRemoteInputStream(
                        new FileInputStream(parentFolder_.resolve(username).resolve(descriptor.relative_path).toFile()));
         return istream.export();
    }
    
    @Override
    public void receiveFile(String username, PathDescriptor descriptor, RemoteInputStream ristream) throws IOException {
        PathUtils.receiveFile(parentFolder_.resolve(username), getRelativeDescriptors(username), descriptor, ristream);
    }
    
    @Override
    public void deleteFile(String username, PathDescriptor descriptor) throws IOException {
        PathUtils.deleteFile(parentFolder_.resolve(username), getRelativeDescriptors(username), descriptor.relative_path);
    }
    
    @Override
    public ArrayList<PathDescriptor> getRelativeDescriptors(String username) throws RemoteException {
        return descriptors_.get(username);
    }

    @Override
    public void startPull(String username) throws RemoteException {
        System.out.println("Start Pull: " + username);
        System.out.println(getRelativeDescriptors(username).toString());
    }

    @Override
    public void endPull(String username) throws RemoteException {
        System.out.println("End Pull: " + username);
        System.out.println(getRelativeDescriptors(username).toString());
    }
    
    @Override
    public void startPush(String username) throws RemoteException {
        System.out.println("Start Push: " + username);
        System.out.println(getRelativeDescriptors(username).toString());
    }
    
    @Override
    public void endPush(String username) throws RemoteException {
        System.out.println("End Push: " + username);
        System.out.println(getRelativeDescriptors(username).toString());
        saveDescriptors();
    }
    
    @Override
    public ReentrantReadWriteLock.ReadLock readLock() throws RemoteException {
        return lock_.readLock();
    }
    
    @Override
    public ReentrantReadWriteLock.WriteLock writeLock() throws RemoteException {
        return lock_.writeLock();
    }
    
    private void saveUsers () {
        try {
           FileOutputStream fileOut = new FileOutputStream(usersPath_.toFile());
           ObjectOutputStream out = new ObjectOutputStream(fileOut);
           out.writeObject(users_);
           out.close();
           fileOut.close();
        } catch(IOException i) {
            System.out.println(i.toString());
        }
    }
    
    private void loadUsers () {
        try {
         FileInputStream fileIn = new FileInputStream(usersPath_.toFile());
         ObjectInputStream in = new ObjectInputStream(fileIn);
         users_ = (HashMap<String, User>) in.readObject();
         in.close();
         fileIn.close();
         System.out.println(users_.toString());
      } catch(IOException i) {
         System.out.println(i.toString());
         return;
      } catch(ClassNotFoundException c) {
         System.out.println("Class not found");
         c.printStackTrace();
         return;
      }
    }
    
    private void saveDescriptors () {
        try {
           FileOutputStream fileOut = new FileOutputStream(descriptorsPath_.toFile());
           ObjectOutputStream out = new ObjectOutputStream(fileOut);
           out.writeObject(descriptors_);
           out.close();
           fileOut.close();
        } catch(IOException i) {
            System.out.println(i.toString());
        }
    }
    
    private void loadDescriptors () {
        try {
            FileInputStream fileIn = new FileInputStream(descriptorsPath_.toFile());
            ObjectInputStream in = new ObjectInputStream(fileIn);
            descriptors_ = (HashMap<String, ArrayList<PathDescriptor>>) in.readObject();
            in.close();
            fileIn.close();
            System.out.println(descriptors_.toString());
        
            for (String username : descriptors_.keySet()) {
                Path path = parentFolder_.resolve(username);
                ArrayList<PathDescriptor> newDescriptors = PathUtils.getRelativePathDescriptors(path.toFile(), true);
                PathDescriptor.updateDescriptors(descriptors_.get(username), newDescriptors);
            }
      } catch(IOException i) {
         System.out.println(i.toString());
         return;
      } catch(ClassNotFoundException c) {
         System.out.println("Class not found");
         c.printStackTrace();
         return;
      }
    }
    
    private Path parentFolder_;
    private Path config_;
    private Path usersPath_;
    private Path descriptorsPath_;
    private Map<String, User> users_;
    private Map<String, ArrayList<PathDescriptor>> descriptors_;
    private final ReentrantReadWriteLock lock_ = new ReentrantReadWriteLock();
}
